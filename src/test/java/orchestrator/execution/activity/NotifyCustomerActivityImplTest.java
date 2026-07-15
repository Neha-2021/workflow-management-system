package orchestrator.execution.activity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class NotifyCustomerActivityImplTest {
  private final NotifyCustomerActivityImpl activity = new NotifyCustomerActivityImpl();

  @Test
  void shouldReturnSuccess() {
    JsonNode input = new ObjectMapper().createObjectNode().put("orderId", "123");

    ActivityResult result = activity.execute(input);

    assertThat(result.isSuccess()).isTrue();
    assertThat(result.errorMessage()).isNull();
    assertThat(activity.getName()).isEqualTo(ActivityNames.NOTIFY_CUSTOMER);
  }
}
