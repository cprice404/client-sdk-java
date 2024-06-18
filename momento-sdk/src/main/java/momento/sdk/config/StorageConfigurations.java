package momento.sdk.config;

import java.time.Duration;
import momento.sdk.config.transport.GrpcConfiguration;
import momento.sdk.config.transport.StaticTransportStrategy;
import momento.sdk.config.transport.TransportStrategy;

/** Prebuilt {@link StorageConfiguration}s for different environments. */
public class StorageConfigurations {
  /**
   * Provides defaults suitable for a medium-to-high-latency dev environment. Permissive timeouts,
   * retries, and relaxed latency and throughput targets.
   */
  public static class Laptop extends StorageConfiguration {

    private Laptop(TransportStrategy transportStrategy) {
      super(transportStrategy);
    }

    /**
     * Provides the latest recommended configuration for a dev environment.
     *
     * <p>This configuration may change in future releases to take advantage of improvements we
     * identify for default configurations.
     *
     * @return the latest Laptop configuration
     */
    public static StorageConfiguration latest() {
      final TransportStrategy transportStrategy =
          // TODO
          new StaticTransportStrategy(new GrpcConfiguration(Duration.ofMillis(15000)));
      return new Laptop(transportStrategy);
    }
  }

  /**
   * Provides defaults suitable for an environment where your client is running in the same region
   * as the Momento service. It has more aggressive timeouts and retry behavior than the Laptop
   * config.
   */
  public static class InRegion extends StorageConfiguration {

    private InRegion(TransportStrategy transportStrategy) {
      super(transportStrategy);
    }

    /**
     * Provides the latest recommended configuration for an in-region environment.
     *
     * <p>This configuration may change in future releases to take advantage of improvements we
     * identify for default configurations.
     *
     * @return the latest in-region configuration
     */
    public static StorageConfiguration latest() {
      final TransportStrategy transportStrategy =
          // TODO
          new StaticTransportStrategy(new GrpcConfiguration(Duration.ofMillis(15000)));
      return new InRegion(transportStrategy);
    }
  }

  public static class Lambda extends StorageConfiguration {
    private Lambda(TransportStrategy transportStrategy) {
      super(transportStrategy);
    }

    /**
     * Provides the latest recommended configuration for a Lambda environment.
     *
     * <p>This configuration may change in future releases to take advantage of improvements we
     * identify for default configurations.
     *
     * <p>NOTE: keep-alives are very important for long-lived server environments where there may be
     * periods of time when the connection is idle. However, they are very problematic for lambda
     * environments where the lambda runtime is continuously frozen and unfrozen, because the lambda
     * may be frozen before the "ACK" is received from the server. This can cause the keep-alive to
     * timeout even though the connection is completely healthy. Therefore, keep-alives should be
     * disabled in lambda and similar environments.
     *
     * @return the latest Lambda configuration
     */
    public static StorageConfiguration latest() {
      final GrpcConfiguration grpcConfig =
          // TODO
          new GrpcConfiguration(Duration.ofMillis(15000)).withKeepAliveDisabled();
      final TransportStrategy transportStrategy = new StaticTransportStrategy(grpcConfig);
      return new Lambda(transportStrategy);
    }
  }
}