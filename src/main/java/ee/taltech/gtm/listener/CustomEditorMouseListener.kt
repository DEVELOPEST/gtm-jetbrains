package ee.taltech.gtm.listener

import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import ee.taltech.gtm.GtmWrapper

class CustomEditorMouseListener : EditorMouseListener {
    override fun mousePressed(event: EditorMouseEvent) {
        val instance = FileDocumentManager.getInstance()
        val file = instance.getFile(event.editor.document) ?: return
        val project = event.editor.project
        GtmWrapper.instance.recordFile(project, file)
    }
}