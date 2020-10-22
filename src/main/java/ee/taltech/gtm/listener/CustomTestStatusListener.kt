package ee.taltech.gtm.listener

import com.intellij.execution.testframework.AbstractTestProxy
import com.intellij.execution.testframework.TestStatusListener

class CustomTestStatusListener : TestStatusListener() {
    override fun testSuiteFinished(root: AbstractTestProxy?) {

    }
}