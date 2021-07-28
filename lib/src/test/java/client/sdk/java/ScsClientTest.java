/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package client.sdk.java;

import grpc.cache_client.Result;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

class ScsClientTest {
    ScsClient c;

    @BeforeEach
    void setUp() {
        c = new ScsClient(System.getenv("TEST_AUTH_TOKEN"));
    }

    @Test
    void testBlockingClientHappyPath() {
        try {
            String key = UUID.randomUUID().toString();

            //Set Key sync
            ClientSetResponse setRsp = c.set(
                    key,
                    ByteBuffer.wrap("bar".getBytes(StandardCharsets.UTF_8)),
                    10
            );
            Assertions.assertEquals(Result.Ok, setRsp.getResult());

            // Get Key that was just set
            ClientGetResponse<ByteBuffer> rsp = c.get(key);

            Assertions.assertEquals(Result.Hit, rsp.getResult());
            Assertions.assertEquals("bar", StandardCharsets.US_ASCII.decode(rsp.getBody()).toString());

        } catch (IOException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testAsyncClientHappyPath() {
        try {
            String key = UUID.randomUUID().toString();
            // Set Key Async
            CompletionStage<ClientSetResponse> setRsp = c.setAsync(
                    key,
                    ByteBuffer.wrap("bar".getBytes(StandardCharsets.UTF_8)),
                    10
            );
            Assertions.assertEquals(Result.Ok, setRsp.toCompletableFuture().get().getResult());

            // Get Key Async
            ClientGetResponse<ByteBuffer> rsp = c.getAsync(key).toCompletableFuture().get();

            Assertions.assertEquals(Result.Hit, rsp.getResult());
            Assertions.assertEquals("bar", StandardCharsets.US_ASCII.decode(rsp.getBody()).toString());

        } catch (IOException | InterruptedException | ExecutionException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testTtlHappyPath() {
        try {
            String key = UUID.randomUUID().toString();

            //Set Key sync
            ClientSetResponse setRsp = c.set(
                    key,
                    ByteBuffer.wrap("bar".getBytes(StandardCharsets.UTF_8)),
                    1
            );
            Assertions.assertEquals(Result.Ok, setRsp.getResult());

            Thread.sleep(1500);

            // Get Key that was just set
            ClientGetResponse<ByteBuffer> rsp = c.get(key);

            Assertions.assertEquals(Result.Miss, rsp.getResult());

        } catch (IOException | InterruptedException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void testMissHappyPath() {
        try {
            // Get Key that was just set
            ClientGetResponse<ByteBuffer> rsp = c.get(UUID.randomUUID().toString());

            Assertions.assertEquals(Result.Miss, rsp.getResult());

        } catch (IOException e) {
            Assertions.fail(e);
        }
    }


    @Test
    void testBadAuthToken() {
        ScsClient badCredClient = new ScsClient("BAD_TOKEN");
        try {
            // Get Key that was just set
            ClientGetResponse<ByteBuffer> rsp = badCredClient.get(UUID.randomUUID().toString());

            Assertions.fail("expected PERMISSION_DENIED io.grpc.StatusRuntimeException");

        } catch (IOException e) {
            Assertions.fail(e);
        } catch (io.grpc.StatusRuntimeException e) {

            // Make sure we get permission denied error the way we would expected
            Assertions.assertEquals(
                    new StatusRuntimeException(
                            Status.PERMISSION_DENIED
                                    .withDescription("Malformed authorization token")
                    ).toString(),
                    e.toString()
            );
        }

    }
}
