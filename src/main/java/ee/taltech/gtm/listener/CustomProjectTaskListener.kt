package ee.taltech.gtm.listener

import com.intellij.task.ProjectTaskContext
import com.intellij.task.ProjectTaskListener
import com.intellij.task.ProjectTaskManager
import ee.taltech.gtm.AppEventType
import ee.taltech.gtm.GtmWrapper

class CustomProjectTaskListener : ProjectTaskListener {
    override fun started(context: ProjectTaskContext) {
        val configuration = context.runConfiguration ?: return
        val project = configuration.project
        val name = configuration.name
        GtmWrapper.instance.recordEvent(project, name, AppEventType.RUN)
    }

    override fun finished(result: ProjectTaskManager.Result) {
        val context = result.context
        val configuration = context.runConfiguration ?: return
        val project = configuration.project
        val name = configuration.name
        GtmWrapper.instance.recordEvent(project, name, AppEventType.BUILD) // TODO: Build Run etc
    }
}