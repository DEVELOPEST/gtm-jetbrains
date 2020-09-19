package ee.taltech.gtm.listener;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import ee.taltech.gtm.GtmWrapper;
import ee.taltech.gtm.Util;
import org.jetbrains.annotations.NotNull;

public class CustomSaveListener implements FileDocumentManagerListener {

    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        FileDocumentManager instance = FileDocumentManager.getInstance();
        VirtualFile file = instance.getFile(document);
        var project = Util.getProject(document);
        project.ifPresent(p -> GtmWrapper.getInstance().recordFile(p, file));
    }
}
