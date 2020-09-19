package ee.taltech.gtm.listener;

import com.intellij.build.BuildProgressListener;
import com.intellij.build.events.BuildEvent;
import org.jetbrains.annotations.NotNull;

public class CustomBuildProcessListener implements BuildProgressListener {
    @Override
    public void onEvent(@NotNull Object buildId, @NotNull BuildEvent event) {
        System.out.println(1);
    }
}
