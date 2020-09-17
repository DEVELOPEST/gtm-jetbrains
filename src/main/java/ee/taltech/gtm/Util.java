package ee.taltech.gtm;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;

import java.util.Optional;

public class Util {
    public static Optional<Project> getProject(Document document) {
        Editor[] editors = EditorFactory.getInstance().getEditors(document);
        return editors.length > 0 ? Optional.ofNullable(editors[0].getProject()) : Optional.empty();
    }
}
