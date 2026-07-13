package orchestrator.workflow.service.publisher;

import java.util.UUID;

public interface ExecutionPublisher {
  void publish(UUID stepExecutionId);
}
