package orchestrator.execution.consumer;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import orchestrator.execution.engine.WorkflowEngine;
import orchestrator.execution.event.StepExecutionEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.Acknowledgment;

class StepExecutionKafkaConsumerTest {
  private final WorkflowEngine workflowEngine = org.mockito.Mockito.mock(WorkflowEngine.class);
  private final Acknowledgment acknowledgment = org.mockito.Mockito.mock(Acknowledgment.class);
  private final StepExecutionKafkaConsumer consumer =
      new StepExecutionKafkaConsumer(workflowEngine);

  @Test
  void shouldExecuteWorkflowStepAndAcknowledgeMessage() {
    UUID stepExecutionId = UUID.randomUUID();

    consumer.consume(new StepExecutionEvent(stepExecutionId), acknowledgment);

    verify(workflowEngine).execute(stepExecutionId);
    verify(acknowledgment).acknowledge();
  }

  @Test
  void shouldAcknowledgeMessageWhenWorkflowEngineThrows() {
    UUID stepExecutionId = UUID.randomUUID();
    doThrow(new RuntimeException("boom")).when(workflowEngine).execute(stepExecutionId);

    consumer.consume(new StepExecutionEvent(stepExecutionId), acknowledgment);

    verify(workflowEngine).execute(stepExecutionId);
    verify(acknowledgment).acknowledge();
  }
}
