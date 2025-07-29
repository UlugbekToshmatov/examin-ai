package dto.response;

public record ErrorResponse(
        Integer statusCode,
        String message
) {
}
