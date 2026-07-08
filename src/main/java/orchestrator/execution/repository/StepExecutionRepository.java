package orchestrator.execution.repository;

import java.util.UUID;
import orchestrator.execution.entity.StepExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepExecutionRepository extends JpaRepository<StepExecutionEntity, UUID> {}
