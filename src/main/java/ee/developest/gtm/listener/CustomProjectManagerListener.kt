package ee.developest.gtm.listener

import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import ee.developest.gtm.GtmWrapper

class CustomProjectManagerListener: ProjectManagerListener {

    override fun projectOpened(project: Project) {
        GtmWrapper.instance.checkHours(project)
    }
}