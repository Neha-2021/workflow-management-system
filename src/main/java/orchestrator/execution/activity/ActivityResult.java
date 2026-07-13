package orchestrator.execution.activity;

public record ActivityResult(boolean isSuccess, String errorMessage) {
  public static ActivityResult success() {
    return new ActivityResult(true, null);
  }

  public static ActivityResult failure(String errorMessage) {
    return new ActivityResult(false, errorMessage);
  }
}
