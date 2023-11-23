package com.edwin.distributed.leader.service;

import com.edwin.distributed.merkletree.util.FileUtil;
import com.edwin.distributed.merkletree.util.MerkleTreeUtil;
import com.edwin.distributed.schema.MerkleRequest;
import com.edwin.distributed.schema.MerkleResponse;
import com.edwin.distributed.schema.MerkleResponse.Merkle;
import com.edwin.distributed.schema.MerkleTreeServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class MerkleServiceBaseImpl extends MerkleTreeServiceGrpc.MerkleTreeServiceImplBase {

    public static final String DATA_DIR_PATH = "/Users/eramirez/workspace/java/chururu/data/leader";

    @Override
    public void get(MerkleRequest request, StreamObserver<MerkleResponse> responseObserver) {
        System.out.println("Request received!");
        try {
            List<File> filesFromFolder =
                    FileUtil.getFilesFromFolder(DATA_DIR_PATH);
            List<Merkle> leafs = MerkleTreeUtil.getLeafs(filesFromFolder);
            MerkleResponse merkleResponse = MerkleResponse.newBuilder()
                    .addAllLeafs(leafs)
                    .build();


            responseObserver.onNext(merkleResponse);
            responseObserver.onCompleted();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
