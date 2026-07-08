package orchestrator.execution.repository;

import java.util.UUID;
import orchestrator.execution.entity.WorkflowExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecutionEntity, UUID> {}
