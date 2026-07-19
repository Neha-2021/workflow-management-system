package orchestrator.execution.dispatcher;

import java.util.UUID;
import orchestrator.execution.event.StepExecutionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaStepExecutionDispatcher implements StepExecutionDispatcher {
  private final KafkaTemplate<String, StepExecutionEvent> kafkaTemplate;
  private final String topicName;

  private static final Logger log = LoggerFactory.getLogger(KafkaStepExecutionDispatcher.class);

  public KafkaStepExecutionDispatcher(
      KafkaTemplate<String, StepExecutionEvent> kafkaTemplate,
      @Value("${workflow.execution.topic}") String topicName) {
    this.kafkaTemplate = kafkaTemplate;
    this.topicName = topicName;
  }

  @Override
  public void dispatch(UUID stepExecutionId) {
    log.info("KafkaStepExecutionDispatcher | Publishing step execution event: {}", stepExecutionId);
    kafkaTemplate.send(
        topicName, stepExecutionId.toString(), new StepExecutionEvent(stepExecutionId));
  }
}
