package utils;

import lombok.experimental.UtilityClass;
import org.slf4j.MDC;

@UtilityClass
public class LoggingTransactionUtils {
    public static String LOGGING_TRANSACTION_ID_KEY = "transactionId";
    public static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";

    public static String getTransactionId() {
        return MDC.get(LOGGING_TRANSACTION_ID_KEY);
    }
}