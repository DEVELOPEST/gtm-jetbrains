package ee.taltech.gtm.listener

import com.intellij.build.BuildProgressListener
import com.intellij.build.events.BuildEvent

class CustomBuildProcessListener : BuildProgressListener {
    override fun onEvent(buildId: Any, event: BuildEvent) {
        println(1)
    }
}