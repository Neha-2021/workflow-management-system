package orchestrator.workflow.repository;

import java.util.UUID;
import orchestrator.workflow.entity.WorkflowStepEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowStepRepository extends JpaRepository<WorkflowStepEntity, UUID> {}
