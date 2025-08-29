package ai.examin.core.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<UUID> {
    private final UserContextService userContextService;

    @Override
    public Optional<UUID> getCurrentAuditor() {
        return userContextService.getCurrentUserId().map(UUID::fromString);
    }
}
