package ee.taltech.gtm.listener;

import com.intellij.openapi.editor.event.VisibleAreaEvent;
import com.intellij.openapi.editor.event.VisibleAreaListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class CustomVisibleAreaListener implements VisibleAreaListener {

    @Override
    public void visibleAreaChanged(@NotNull VisibleAreaEvent e) {
        FileDocumentManager instance = FileDocumentManager.getInstance();
        VirtualFile file = instance.getFile(e.getEditor().getDocument());
        Project project = e.getEditor().getProject();
        // TODO: dispatch event, or delete this"
    }
}
