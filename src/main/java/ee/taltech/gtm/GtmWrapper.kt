package ee.taltech.gtm

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import ee.taltech.gtm.popup.PopupFactory
import ee.taltech.gtm.service.ConfigService
import ee.taltech.gtm.widget.GTMStatusWidget
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future


class GtmWrapper {
    companion object {
        private const val RECORD_MIN_THRESHOLD = 10000L // 10 seconds
        private const val RECORD_COMMAND = "record"
        private const val VERIFY_COMMAND = "verify"
        private const val STATUS_COMMAND = "status"
        private const val INIT_COMMAND = "init"
        private const val STATUS_OPTION = "--status"
        private const val TOTAL_ONLY_OPTION = "--total-only"
        private const val VERSION_OPTION = "--version"

        private const val ALL_OPTION = "--all"
        private const val CWD_OPTION = "--cwd"
        private const val INIT_FAIL = "unable to initialize"

        private const val GTM_MIN_VERSION = "0.1.0"

        private var gtmExePath: String? = null
        private var gtmExeFound = false
        private var lastRecordPath: String? = null
        private var lastRecordTime: Long? = null
        private var lastRunTime: Long? = null
        private val executor = Executors.newSingleThreadExecutor()
        private var recordTask: Future<*>? = null
        private const val MAX_RUN_TIME = 2000L // 2 seconds
        private var configService: ConfigService? = null
        val instance: GtmWrapper = GtmWrapper()
    }

    init {
        initGtmExePath()
        if (gtmExeFound) {
            checkGtmVersion()
        }
    }

    private fun initGtmExePath(): Boolean {
        val gtmExeName = if (System.getProperty("os.name").startsWith("Windows")) "gtm.exe" else "gtm"
        val gtmPath: Array<String>
        val pathVar = StringBuilder(System.getenv("PATH"))
        gtmPath = if (System.getProperty("os.name").startsWith("Windows")) {
            // Setup an additional Windows user path
            val userWinBin = "${System.getProperty("user.home")}${File.separator}gtm"
            arrayOf(
                    Paths.get(System.getenv("ProgramFiles"), "gtm").toString(),
                    Paths.get(System.getenv("ProgramFiles(x86)"), "gtm").toString(),
                    userWinBin)
        } else {
            // Setup additional common *nix user paths
            val userBin = "${System.getProperty("user.home")}${File.separator}bin"
            val userLocalBin = "${System.getProperty("user.home")}${File.separator}local${File.separator}bin"
            arrayOf("/usr/bin", "/bin", "/usr/sbin", "/sbin", "/usr/local/bin/", userBin, userLocalBin)
        }
        gtmPath.forEach { path ->
            if (!pathVar.toString().contains(path)) {
                pathVar.append(File.pathSeparator).append(path)
            }
        }
        var result: String? = null
        val pathDirs = pathVar.toString().split(File.pathSeparator).toTypedArray()
        for (pathDir in pathDirs) {
            val exeFile = Paths.get(pathDir).resolve(gtmExeName).toFile()
            if (exeFile.absoluteFile.exists() && exeFile.absoluteFile.canExecute()) {
                result = exeFile.absolutePath
                break
            }
        }
        gtmExeFound = result != null
        gtmExePath = result
        if (!gtmExeFound) {
            println("Unable to find executable gtm in PATH")
        }
        return gtmExeFound
    }

    private fun checkGtmVersion() {
        val process = Runtime.getRuntime().exec("$gtmExePath --version")
        val version = readOutput(process).split(".")
        val minVersion = GTM_MIN_VERSION.split(".")
        var isUpToDate: Boolean? = null
        if (version.size >= minVersion.size) {
            minVersion.zip(version)
                    .forEach { (min, curr) ->
                        if (isUpToDate == null) {
                            isUpToDate = if (curr != min) curr.toInt() > min.toInt() else null
                        }
                    }
        } else {
            isUpToDate = false
        }

        if (isUpToDate == false) {
            PopupFactory.showInfoMessage("Gtm",
                    "Gtm-enhanced core is not updated to latest and may cause some functionality not to work\n"
                            + "Latest version can be downloaded from https://github.com/kilpkonn/gtm-enhanced/releases")
        }
    }

    @Synchronized
    private fun submitRecord(r: Runnable) {
        if (recordTask != null && !recordTask!!.isDone) {
            // make sure it's not a hung process
            if (lastRunTime != null && System.currentTimeMillis() - lastRunTime!! > MAX_RUN_TIME) {
                // process is hung, cancel it
                recordTask!!.cancel(true)
                println("Record task cancelled")
            }
        }
        recordTask = executor.submit(r)
        lastRunTime = System.currentTimeMillis()
    }

    fun recordFile(project: Project?, file: VirtualFile) {
        val r = Runnable { runRecord(project, file.path) }
        submitRecord(r)
    }

    fun recordEvent(project: Project, eventName: String) {
        val r = Runnable { runRecord(eventName, project) }
        submitRecord(r)
    }

    private fun runRecord(eventName: String, project: Project) {
        runRecord(project, CWD_OPTION, project.basePath!!, "--app", eventName)
    }

    private fun runRecord(project: Project?, vararg args: String) {
        if (args.isEmpty() || Arrays.stream(args).anyMatch { obj: String -> obj.isBlank() }) return
        if (!gtmExeFound) {
            return
        }
        try {
            val currentTime = System.currentTimeMillis()
            if (lastRecordPath == args.joinToString(separator = "-")) {
                if (lastRecordTime != null && currentTime - lastRecordTime!! <= RECORD_MIN_THRESHOLD) {
                    return
                }
            }
            lastRecordPath = java.lang.String.join("-", *args)
            lastRecordTime = currentTime
            val process = ProcessBuilder(gtmExePath, RECORD_COMMAND, STATUS_OPTION, *args).start()
            val status = readOutput(process)
            GTMStatusWidget.instance.setTimeSpent(status)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun checkHours(project: Project, retries: Int = 1) {
        configService = project.getService(ConfigService::class.java)
        if (!gtmExeFound) return

        val process = ProcessBuilder(gtmExePath,
                STATUS_COMMAND,
                CWD_OPTION,
                project.basePath!!,
                TOTAL_ONLY_OPTION
        ).start()
        val status = readOutput(process)
        if (status.toLowerCase().contains("not initialized") && configService?.state?.isGtmDisabled != true) {
            PopupFactory.showInitConfirmation(project, {
                if (initGtm(project)) {
                    PopupFactory.showInfoNotification("Gtm", "Successfully initialized gtm time tracking")
                    if (retries > 0) checkHours(project, retries - 1)
                }
            }, {
                PopupFactory.showInfoNotification("Gtm", "You can later init time tracking via `gtm init`")
                configService?.setGtmDisabled(true)
            })
        } else {
            GTMStatusWidget.instance.setTimeSpent(status)
        }

    }

    private fun initGtm(project: Project): Boolean {
        val process = ProcessBuilder(gtmExePath, INIT_COMMAND, CWD_OPTION, project.basePath).start();
        val status = readOutput(process)
        val success = !status.toLowerCase().contains(INIT_FAIL)
        if (!success) {
            PopupFactory.showErrorNotification("Gtm", status)
        }
        return success
    }

    private fun readOutput(process: Process): String {
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val builder = StringBuilder()
        var line: String?
        while (null != reader.readLine().also { line = it }) {
            builder.append(line)
        }
        process.inputStream.close()
        return builder.toString()
    }
}