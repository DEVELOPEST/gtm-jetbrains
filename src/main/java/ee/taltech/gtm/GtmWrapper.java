package ee.taltech.gtm;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

public class GtmWrapper {
    private static final Long RECORD_MIN_THRESHOLD = 30000L; // 30 seconds
    private static final String RECORD_COMMAND = "record";

    private static String gtmExePath = null;
    private static boolean gtmExeFound = false;

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

    }
}
