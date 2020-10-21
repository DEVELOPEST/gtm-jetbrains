package ee.taltech.gtm.listener

import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import ee.taltech.gtm.GtmWrapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CustomProcessListener: ExecutionListener {

    companion object {
        private const val TASK_SEND_DELAY = 1000L
        private const val MAX_TASK_TIME = 60 * 60 // 1 hour
    }

    private val jobs: MutableMap<String, Job> = mutableMapOf()

    override fun processStarting(executorId: String, env: ExecutionEnvironment) {
        jobs[executorId] = GlobalScope.launch{
            repeat(MAX_TASK_TIME) {
                GtmWrapper.instance.recordEvent(env.project, env.runProfile.toString())
                delay(TASK_SEND_DELAY)
            }
        }
    }

    override fun processTerminated(executorId: String, env: ExecutionEnvironment, handler: ProcessHandler, exitCode: Int) {
        jobs[executorId]?.cancel()
    }

}