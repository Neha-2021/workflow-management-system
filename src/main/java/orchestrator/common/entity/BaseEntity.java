package orchestrator.common.entity;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity {
  @Id private UUID id;

  @Version private Long version;

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getId() {
    return id;
  }

  public Long getVersion() {
    return version;
  }
}
