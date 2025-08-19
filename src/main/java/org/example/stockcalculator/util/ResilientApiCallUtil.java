package org.example.stockcalculator.util;

import static org.example.stockcalculator.util.SleepUtil.sleepSilentlyForSeconds;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

import java.util.function.Supplier;

import org.springframework.web.client.HttpClientErrorException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResilientApiCallUtil {

    public static final int DEFAULT_MAX_RETRIES = 6;

    public static <T> T executeWithRetryOn529(Supplier<T> action, Object info) {
        return executeWithRetryOn529(action, DEFAULT_MAX_RETRIES, info);
    }

    public static <T> T executeWithRetryOn529(Supplier<T> action, int maxRetries, Object info) {
        int attempt = 0;
        while (attempt < maxRetries) {
            try {
                return action.get();
            }
            catch (HttpClientErrorException e) {
                if (e.getStatusCode().isSameCodeAs(TOO_MANY_REQUESTS)) {
                    attempt++;
                    log.warn("Received 529 Too Many Requests from {}. Attempt {} of {}. Retrying in 10 seconds...", info, attempt, maxRetries);
                    sleepSilentlyForSeconds(10);
                }
                else {
                    throw e;
                }
            }
            catch (Exception ex) {
                log.error("Exception occurred while executing {}", info, ex);
                throw ex;
            }
        }
        throw new RuntimeException(String.format("Failed to complete API call to %s after %d attempts due to repeated 529 errors.", info, maxRetries));
    }

}
