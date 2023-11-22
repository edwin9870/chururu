package com.edwin.distributed.leader;

import com.edwin.distributed.leader.service.FileServiceBaseImpl;
import com.edwin.distributed.leader.service.MerkleServiceBaseImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

public class Main {

    public static final int GRPC_PORT = 50_000;

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(GRPC_PORT)
                .addService(new MerkleServiceBaseImpl())
                .addService(new FileServiceBaseImpl())
                .build();
        server.start();
        System.out.println("server started. Listening on port : " +
                GRPC_PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request.");
            server.shutdown();
            System.out.println("server stopped.");
        }));
        server.awaitTermination();
    }

}
