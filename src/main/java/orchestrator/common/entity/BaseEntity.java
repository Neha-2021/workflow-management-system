package orchestrator.common.entity;

import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity {
    @Id
    private UUID id;

    @Version
    private Long version;
}
