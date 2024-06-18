package momento.sdk.storage;

import static momento.sdk.TestUtils.randomString;
import static org.assertj.core.api.Assertions.assertThat;

import momento.sdk.BaseTestClass;
import momento.sdk.IPreviewStorageClient;
import momento.sdk.PreviewStorageClient;
import momento.sdk.auth.CredentialProvider;
import momento.sdk.config.StorageConfigurations;
import momento.sdk.exceptions.NotFoundException;
import momento.sdk.responses.storage.data.DeleteResponse;
import momento.sdk.responses.storage.data.GetResponse;
import momento.sdk.responses.storage.data.PutResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DataTests extends BaseTestClass {
  private IPreviewStorageClient client;

  // TODO can set to the same value as the cache tests
  // TODO rename env var for clarity to TEST_RESOURCE_NAME or similar
  private final String storeName = System.getenv("TEST_CACHE_NAME");

  @BeforeEach
  void setup() {
    /*target =
    CacheClient.builder(credentialProvider, Configurations.Laptop.latest(), DEFAULT_TTL_SECONDS)
            .build();*/
    client =
        new PreviewStorageClient(
            CredentialProvider.fromEnvVar("MOMENTO_API_KEY"),
            StorageConfigurations.Laptop.latest());
  }

  @AfterEach
  void tearDown() {
    /*target.close();*/
  }

  @Test
  void getReturnsValueAsStringAfterPut() {
    final String key = randomString("key");
    final String value = randomString("value");

    // Successful Set
    final PutResponse putResponse = client.put(storeName, key, value).join();
    assertThat(putResponse).isInstanceOf(PutResponse.Success.class);

    // Successful Get
    final GetResponse getResponse = client.get(storeName, key).join();
    assertThat(getResponse).isInstanceOf(GetResponse.Success.class);
    assertThat(((GetResponse.Success) getResponse).valueString()).isEqualTo(value);
  }

  @Test
  void getReturnsValueAsByteArrayAfterPut() {
    final String key = randomString("key");
    final String value = randomString("value");

    // Successful Set
    final PutResponse putResponse = client.put(storeName, key, value.getBytes()).join();
    assertThat(putResponse).isInstanceOf(PutResponse.Success.class);

    // Successful Get
    final GetResponse getResponse = client.get(storeName, key).join();
    assertThat(getResponse).isInstanceOf(GetResponse.Success.class);
    assertThat(((GetResponse.Success) getResponse).valueByteArray()).isEqualTo(value.getBytes());
  }

  @Test
  void getReturnsValueAsLongAfterPut() {
    final String key = randomString("key");
    final long value = 42L;

    // Successful Set
    final PutResponse putResponse = client.put(storeName, key, value).join();
    assertThat(putResponse).isInstanceOf(PutResponse.Success.class);

    // Successful Get
    final GetResponse getResponse = client.get(storeName, key).join();
    assertThat(getResponse).isInstanceOf(GetResponse.Success.class);
    assertThat(((GetResponse.Success) getResponse).valueLong()).isEqualTo(value);
  }

  @Test
  void getReturnsValueAsDoubleAfterPut() {
    final String key = randomString("key");
    final double value = 3.14;

    // Successful Set
    final PutResponse putResponse = client.put(storeName, key, value).join();
    assertThat(putResponse).isInstanceOf(PutResponse.Success.class);

    // Successful Get
    final GetResponse getResponse = client.get(storeName, key).join();
    assertThat(getResponse).isInstanceOf(GetResponse.Success.class);
    assertThat(((GetResponse.Success) getResponse).valueDouble()).isEqualTo(value);
  }

  @Test
  void storeKeyNotFound() {
    // Get key that was not set
    final GetResponse response = client.get(storeName, randomString("key")).join();
    assertThat(response).isInstanceOf(GetResponse.Error.class);
    assertThat(((GetResponse.Error) response).getCause()).isInstanceOf(NotFoundException.class);
  }

  @Test
  public void badStoreNameReturnsError() {
    final String storeName = randomString("name");

    final GetResponse getResponse = client.get(storeName, "").join();
    assertThat(getResponse).isInstanceOf(GetResponse.Error.class);
    assertThat(((GetResponse.Error) getResponse)).hasCauseInstanceOf(NotFoundException.class);

    final PutResponse putResponse = client.put(storeName, "", "").join();
    assertThat(putResponse).isInstanceOf(PutResponse.Error.class);
    assertThat(((PutResponse.Error) putResponse)).hasCauseInstanceOf(NotFoundException.class);
  }

  @Test
  public void allowEmptyKeyValuesOnGet() throws Exception {
    final String emptyKey = "";
    final String emptyValue = "";
    client.put(storeName, emptyKey, emptyValue).get();
    final GetResponse response = client.get(storeName, emptyKey).get();
    assertThat(response).isInstanceOf(GetResponse.Success.class);
    assertThat(((GetResponse.Success) response).valueString()).isEqualTo(emptyValue);
  }

  @Test
  public void deleteHappyPath() throws Exception {
    final String key = "key";
    final String value = "value";

    client.put(storeName, key, value).get();
    final GetResponse getResponse = client.get(storeName, key).get();
    assertThat(getResponse).isInstanceOf(GetResponse.Success.class);
    assertThat(((GetResponse.Success) getResponse).valueString()).isEqualTo(value);

    final DeleteResponse deleteResponse = client.delete(storeName, key).get();
    assertThat(deleteResponse).isInstanceOf(DeleteResponse.Success.class);

    final GetResponse getAfterDeleteResponse = client.get(storeName, key).get();
    assertThat(getAfterDeleteResponse).isInstanceOf(GetResponse.Success.class);
  }
  /*

  @Test
    public void getWithShortTimeoutReturnsError() {
        try (final StoreClient client =
                     StoreClient.builder(
                                     credentialProvider,
                                     Configurations.Laptop.latest().withTimeout(Duration.ofMillis(1)),
                                     DEFAULT_ITEM_TTL_SECONDS)
                             .build()) {

            final GetResponse response = client.get(storeName, "key").join();
            assertThat(response).isInstanceOf(GetResponse.Error.class);
            assertThat(((GetResponse.Error) response)).hasCauseInstanceOf(TimeoutException.class);
        }
    }


   */
}