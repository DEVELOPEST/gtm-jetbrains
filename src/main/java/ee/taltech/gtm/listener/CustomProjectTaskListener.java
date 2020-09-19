package ee.taltech.gtm.listener;

import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.task.ProjectTaskContext;
import com.intellij.task.ProjectTaskListener;
import com.intellij.task.ProjectTaskManager;
import ee.taltech.gtm.GtmWrapper;
import org.jetbrains.annotations.NotNull;

public class CustomProjectTaskListener implements ProjectTaskListener {

    @Override
    public void started(@NotNull ProjectTaskContext context) {
        RunConfiguration configuration = context.getRunConfiguration();
        if (configuration == null) return;
        Project project = configuration.getProject();
        GtmWrapper.getInstance().recordEvent(project, GtmWrapper.AppEventType.RUN);
    }

    @Override
    public void finished(@NotNull ProjectTaskManager.Result result) {
        ProjectTaskContext context = result.getContext();
        RunConfiguration configuration = context.getRunConfiguration();
        if (configuration == null) return;
        Project project = configuration.getProject();
        GtmWrapper.getInstance().recordEvent(project, GtmWrapper.AppEventType.RUN);
    }
}
