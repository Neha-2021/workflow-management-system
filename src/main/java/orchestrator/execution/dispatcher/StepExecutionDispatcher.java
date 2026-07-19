package orchestrator.execution.dispatcher;

import java.util.UUID;

public interface StepExecutionDispatcher {
  void dispatch(UUID stepExecutionId);
}
