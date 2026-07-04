package orchestrator.workflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import orchestrator.common.entity.BaseEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "workflow_definition")
public class WorkflowDefinitionEntity extends BaseEntity {
  @NotBlank private String name;

  @NotNull private Integer definitionVersion;

  private String description;

  @NotNull @CreatedDate private Instant createdAt;

  @NotNull @LastModifiedDate private Instant updatedAt;

  public WorkflowDefinitionEntity(
      UUID workflowId, Integer definitionVersion, String name, String description) {
    this.name = name;
    this.setId(workflowId);
    this.definitionVersion = definitionVersion;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getDefinitionVersion() {
    return definitionVersion;
  }

  public void setDefinitionVersion(Integer definitionVersion) {
    this.definitionVersion = definitionVersion;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }
}
