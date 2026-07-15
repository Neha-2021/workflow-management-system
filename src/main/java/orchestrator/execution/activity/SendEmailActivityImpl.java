package orchestrator.execution.activity;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class SendEmailActivityImpl implements Activity {
  @Override
  public String getName() {
    return ActivityNames.SEND_EMAIL;
  }

  @Override
  public ActivityResult execute(JsonNode input) {
    return ActivityResult.success();
  }
}
