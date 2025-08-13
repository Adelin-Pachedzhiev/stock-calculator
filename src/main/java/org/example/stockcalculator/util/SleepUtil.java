package org.example.stockcalculator.util;

import static java.util.concurrent.TimeUnit.SECONDS;
import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class SleepUtil {

    public static void sleepSilentlyForSeconds(long seconds) {
        try {
            SECONDS.sleep(seconds);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
