package orchestrator.common.exception;

public class WorkflowStepNotFoundException extends NotFoundException {
  public WorkflowStepNotFoundException(String message) {
    super(message);
  }
}
