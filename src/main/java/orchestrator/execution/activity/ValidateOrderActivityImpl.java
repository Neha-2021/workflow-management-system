package orchestrator.execution.activity;

import com.fasterxml.jackson.databind.JsonNode;

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
