package com.edwin.distributed.leader.service;

import com.edwin.distributed.leader.util.FileUtil;
import com.edwin.distributed.merkletree.util.HashUtil;
import com.edwin.distributed.schema.MerkleRequest;
import com.edwin.distributed.schema.MerkleResponse;
import com.edwin.distributed.schema.MerkleResponse.Merkle;
import com.edwin.distributed.schema.MerkleTreeServiceGrpc;
import com.google.common.io.Files;
import io.grpc.stub.StreamObserver;
import java.io.File;
import java.io.IOException;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

public class MerkleServiceBaseImpl extends MerkleTreeServiceGrpc.MerkleTreeServiceImplBase {

    public static final String DATA_DIR_PATH = "/Users/eramirez/workspace/java/chururu/data";

    @Override
    public void get(MerkleRequest request, StreamObserver<MerkleResponse> responseObserver) {
        System.out.println("Request received!");
        try {
            List<File> filesFromFolder =
                    FileUtil.getFilesFromFolder(DATA_DIR_PATH);
            List<Merkle> leafs = getLeafs(filesFromFolder);
            MerkleResponse merkleResponse = MerkleResponse.newBuilder()
                    .addAllLeafs(leafs)
                    .build();


            responseObserver.onNext(merkleResponse);
            responseObserver.onCompleted();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Merkle> getLeafs(List<File> filesFromFolder) {
        return filesFromFolder.stream().map(e -> {
            try {
                byte[] fileBytes = Files.toByteArray(e);
                String name = e.getName();
                return Merkle.newBuilder()
                        .setHash(HexFormat.of().formatHex(Objects.requireNonNull(HashUtil.hash(fileBytes))))
                        .setFileName(name)
                        .build();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }).toList();
    }
}
