package momento.client.example;

import java.time.Duration;
import java.util.Map;

import momento.sdk.CacheClient;
import momento.sdk.PreviewStorageClient;
import momento.sdk.auth.CredentialProvider;
import momento.sdk.auth.EnvVarCredentialProvider;
import momento.sdk.config.Configurations;
import momento.sdk.config.StorageConfigurations;
import momento.sdk.exceptions.AlreadyExistsException;
import momento.sdk.responses.cache.control.CacheCreateResponse;
import momento.sdk.responses.cache.control.CacheInfo;
import momento.sdk.responses.cache.control.CacheListResponse;
import momento.sdk.responses.storage.data.GetResponse;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

public class BasicExample {

  private static final String API_KEY_ENV_VAR = "MOMENTO_API_KEY";
  private static final Duration DEFAULT_ITEM_TTL = Duration.ofSeconds(60);

  private static final String CACHE_NAME = "cache";
  private static final String KEY = "key";
  private static final String VALUE = "value";

  public static void main(String[] args) {
//    printStartBanner();

    DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_2)
            .build();

    var tableName = "DELETEME-ddbtest";

    dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName(tableName)
                    .item(Map.of(
                            "id", AttributeValue.builder().s("1").build(),
                            "number", AttributeValue.builder().n("42").build(),
                            "bytes", AttributeValue.builder().b(SdkBytes.fromByteArray(
                                    new byte[]{42, 90, 21, 0}
                            )).build()
                    ))
                    .build());

    System.out.println("Table created and item added");

    var getItemResponse = dynamoDbClient.getItem(GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                    "id", AttributeValue.builder().s("1").build()
            ))
            .build());

    System.out.println("Get item response: " + getItemResponse);
    var number = getItemResponse.item().get("number").n();
    System.out.println("Number: " + number);
    var numberBytes = getItemResponse.item().get("number").b();
    System.out.println("Number bytes: " + numberBytes);

    var nonExistingItemResponse = dynamoDbClient.getItem(GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of(
                    "id", AttributeValue.builder().s("2").build()
            ))
            .build());
    System.out.println("Non-existing item response: " + nonExistingItemResponse);
    var nonExistingItem = nonExistingItemResponse.item();
    System.out.println("Non-existing item: " + nonExistingItem);

    try {
      var nonExistingItemNumberAsString = nonExistingItemResponse.item().get("number").s();
      System.out.println("Non-existing item number as string: " + nonExistingItemNumberAsString);
    } catch (NullPointerException e) {
        System.out.println("NPE trying to dereference non-existing item");
    }

    final CredentialProvider credentialProvider = new EnvVarCredentialProvider(API_KEY_ENV_VAR);
    var storageClient = new PreviewStorageClient(credentialProvider, StorageConfigurations.Laptop.latest());
    var storeName = "store";
    storageClient.createStore(storeName).join();
    storageClient.put(storeName, "stringKey", "stringValue").join();
    var stringGetResponse = storageClient.get(storeName, "stringKey").join();
    var stringGetTryValue = stringGetResponse.tryValue().get().getString();
//    stringGetResponse.
    System.out.println("String get tryValue: " + stringGetTryValue);
    if (stringGetResponse instanceof GetResponse.Success stringGetSuccess) {
      System.out.println("String get success: " + stringGetSuccess.value().getString());
    }
  }

  private static void createCache(CacheClient cacheClient, String cacheName) {
    final CacheCreateResponse createResponse = cacheClient.createCache(cacheName).join();
    if (createResponse instanceof CacheCreateResponse.Error error) {
      if (error.getCause() instanceof AlreadyExistsException) {
        System.out.println("Cache with name '" + cacheName + "' already exists.");
      } else {
        System.out.println("Unable to create cache with error " + error.getErrorCode());
        System.out.println(error.getMessage());
      }
    }
  }

  private static void listCaches(CacheClient cacheClient) {
    System.out.println("Listing caches:");
    final CacheListResponse listResponse = cacheClient.listCaches().join();
    if (listResponse instanceof CacheListResponse.Success success) {
      for (CacheInfo cacheInfo : success.getCaches()) {
        System.out.println(cacheInfo.name());
      }
    } else if (listResponse instanceof CacheListResponse.Error error) {
      System.out.println("Unable to list caches with error " + error.getErrorCode());
      System.out.println(error.getMessage());
    }
  }

  private static void printStartBanner() {
    System.out.println("******************************************************************");
    System.out.println("Basic Example Start");
    System.out.println("******************************************************************");
  }

  private static void printEndBanner() {
    System.out.println("******************************************************************");
    System.out.println("Basic Example End");
    System.out.println("******************************************************************");
  }
}
