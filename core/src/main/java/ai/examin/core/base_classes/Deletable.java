package ai.examin.core.base_classes;

import java.util.UUID;

public interface Deletable {
    void softDelete(UUID currentUserId);
}
