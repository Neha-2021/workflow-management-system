package orchestrator.common.exception;

public abstract class ConflictException extends ApiException {

  protected ConflictException(String message) {
    super(message);
  }
}
