package config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static utils.LoggingTransactionUtils.LOGGING_TRANSACTION_ID_KEY;
import static utils.LoggingTransactionUtils.TRANSACTION_ID_HEADER;

@Component
@Slf4j
public class TransactionIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String transactionId = request.getHeader(TRANSACTION_ID_HEADER);
        if (transactionId == null || transactionId.isBlank()) {
            transactionId = UUID.randomUUID().toString();
        }

        MDC.put(LOGGING_TRANSACTION_ID_KEY, transactionId);
        response.setHeader(TRANSACTION_ID_HEADER, transactionId);

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        try {
            log.info("""
                            
                            
                            ==================== [START REQUEST] ====================
                            Time          : {}
                            Transaction-ID: {}
                            Method        : {}
                            URI           : {}
                            =========================================================""",
                    timestamp, transactionId, method, uri);

            filterChain.doFilter(request, response);

        } finally {
            log.info("""
                            
                            ===================== [END REQUEST] =====================
                            Transaction-ID: {}
                            =========================================================
                            """,
                    transactionId);

            MDC.remove(LOGGING_TRANSACTION_ID_KEY);
        }
    }

}
