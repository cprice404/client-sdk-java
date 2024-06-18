package momento.sdk;

import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import momento.sdk.auth.CredentialProvider;
import momento.sdk.config.StorageConfiguration;
import momento.sdk.responses.storage.control.CreateStoreResponse;
import momento.sdk.responses.storage.control.DeleteStoreResponse;
import momento.sdk.responses.storage.control.ListStoresResponse;
import momento.sdk.responses.storage.data.DeleteResponse;
import momento.sdk.responses.storage.data.GetResponse;
import momento.sdk.responses.storage.data.PutResponse;

public class PreviewStorageClient implements IPreviewStorageClient, AutoCloseable {
  private final StorageControlClient controlClient;
  private final StorageDataClient dataClient;

  public PreviewStorageClient(
      @Nonnull CredentialProvider credentialProvider, @Nonnull StorageConfiguration configuration) {
    this.controlClient = new StorageControlClient(credentialProvider, configuration);
    this.dataClient = new StorageDataClient(credentialProvider, configuration);
  }

  /** Control operations */
  public CompletableFuture<CreateStoreResponse> createStore(String storeName) {
    return this.controlClient.createStore(storeName);
  }

  public CompletableFuture<DeleteStoreResponse> deleteStore(String storeName) {
    return this.controlClient.deleteStore(storeName);
  }

  public CompletableFuture<ListStoresResponse> listStores() {
    return this.controlClient.listStores();
  }

  /** Data operations */
  public CompletableFuture<GetResponse> get(String storeName, String key) {
    return this.dataClient.get(storeName, key);
  }

  public CompletableFuture<PutResponse> put(String storeName, String key, byte[] value) {
    return this.dataClient.put(storeName, key, value);
  }

  public CompletableFuture<PutResponse> put(String storeName, String key, String value) {
    return this.dataClient.put(storeName, key, value);
  }

  public CompletableFuture<PutResponse> put(String storeName, String key, long value) {
    return this.dataClient.put(storeName, key, value);
  }

  public CompletableFuture<PutResponse> put(String storeName, String key, double value) {
    return this.dataClient.put(storeName, key, value);
  }

  public CompletableFuture<DeleteResponse> delete(String storeName, String key) {
    return this.dataClient.delete(storeName, key);
  }

  @Override
  public void close() {
    this.controlClient.close();
    this.dataClient.close();
  }
}
