package orchestrator.execution.dispatcher;

import static org.mockito.Mockito.verify;

import java.util.UUID;
import orchestrator.execution.event.StepExecutionEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.kafka.core.KafkaTemplate;

class KafkaStepExecutionDispatcherTest {

  @SuppressWarnings("unchecked")
  private final KafkaTemplate<String, StepExecutionEvent> kafkaTemplate =
      org.mockito.Mockito.mock(KafkaTemplate.class);

  private final KafkaStepExecutionDispatcher dispatcher =
      new KafkaStepExecutionDispatcher(kafkaTemplate, "workflow.step-executions");

  @Test
  void shouldPublishStepExecutionEventToKafka() {
    UUID stepExecutionId = UUID.randomUUID();

    dispatcher.dispatch(stepExecutionId);

    ArgumentCaptor<StepExecutionEvent> eventCaptor =
        ArgumentCaptor.forClass(StepExecutionEvent.class);
    verify(kafkaTemplate)
        .send(
            ArgumentMatchers.eq("workflow.step-executions"),
            ArgumentMatchers.eq(stepExecutionId.toString()),
            eventCaptor.capture());
    org.assertj.core.api.AssertionsForClassTypes.assertThat(
            eventCaptor.getValue().stepExecutionId())
        .isEqualTo(stepExecutionId);
  }
}
