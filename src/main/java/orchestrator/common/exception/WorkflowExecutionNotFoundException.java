package orchestrator.common.exception;

public class WorkflowExecutionNotFoundException extends NotFoundException {
  public WorkflowExecutionNotFoundException(String message) {
    super(message);
  }
}
