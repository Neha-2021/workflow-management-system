package orchestrator.execution.activity;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class ValidateOrderActivityImpl implements Activity {
  @Override
  public String getName() {
    return ActivityNames.VALIDATE_ORDER;
  }

  @Override
  public ActivityResult execute(JsonNode input) {
    return ActivityResult.success();
  }
}
