package orchestrator.execution.activity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValidateOrderActivityImplTest {
  private final ValidateOrderActivityImpl activity = new ValidateOrderActivityImpl();

  @Test
  void shouldReturnSuccess() {

    JsonNode input = new ObjectMapper().createObjectNode().put("orderId", "123");

    ActivityResult result = activity.execute(input);

    assertThat(result.isSuccess()).isTrue();
    assertThat(result.errorMessage()).isNull();
    assertThat(activity.getName()).isEqualTo(ActivityNames.VALIDATE_ORDER);
  }
}
