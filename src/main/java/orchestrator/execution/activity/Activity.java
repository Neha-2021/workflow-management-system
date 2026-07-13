package orchestrator.execution.activity;

import com.fasterxml.jackson.databind.JsonNode;

public interface Activity {
  String getName();

  ActivityResult execute(JsonNode input);
}
