package ai.examin.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
@RequiredArgsConstructor
public class RestTemplateUtils {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    public <T> ResponseEntity<String> execute(String url, T payload, MultiValueMap<String, String> headers, HttpMethod method) {
        HttpEntity<T> httpEntity = getHttpEntity(payload, headers);
        logHttpEntity(httpEntity);
        ResponseEntity<String> response = restTemplate.exchange(url, method, httpEntity, String.class);

        logResponse(response.getBody(), Object.class);

        return response;
    }

    public <T, R> ResponseEntity<R> execute(String url, T payload, MultiValueMap<String, String> headers, HttpMethod method, Class<R> responseType) {
        HttpEntity<T> httpEntity = getHttpEntity(payload, headers);
        logHttpEntity(httpEntity);
        ResponseEntity<R> response = restTemplate.exchange(url, method, httpEntity, responseType);
        logResponse(response.getBody());
        return response;
    }

    public <T> ResponseEntity<Object> request(String url, T payload, MultiValueMap<String, String> headers, HttpMethod method) {
        HttpEntity<T> httpEntity = getHttpEntity(payload, headers);
        logHttpEntity(httpEntity);
        ResponseEntity<Object> response = restTemplate.exchange(url, method, httpEntity, Object.class);

        logResponse(response.getBody());

        return response;
    }

    private <T> HttpEntity<T> getHttpEntity(T payload, MultiValueMap<String, String> headers) {
        headers = getHttpHeaders(headers);
        return new HttpEntity<>(payload, headers);
    }

    private MultiValueMap<String, String> getHttpHeaders(MultiValueMap<String, String> headers) {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE))
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        if (!headers.containsKey(HttpHeaders.ACCEPT))
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        return headers;
    }

    private void logHttpEntity(HttpEntity<?> httpEntity) {
        log.info("Http Entity: \"{}\"", httpEntity);
    }

    private void logResponse(Object body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            log.info("{}: {}", "Response Entity", json);
        } catch (Exception e) {
            log.warn("{}: Failed to serialize response to JSON: {}", "Response Entity", e.getMessage());
        }
    }

    private <T> void logResponse(String json, Class<T> clazz) {
        try {
            T obj = objectMapper.readValue(json, clazz);
            log.info("{}: {}", "Response Entity in Object form", obj);
        } catch (Exception e) {
            log.warn("{}: Failed to deserialize JSON to {}: {}", "Response Entity in Object form", clazz.getSimpleName(), e.getMessage());
        }
    }
}
