package orchestrator.execution.dto.response;

import java.util.UUID;
import orchestrator.common.enums.WorkflowStatus;

public record StartWorkflowExecutionResponse(UUID workflowExecutionId, WorkflowStatus status) {}
