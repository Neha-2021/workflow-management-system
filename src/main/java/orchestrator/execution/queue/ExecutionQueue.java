package orchestrator.execution.queue;

import java.util.UUID;

public interface ExecutionQueue {
  void submit(UUID stepExecutionId);

  UUID take() throws InterruptedException;
}
