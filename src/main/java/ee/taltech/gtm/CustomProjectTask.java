package ee.taltech.gtm;

import com.intellij.task.ProjectTaskContext;
import com.intellij.task.ProjectTaskListener;
import org.jetbrains.annotations.NotNull;

public class CustomProjectTask implements ProjectTaskListener {

    @Override
    public void started(@NotNull ProjectTaskContext context) {
        System.out.println(1);
    }
}
