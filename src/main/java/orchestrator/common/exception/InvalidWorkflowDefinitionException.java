package orchestrator.common.exception;

public class InvalidWorkflowDefinitionException extends BadRequestException {

  public InvalidWorkflowDefinitionException(String message) {
    super(message);
  }
}
