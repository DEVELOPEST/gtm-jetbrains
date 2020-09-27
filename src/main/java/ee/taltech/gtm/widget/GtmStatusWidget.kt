package ee.taltech.gtm.widget

import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget


class GTMStatusWidget private constructor(): StatusBarWidget {

    companion object {
        val hoursRegex = """(\d+)h""".toRegex()
        val minutesRegex = """(\d+)m""".toRegex()
        val secondsRegex = """(\d+)s""".toRegex()

        var instance = GTMStatusWidget()
        val widgetPresentation = GtmWidgetPresentation()
    }
    var displayText = "GTM: not initialized"

    var statusBar: StatusBar? = null

    override fun ID(): String {
        return GTMStatusWidget::class.java.simpleName
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return widgetPresentation
    }

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
        statusBar.updateWidget(ID())
    }

    override fun dispose() {

    }

    fun setTimeSpent(timeText: String) {
        val h = hoursRegex.find(timeText)?.groups?.get(1)?.value
        val m = minutesRegex.find(timeText)?.groups?.get(1)?.value
        val s = secondsRegex.find(timeText)?.groups?.get(1)?.value
        displayText = "GTM: ${h ?: 0}h ${m ?: 0}m ${s ?: 0}s"
        statusBar?.updateWidget(ID())
    }
}