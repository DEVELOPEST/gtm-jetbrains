package ee.developest.gtm

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import java.util.*

object Util {
    fun getProject(document: Document): Optional<Project> {
        val editors = EditorFactory.getInstance().getEditors(document)
        return if (editors.isNotEmpty()) Optional.ofNullable(editors[0].project) else Optional.empty()
    }
}