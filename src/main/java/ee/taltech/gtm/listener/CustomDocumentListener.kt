package ee.taltech.gtm.listener

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import ee.taltech.gtm.GtmWrapper
import ee.taltech.gtm.Util

class CustomDocumentListener : DocumentListener {
    override fun documentChanged(event: DocumentEvent) {
        val document = event.document
        val instance = FileDocumentManager.getInstance()
        val file = instance.getFile(document) ?: return
        val project = Util.getProject(document)
        project.ifPresent { p: Project? -> GtmWrapper.instance.recordFile(p, file) }
    }


}