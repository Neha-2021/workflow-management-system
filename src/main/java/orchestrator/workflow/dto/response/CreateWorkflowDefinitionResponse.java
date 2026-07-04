package orchestrator.workflow.dto.response;

import java.util.UUID;

public record CreateWorkflowDefinitionResponse(UUID workflowId, Integer version) {}
