package orchestrator.execution.activity;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class ProcessPaymentActivityImpl implements Activity {
  @Override
  public String getName() {
    return ActivityNames.PROCESS_PAYMENT;
  }

  @Override
  public ActivityResult execute(JsonNode input) {
    return ActivityResult.success();
  }
}
