package ee.developest.gtm.widget

import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.Consumer
import ee.developest.gtm.GtmWrapper
import java.awt.event.MouseEvent

class GtmWidgetPresentation : StatusBarWidget.TextPresentation {
    override fun getTooltipText(): String {
        return "Git Time Metrics"
    }

    override fun getClickConsumer(): Consumer<MouseEvent>? {
        return null
    }

    override fun getText(): String {
        return GTMStatusWidget.instance.displayText
    }

    override fun getAlignment(): Float {
        return 0.0f
    }
}