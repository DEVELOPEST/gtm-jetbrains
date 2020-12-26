package ee.developest.gtm.listener

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.Project
import ee.developest.gtm.GtmWrapper
import ee.developest.gtm.Util

class CustomSaveListener : FileDocumentManagerListener {
    override fun beforeDocumentSaving(document: Document) {
        val instance = FileDocumentManager.getInstance()
        val file = instance.getFile(document) ?: return
        val project = Util.getProject(document)
        project.ifPresent { p: Project? -> GtmWrapper.instance.recordFile(p, file) }
    }
}