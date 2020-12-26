package ee.developest.gtm.listener

import com.intellij.openapi.editor.event.VisibleAreaEvent
import com.intellij.openapi.editor.event.VisibleAreaListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import ee.developest.gtm.GtmWrapper

class CustomVisibleAreaListener : VisibleAreaListener {
    override fun visibleAreaChanged(e: VisibleAreaEvent) {
        val instance = FileDocumentManager.getInstance()
        val file = instance.getFile(e.editor.document) ?: return
        val project = e.editor.project ?: return
        GtmWrapper.instance.recordFile(project, file)
    }
}