package momento.client.example;

import com.google.common.util.concurrent.RateLimiter;
import momento.sdk.CacheClient;
import momento.sdk.auth.CredentialProvider;
import momento.sdk.config.Configurations;
import momento.sdk.responses.cache.GetResponse;
import momento.sdk.responses.cache.SetResponse;
import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.Histogram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ManyTransformedRequests {
    private static final String CACHE_NAME = "java-loadgen";
    private static final Duration DEFAULT_ITEM_TTL = Duration.ofMinutes(60);
    private static final int NUM_REGULAR_KEYS = 1000;
    private static final String cacheValue = "x".repeat(200);

    private static final Logger logger = LoggerFactory.getLogger(ManyTransformedRequests.class);


    private static <T> void awaitFutures(List<Future<T>> futures) {
        futures.forEach(future -> {
            try {
//                logger.info("Waiting for futures");
                future.get(20, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static String formatHistogram(Histogram histogram) {
        return String.format("%5s: %d\n", "count", histogram.getTotalCount())
                + String.format("%5s: %.2f\n", "min", histogram.getMinValue() / 1_000_000.0)
                + String.format("%5s: %.2f\n", "p50", histogram.getValueAtPercentile(50.0) / 1_000_000.0)
                + String.format("%5s: %.2f\n", "p90", histogram.getValueAtPercentile(90.0) / 1_000_000.0)
                + String.format("%5s: %.2f\n", "p95", histogram.getValueAtPercentile(95.0) / 1_000_000.0)
                + String.format("%5s: %.2f\n", "p96", histogram.getValueAtPercentile(96.0) / 1_000_000.0)
                + String.format("%5s: %.2f\n", "p97", histogram.getValueAtPercentile(97.0) / 1_000_000.0)
                + String.format("%5s: %.2f\n", "p98", histogram.getValueAtPercentile(98.0) / 1_000_000.0)
                + String.format("%5s: %.2f\n", "p99", histogram.getValueAtPercentile(99.0) / 1_000_000.0)
                + String.format("%5s: %.2f\n", "p99.9", histogram.getValueAtPercentile(99.9) / 1_000_000.0)
                + String.format("%5s: %.2f\n", "max", histogram.getMaxValue() / 1_000_000.0);
    }

    public static void main(String[] args) throws InterruptedException {
        logger.info("Hello! Many transformed requests!");
        final ConcurrentHistogram getHistogram = new ConcurrentHistogram(3);
        final CredentialProvider credentialProvider = CredentialProvider.fromEnvVar("MOMENTO_API_KEY");
        final RateLimiter initializeKeysRateLimiter = RateLimiter.create(1000);
        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(100);

        try (CacheClient client = CacheClient.create(credentialProvider, Configurations.Laptop.v1(), DEFAULT_ITEM_TTL)) {

            List<Future<SetResponse>> initializeKeyFutures = IntStream.range(1, NUM_REGULAR_KEYS + 1).mapToObj(i -> {
                return (Future<SetResponse>) executorService.schedule(() -> {
//                    logger.info("Regular keys: Acquire rate limiter");
                    initializeKeysRateLimiter.acquire();
//                    logger.info("Regular keys: Acquired rate limiter");
                    final String key = "regularKey" + i;
//                    logger.info("Initializing regular key: " + key);
                    SetResponse setResult = client.set(CACHE_NAME, key, cacheValue).join();
                    if (!(setResult instanceof SetResponse.Success)) {
                        logger.error("THROWING EXCEPTION!");
                        throw new RuntimeException("Failed to initialize regular key: " + key);
                    }
//                    logger.info("Initialized key: " + key);
                    return setResult;
                }, 0, TimeUnit.MILLISECONDS);
            }).toList();

            awaitFutures(initializeKeyFutures);

            logger.info("Initialized all keys!");

            ExecutorService transformExecutorService = Executors.newFixedThreadPool(100);
//            RateLimiter rateLimiter = RateLimiter.create(2000);
            RateLimiter rateLimiter = RateLimiter.create(100);

            for (int iteration = 0; iteration < 1; iteration++) {
                List<Future<Void>> getFutures = new ArrayList<>();
                for (int i = 0; i < 1000; i++) {
//                    Thread.sleep(1);
                rateLimiter.acquire();
                    String key = "regularKey" + (i + 1);
                    long start = System.nanoTime();
                getFutures.add(client.get(CACHE_NAME, key).thenAccept(getResponse -> {
                    if (!(getResponse instanceof GetResponse.Hit)) {
                        logger.warn("Got a response that wasn't a hit! (key " + key + "): " + getResponse);
                    }
                   getHistogram.recordValue(System.nanoTime() - start);
                }));
//                    getFutures.add(client.get(CACHE_NAME, key).thenAcceptAsync(getResponse -> {
//                        getHistogram.recordValue(System.nanoTime() - start);
//                    }, transformExecutorService));
                }

                logger.info("Awaiting futures for iteration " + iteration);
                awaitFutures(getFutures);
                logger.info("Futures complete for " + iteration);
            }

            logger.info("\n" + formatHistogram(getHistogram));


            transformExecutorService.shutdown();
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
