package ee.taltech.gtm

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
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
        private const val STATUS_OPTION = "--status"
        private const val TOTAL_ONLY_OPTION = "--total-only"
        private const val ALL_OPTION = "--all"
        private const val CWD_OPTION = "--cwd"

        private var gtmExePath: String? = null
        private var gtmExeFound = false
        private var lastRecordPath: String? = null
        private var lastRecordTime: Long? = null
        private var lastRunTime: Long? = null
        private val executor = Executors.newSingleThreadExecutor()
        private var recordTask: Future<*>? = null
        private const val MAX_RUN_TIME = 2000L // 2 seconds
        val instance: GtmWrapper = GtmWrapper()

        private fun initGtmExePath(): Boolean {
            val gtmExeName = if (System.getProperty("os.name").startsWith("Windows")) "gtm.exe" else "gtm"
            val gtmPath: Array<String>
            val pathVar = StringBuilder(System.getenv("PATH"))
            gtmPath = if (System.getProperty("os.name").startsWith("Windows")) {
                // Setup an additional Windows user path
                val userWinBin = System.getProperty("user.home") + File.separator + "gtm"
                arrayOf(
                        Paths.get(System.getenv("ProgramFiles"), "gtm").toString(),
                        Paths.get(System.getenv("ProgramFiles(x86)"), "gtm").toString(),
                        userWinBin)
            } else {
                // Setup additional common *nix user paths
                val userBin = System.getProperty("user.home") + File.separator + "bin"
                val userLocalBin = System.getProperty("user.home") + File.separator + "local" + File.separator + "bin"
                arrayOf("/usr/bin", "/bin", "/usr/sbin", "/sbin", "/usr/local/bin/", userBin, userLocalBin)
            }
            for (aGtmPath in gtmPath) {
                if (!pathVar.toString().contains(aGtmPath)) {
                    pathVar.append(File.pathSeparator).append(aGtmPath)
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

        init {
            initGtmExePath()
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
    }

    fun recordFile(project: Project?, file: VirtualFile) {
        val r = Runnable { runRecord(project, file.path) }
        submitRecord(r)
    }

    fun recordEvent(project: Project, eventName: String?, event: AppEventType) {
        val r = Runnable { runRecord(event, eventName, project) }
        submitRecord(r)
    }

    protected fun runRecord(type: AppEventType, eventName: String?, project: Project) {
        runRecord(project, CWD_OPTION, project.basePath!!, type.command, eventName!!)
    }

    protected fun runRecord(project: Project?, vararg args: String) {
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

    fun checkHours(project: Project) {
        if (gtmExeFound) {
            val process = ProcessBuilder(gtmExePath,
                    STATUS_COMMAND,
                    CWD_OPTION,
                    project.basePath!!,
                    TOTAL_ONLY_OPTION
            ).start()
            val status = readOutput(process)
            GTMStatusWidget.instance.setTimeSpent(status)
        }
    }

    private fun readOutput(process: Process): String {
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val builder = StringBuilder()
        var line: String?
        while (null != reader.readLine().also { line = it }) {
            builder.append(line)
        }
        return builder.toString()
    }
}