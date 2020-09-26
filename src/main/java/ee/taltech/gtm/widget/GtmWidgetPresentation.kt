package ee.taltech.gtm.widget

import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.Consumer
import java.awt.event.MouseEvent

class GtmWidgetPresentation : StatusBarWidget.TextPresentation {

    var displayText = "GTM: not initialized"

    override fun getTooltipText(): String {
        return "Git Time Metrics"
    }

    override fun getClickConsumer(): Consumer<MouseEvent>? {
        return null
    }

    override fun getText(): String {
        return displayText
    }

    override fun getAlignment(): Float {
        return 0.0f
    }
}