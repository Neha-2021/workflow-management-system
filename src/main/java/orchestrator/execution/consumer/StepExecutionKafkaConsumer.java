package orchestrator.execution.consumer;

import orchestrator.execution.engine.WorkflowEngine;
import orchestrator.execution.event.StepExecutionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class StepExecutionKafkaConsumer {
  private final WorkflowEngine workflowEngine;

  private static final Logger log = LoggerFactory.getLogger(StepExecutionKafkaConsumer.class);

  public StepExecutionKafkaConsumer(WorkflowEngine workflowEngine) {
    this.workflowEngine = workflowEngine;
  }

  @KafkaListener(topics = "${workflow.execution.topic}")
  public void consume(StepExecutionEvent event, Acknowledgment acknowledgment) {
    log.info(
        "StepExecutionKafkaConsumer | Received step execution event: {}", event.stepExecutionId());
    try {
      workflowEngine.execute(event.stepExecutionId());
    } catch (Exception ex) {
      log.error(
          "StepExecutionKafkaConsumer | Failed to execute step: {}", event.stepExecutionId(), ex);
    } finally {
      acknowledgment.acknowledge();
      log.info(
          "StepExecutionKafkaConsumer | Acknowledged step execution event: {}",
          event.stepExecutionId());
    }
  }
}
