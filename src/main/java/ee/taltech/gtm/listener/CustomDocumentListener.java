package ee.taltech.gtm.listener;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import ee.taltech.gtm.GtmWrapper;
import ee.taltech.gtm.Util;
import org.jetbrains.annotations.NotNull;

public class CustomDocumentListener implements DocumentListener {

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
        Document document = event.getDocument();
        FileDocumentManager instance = FileDocumentManager.getInstance();
        VirtualFile file = instance.getFile(document);
        var project = Util.getProject(document);
        project.ifPresent(p -> GtmWrapper.getInstance().recordFile(p, file));
    }
}
