package ee.taltech.gtm.listener

import com.intellij.openapi.editor.event.VisibleAreaEvent
import com.intellij.openapi.editor.event.VisibleAreaListener
import com.intellij.openapi.fileEditor.FileDocumentManager

class CustomVisibleAreaListener : VisibleAreaListener {
    override fun visibleAreaChanged(e: VisibleAreaEvent) {
        val instance = FileDocumentManager.getInstance()
        val file = instance.getFile(e.editor.document)
        val project = e.editor.project
        // TODO: dispatch event, or delete this"
    }
}