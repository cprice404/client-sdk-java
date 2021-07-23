package client.sdk.java;

import io.grpc.*;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

public class AuthInterceptor implements ClientInterceptor {

    private String tokenValue;

    public AuthInterceptor(String token) {
        tokenValue = token;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                channel.newCall(methodDescriptor, callOptions)) {
            @Override
            public void start(Listener<RespT> listener, Metadata metadata) {
                metadata.put(Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER), tokenValue);
                super.start(listener, metadata);
            }
        };
    }
}
