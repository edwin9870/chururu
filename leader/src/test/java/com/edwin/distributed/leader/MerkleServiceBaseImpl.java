package com.edwin.distributed.leader;

import com.edwin.distributed.schema.MerkleRequest;
import com.edwin.distributed.schema.MerkleResponse;
import com.edwin.distributed.schema.MerkleResponse.Merkle;
import com.edwin.distributed.schema.MerkleTreeServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.util.UUID;

public class MerkleServiceBaseImpl extends MerkleTreeServiceGrpc.MerkleTreeServiceImplBase {

    @Override
    public void get(MerkleRequest request, StreamObserver<MerkleResponse> responseObserver) {
        System.out.println("Request received!");
        MerkleResponse merkleResponse = MerkleResponse.newBuilder()
                .addLeafs(Merkle.newBuilder()
                        .setHash(UUID.randomUUID().toString())
                        .setFileName("hi.txt")
                        .build())
                .build();
        responseObserver.onNext(merkleResponse);
        responseObserver.onCompleted();
    }
}
