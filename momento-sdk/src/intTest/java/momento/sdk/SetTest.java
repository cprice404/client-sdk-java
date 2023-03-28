package momento.sdk;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import momento.sdk.exceptions.InvalidArgumentException;
import momento.sdk.messages.CacheSetAddElementResponse;
import momento.sdk.messages.CacheSetFetchResponse;
import momento.sdk.requests.CollectionTtl;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SetTest {
  private static final Duration DEFAULT_TTL = Duration.ofSeconds(60);
  private CacheClient client;
  private String cacheName;

  private final String setName = "test-set";

  @BeforeEach
  void setup() {
    client = CacheClient.builder(System.getenv("TEST_AUTH_TOKEN"), DEFAULT_TTL).build();
    cacheName = System.getenv("TEST_CACHE_NAME");
    client.createCache(cacheName);
  }

  @AfterEach
  void teardown() {
    client.deleteCache(cacheName);
    client.close();
  }

  @Test
  public void setAddElementStringHappyPath() {
    final String element1 = "1";
    final String element2 = "2";

    assertThat(client.setFetch(cacheName, setName))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .isInstanceOf(CacheSetFetchResponse.Miss.class);

    assertThat(client.setAddElement(cacheName, setName, element1, CollectionTtl.fromCacheTtl()))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .isInstanceOf(CacheSetAddElementResponse.Success.class);

    assertThat(client.setFetch(cacheName, setName))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetFetchResponse.Hit.class))
        .satisfies(hit -> assertThat(hit.valueSetString()).hasSize(1).containsOnly(element1));

    // Try to add the same element again
    assertThat(client.setAddElement(cacheName, setName, element1, CollectionTtl.fromCacheTtl()))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .isInstanceOf(CacheSetAddElementResponse.Success.class);

    assertThat(client.setFetch(cacheName, setName))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetFetchResponse.Hit.class))
        .satisfies(hit -> assertThat(hit.valueSetString()).hasSize(1).containsOnly(element1));

    // Add a different element
    assertThat(client.setAddElement(cacheName, setName, element2, CollectionTtl.fromCacheTtl()))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .isInstanceOf(CacheSetAddElementResponse.Success.class);

    assertThat(client.setFetch(cacheName, setName))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetFetchResponse.Hit.class))
        .satisfies(
            hit -> assertThat(hit.valueSetString()).hasSize(2).containsOnly(element1, element2));
  }

  @Test
  public void setAddElementBytesHappyPath() {
    final byte[] element1 = "one".getBytes();
    final byte[] element2 = "two".getBytes();

    assertThat(client.setAddElement(cacheName, setName, element1, CollectionTtl.fromCacheTtl()))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .isInstanceOf(CacheSetAddElementResponse.Success.class);

    assertThat(client.setFetch(cacheName, setName))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetFetchResponse.Hit.class))
        .satisfies(hit -> assertThat(hit.valueSetByteArray()).hasSize(1).containsOnly(element1));

    // Try to add the same element again
    assertThat(client.setAddElement(cacheName, setName, element1, CollectionTtl.fromCacheTtl()))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .isInstanceOf(CacheSetAddElementResponse.Success.class);

    assertThat(client.setFetch(cacheName, setName))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetFetchResponse.Hit.class))
        .satisfies(hit -> assertThat(hit.valueSetByteArray()).hasSize(1).containsOnly(element1));

    // Add a different element
    assertThat(client.setAddElement(cacheName, setName, element2, CollectionTtl.fromCacheTtl()))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .isInstanceOf(CacheSetAddElementResponse.Success.class);

    assertThat(client.setFetch(cacheName, setName))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetFetchResponse.Hit.class))
        .satisfies(
            hit -> assertThat(hit.valueSetByteArray()).hasSize(2).containsOnly(element1, element2));
  }

  @Test
  public void setAddElementReturnsErrorWithNullCacheName() {
    final String elementString = "element";
    final byte[] elementBytes = elementString.getBytes(StandardCharsets.UTF_8);

    assertThat(client.setAddElement(null, setName, elementString, CollectionTtl.fromCacheTtl()))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetAddElementResponse.Error.class))
        .satisfies(error -> assertThat(error).hasCauseInstanceOf(InvalidArgumentException.class));

    assertThat(client.setAddElement(null, setName, elementBytes, CollectionTtl.fromCacheTtl()))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetAddElementResponse.Error.class))
        .satisfies(error -> assertThat(error).hasCauseInstanceOf(InvalidArgumentException.class));
  }

  @Test
  public void setAddElementReturnsErrorWithNullSetName() {
    final String elementString = "element";
    final byte[] elementBytes = elementString.getBytes(StandardCharsets.UTF_8);

    assertThat(client.setAddElement(cacheName, null, elementString, CollectionTtl.fromCacheTtl()))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetAddElementResponse.Error.class))
        .satisfies(error -> assertThat(error).hasCauseInstanceOf(InvalidArgumentException.class));

    assertThat(client.setAddElement(cacheName, null, elementBytes, CollectionTtl.fromCacheTtl()))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetAddElementResponse.Error.class))
        .satisfies(error -> assertThat(error).hasCauseInstanceOf(InvalidArgumentException.class));
  }

  @Test
  public void setAddElementReturnsErrorWithNullElement() {
    assertThat(
            client.setAddElement(cacheName, cacheName, (String) null, CollectionTtl.fromCacheTtl()))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetAddElementResponse.Error.class))
        .satisfies(error -> assertThat(error).hasCauseInstanceOf(InvalidArgumentException.class));

    assertThat(
            client.setAddElement(cacheName, cacheName, (byte[]) null, CollectionTtl.fromCacheTtl()))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetAddElementResponse.Error.class))
        .satisfies(error -> assertThat(error).hasCauseInstanceOf(InvalidArgumentException.class));
  }

  @Test
  public void setFetchReturnsErrorWithNullCacheName() {
    assertThat(client.setFetch(null, "set"))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetFetchResponse.Error.class))
        .satisfies(error -> assertThat(error).hasCauseInstanceOf(InvalidArgumentException.class));
  }

  @Test
  public void setFetchReturnsErrorWithNullSetName() {
    assertThat(client.setFetch(cacheName, null))
        .succeedsWithin(5, TimeUnit.SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.type(CacheSetFetchResponse.Error.class))
        .satisfies(error -> assertThat(error).hasCauseInstanceOf(InvalidArgumentException.class));
  }
}
