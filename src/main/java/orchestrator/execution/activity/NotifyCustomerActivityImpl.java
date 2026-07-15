package orchestrator.execution.activity;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class NotifyCustomerActivityImpl implements Activity {
  @Override
  public String getName() {
    return ActivityNames.NOTIFY_CUSTOMER;
  }

  @Override
  public ActivityResult execute(JsonNode input) {
    return ActivityResult.success();
  }
}
