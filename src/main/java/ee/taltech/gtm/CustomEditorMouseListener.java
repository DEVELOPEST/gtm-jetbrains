package ee.taltech.gtm;

import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class CustomEditorMouseListener implements EditorMouseListener {

    @Override
    public void mousePressed(@NotNull EditorMouseEvent event) {
        FileDocumentManager instance = FileDocumentManager.getInstance();
        VirtualFile file = instance.getFile(event.getEditor().getDocument());
        Project project = event.getEditor().getProject();
        // TODO: Send event
    }
}
