package ee.developest.gtm.listener

import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import ee.developest.gtm.GtmWrapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CustomProcessListener: ExecutionListener {

    companion object {
        private const val TASK_SEND_DELAY = 10000L
        private const val MAX_TASK_TIME = 5 * 60 * 1000L // 5 min
    }

    private val jobs: MutableMap<String, Job> = mutableMapOf()

    override fun processStarting(executorId: String, env: ExecutionEnvironment) {
        jobs[executorId] = GlobalScope.launch{
            repeat((MAX_TASK_TIME / TASK_SEND_DELAY).toInt()) {
                GtmWrapper.instance.recordEvent(env.project, env.runProfile.toString())
                delay(TASK_SEND_DELAY)
            }
        }
    }

    override fun processTerminated(executorId: String, env: ExecutionEnvironment, handler: ProcessHandler, exitCode: Int) {
        jobs[executorId]?.cancel()
    }

}