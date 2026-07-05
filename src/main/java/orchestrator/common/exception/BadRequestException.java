package orchestrator.common.exception;

public abstract class BadRequestException extends ApiException {

  protected BadRequestException(String message) {
    super(message);
  }
}
