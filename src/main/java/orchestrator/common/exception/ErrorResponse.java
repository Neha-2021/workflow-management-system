package orchestrator.common.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
  private LocalDateTime timeStamp;
  private int status;
  private String error;
  private String errorMessage;

  public ErrorResponse(int status, String error, String errorMessage) {
    this.timeStamp = LocalDateTime.now();
    this.status = status;
    this.error = error;
    this.errorMessage = errorMessage;
  }

  public LocalDateTime getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(LocalDateTime timeStamp) {
    this.timeStamp = timeStamp;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
