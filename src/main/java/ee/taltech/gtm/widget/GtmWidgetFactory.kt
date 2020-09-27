package ee.taltech.gtm.widget

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import ee.taltech.gtm.GtmWrapper

class GtmWidgetFactory : StatusBarWidgetFactory {

    override fun getId(): String {
        return GTMStatusWidget::class.java.simpleName
    }

    override fun getDisplayName(): String {
       return "GTM"
    }

    override fun isAvailable(project: Project): Boolean {
        return true
    }

    override fun createWidget(project: Project): StatusBarWidget {
        GtmWrapper.instance.checkHours(project)
        return GTMStatusWidget.instance
    }

    override fun disposeWidget(widget: StatusBarWidget) {
    }

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean {
        return true
    }

}