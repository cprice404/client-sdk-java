package momento.sdk;

import static momento.sdk.ValidationUtils.checkCacheNameValid;

import grpc.control_client._Cache;
import grpc.control_client._CreateCacheRequest;
import grpc.control_client._DeleteCacheRequest;
import grpc.control_client._ListCachesRequest;
import grpc.control_client._ListCachesResponse;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import momento.sdk.exceptions.CacheServiceExceptionMapper;
import momento.sdk.messages.CacheInfo;
import momento.sdk.messages.CreateCacheResponse;
import momento.sdk.messages.DeleteCacheResponse;
import momento.sdk.messages.ListCachesResponse;
import org.apache.commons.lang3.StringUtils;

/** Client for interacting with Scs Control Plane. */
final class ScsControlClient implements Closeable {

  private final ScsControlGrpcStubsManager controlGrpcStubsManager;

  ScsControlClient(String authToken, String endpoint) {
    this.controlGrpcStubsManager = new ScsControlGrpcStubsManager(authToken, endpoint);
  }

  CreateCacheResponse createCache(String cacheName) {
    checkCacheNameValid(cacheName);
    try {
      controlGrpcStubsManager.getBlockingStub().createCache(buildCreateCacheRequest(cacheName));
      return new CreateCacheResponse();
    } catch (Exception e) {
      throw CacheServiceExceptionMapper.convert(e);
    }
  }

  DeleteCacheResponse deleteCache(String cacheName) {
    checkCacheNameValid(cacheName);
    try {
      controlGrpcStubsManager.getBlockingStub().deleteCache(buildDeleteCacheRequest(cacheName));
      return new DeleteCacheResponse();
    } catch (Exception e) {
      throw CacheServiceExceptionMapper.convert(e);
    }
  }

  ListCachesResponse listCaches(Optional<String> nextToken) {
    try {
      return convert(controlGrpcStubsManager.getBlockingStub().listCaches(convert(nextToken)));
    } catch (Exception e) {
      throw CacheServiceExceptionMapper.convert(e);
    }
  }

  private static _CreateCacheRequest buildCreateCacheRequest(String cacheName) {
    return _CreateCacheRequest.newBuilder().setCacheName(cacheName).build();
  }

  private static _DeleteCacheRequest buildDeleteCacheRequest(String cacheName) {
    return _DeleteCacheRequest.newBuilder().setCacheName(cacheName).build();
  }

  private static _ListCachesRequest convert(Optional<String> nextToken) {
    String grpcNextToken = nextToken == null || !nextToken.isPresent() ? "" : nextToken.get();
    return _ListCachesRequest.newBuilder().setNextToken(grpcNextToken).build();
  }

  private static ListCachesResponse convert(_ListCachesResponse response) {
    List<CacheInfo> caches = new ArrayList<>();
    for (_Cache cache : response.getCacheList()) {
      caches.add(convert(cache));
    }
    Optional<String> nextPageToken =
        StringUtils.isEmpty(response.getNextToken())
            ? Optional.empty()
            : Optional.of(response.getNextToken());
    return new ListCachesResponse(caches, nextPageToken);
  }

  private static CacheInfo convert(_Cache cache) {
    return new CacheInfo(cache.getCacheName());
  }

  @Override
  public void close() {
    controlGrpcStubsManager.close();
  }
}