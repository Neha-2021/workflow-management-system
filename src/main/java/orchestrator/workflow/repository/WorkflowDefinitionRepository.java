package orchestrator.workflow.repository;

import java.util.Optional;
import java.util.UUID;
import orchestrator.workflow.entity.WorkflowDefinitionEntity;
import orchestrator.workflow.enums.WorkflowDefinitionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowDefinitionRepository
    extends JpaRepository<WorkflowDefinitionEntity, UUID> {
  Optional<WorkflowDefinitionEntity> findTopByNameOrderByDefinitionVersionDesc(String name);

  Optional<WorkflowDefinitionEntity> findByNameAndDefinitionVersion(
      String name, Integer definitionVersion);

  Optional<WorkflowDefinitionEntity> findByNameAndStatus(
      String name, WorkflowDefinitionStatus status);
}
