package orchestrator.workflow.repository;

import java.util.Optional;
import java.util.UUID;
import orchestrator.workflow.entity.WorkflowDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowDefinitionRepository
    extends JpaRepository<WorkflowDefinitionEntity, UUID> {
  Optional<WorkflowDefinitionEntity> findTopByNameOrderByDefinitionVersionDesc(String name);
}
