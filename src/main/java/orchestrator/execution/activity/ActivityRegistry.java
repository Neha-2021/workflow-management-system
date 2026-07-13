package orchestrator.execution.activity;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import orchestrator.common.exception.ActivityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ActivityRegistry {
  private final Map<String, Activity> activities;

  public ActivityRegistry(List<Activity> activities) {
    this.activities =
        activities.stream().collect(Collectors.toMap(Activity::getName, Function.identity()));
  }

  public Activity getActivity(String activityName) {
    Activity activity = activities.get(activityName);

    if (activity == null) {
      throw new ActivityNotFoundException(
          String.format("Activity '%s' not registered", activityName));
    }
    return activity;
  }
}
