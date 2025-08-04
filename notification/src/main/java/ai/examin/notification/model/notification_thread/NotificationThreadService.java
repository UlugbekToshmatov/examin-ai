package ai.examin.notification.model.notification_thread;

import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.exception_handler.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationThreadService {
    private final RabbitListenerEndpointRegistry registry;

    public String start(String listenerId) {
        var container = registry.getListenerContainer(listenerId);

        if (container == null)
            throw new ApiException(ResponseStatus.LISTENER_CONTAINER_NOT_FOUND);
        if (container.isRunning())
            throw new ApiException(ResponseStatus.LISTENER_CONTAINER_ALREADY_STARTED);

        container.start();
        return "Started listener container with id: " + listenerId;
    }

    public String stop(String listenerId) {
        var container = registry.getListenerContainer(listenerId);

        if (container == null)
            throw new ApiException(ResponseStatus.LISTENER_CONTAINER_NOT_FOUND);
        if (!container.isRunning())
            throw new ApiException(ResponseStatus.LISTENER_CONTAINER_ALREADY_STOPPED);

        container.stop();
        return "Stopped listener container with id: " + listenerId;
    }

    public Map<String, Boolean> getStatuses() {
        Map<String, Boolean> status = new LinkedHashMap<>();
        registry.getListenerContainerIds()
            .forEach(id -> status.put(id, registry.getListenerContainer(id).isRunning()));
        return status;
    }
}
