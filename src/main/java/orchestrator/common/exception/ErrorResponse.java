package orchestrator.common.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
    LocalDateTime timeStamp, int status, String error, String errorMessage) {
  public ErrorResponse(int status, String error, String errorMessage) {
    this(LocalDateTime.now(), status, error, errorMessage);
  }
}
