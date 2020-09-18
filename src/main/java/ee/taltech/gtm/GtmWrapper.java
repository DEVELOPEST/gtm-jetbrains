package ee.taltech.gtm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GtmWrapper {

    public enum AppEventType {
        RUN("run"),
        DEBUG("debug");

        AppEventType(String command) {
            this.command = command;
        }

        private final String command;

        public String getCommand() {
            return command;
        }
    }

    private static final Long RECORD_MIN_THRESHOLD = 10000L; // 10 seconds
    private static final String RECORD_COMMAND = "record";
    private static final String VERIFY_COMMAND = "verify";
    private static final String STATUS_OPTION = "--status";

    private static String gtmExePath = null;
    private static boolean gtmExeFound = false;

    private static String lastRecordPath = null;
    private static Long lastRecordTime = null;
    private static Long lastRunTime = null;

    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private static Future recordTask;
    private static Long MAX_RUN_TIME = 2000L; // 2 seconds

    private static GtmWrapper instance = null;

    public GtmWrapper() {
        initGtmExePath();
    }

    public static GtmWrapper getInstance() {
        if (instance == null) instance = new GtmWrapper();
        return instance;
    }

    private static Boolean initGtmExePath() {
        String gtmExeName = System.getProperty("os.name").startsWith("Windows") ? "gtm.exe" : "gtm";
        String[] gtmPath;
        StringBuilder pathVar = new StringBuilder(System.getenv("PATH"));

        if (System.getProperty("os.name").startsWith("Windows")) {
            // Setup an additional Windows user path
            String userWinBin = System.getProperty("user.home") + File.separator + "gtm";
            gtmPath = new String[]{
                    Paths.get(System.getenv("ProgramFiles"), "gtm").toString(),
                    Paths.get(System.getenv("ProgramFiles(x86)"), "gtm").toString(),
                    userWinBin};
        } else {
            // Setup additional common *nix user paths
            String userBin = System.getProperty("user.home") + File.separator + "bin";
            String userLocalBin = System.getProperty("user.home") + File.separator + "local" + File.separator + "bin";
            gtmPath = new String[]{"/usr/bin", "/bin", "/usr/sbin", "/sbin", "/usr/local/bin/", userBin, userLocalBin};
        }

        for (String aGtmPath : gtmPath) {
            if (!pathVar.toString().contains(aGtmPath)) {
                pathVar.append(File.pathSeparator).append(aGtmPath);
            }
        }

        String result = null;
        String[] pathDirs = pathVar.toString().split(File.pathSeparator);
        for (String pathDir : pathDirs) {
            File exeFile = Paths.get(pathDir).resolve(gtmExeName).toFile();
            if (exeFile.getAbsoluteFile().exists() && exeFile.getAbsoluteFile().canExecute()) {
                result = exeFile.getAbsolutePath();
                break;
            }
        }
        gtmExeFound = (result != null);
        gtmExePath = result;
        if (!gtmExeFound) {
            System.out.println("Unable to find executable gtm in PATH");
        }
        return gtmExeFound;
    }

    public void recordFile(Project project, VirtualFile file) {
        Runnable r = () -> runRecord(file.getPath(), project);
        submitRecord(r);
    }

    protected void runRecord(AppEventType type, Project project) {
        runRecord(type.getCommand(), project);;
    }

    protected void runRecord(String args, Project project) {
        if (StringUtils.isBlank(args)) return;
        if (!gtmExeFound) {
            return;
        }
        try {
            Long currentTime = System.currentTimeMillis();
            if (Objects.equals(lastRecordPath, args)) {
                if (lastRecordTime != null && currentTime - lastRecordTime <= RECORD_MIN_THRESHOLD) {
                    return;
                }
            }
            lastRecordPath = args;
            lastRecordTime = currentTime;

            Process process = new ProcessBuilder(gtmExePath, RECORD_COMMAND, args).start();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private static synchronized void submitRecord(Runnable r) {
        if (recordTask != null && !recordTask.isDone()) {
            // make sure it's not a hung process
            if (lastRunTime != null && System.currentTimeMillis() - lastRunTime > MAX_RUN_TIME) {
                // process is hung, cancel it
                recordTask.cancel(true);
                System.out.println("Record task cancelled");
            }
        }
        recordTask = executor.submit(r);
        lastRunTime = System.currentTimeMillis();
    }
}
