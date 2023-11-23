package com.edwin.distributed.slave.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public final class Connection {
    private static final int GRPC_PORT = 50_000;
    private static final String HOST = "localhost";
    private static ManagedChannel channel;

    private Connection() {
    }

    public static ManagedChannel getConnection() {
        if(channel != null) {
            return channel;
        }
        channel = ManagedChannelBuilder.forAddress(HOST, GRPC_PORT).usePlaintext().build();
        return channel;
    }
}
