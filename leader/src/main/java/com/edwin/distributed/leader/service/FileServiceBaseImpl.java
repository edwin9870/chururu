package com.edwin.distributed.leader.service;

import com.edwin.distributed.schema.file.FileChunk;
import com.edwin.distributed.schema.file.FileDownloadRequest;
import com.edwin.distributed.schema.file.FileServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileServiceBaseImpl extends FileServiceGrpc.FileServiceImplBase {

    @Override
    public void download(FileDownloadRequest request, StreamObserver<FileChunk> responseObserver) {
        try {
            Path filePath = Paths.get(MerkleServiceBaseImpl.DATA_DIR_PATH, request.getFileName());
            System.out.println("Downloading file: " + filePath);
            byte[] fileBytes = Files.readAllBytes(filePath);
            for (int i = 0; i < fileBytes.length; i += 4096) {
                int length = Math.min(4096, fileBytes.length - i);
                byte[] chunk = new byte[length];
                System.arraycopy(fileBytes, i, chunk, 0, length);

                FileChunk fileChunk = FileChunk.newBuilder()
                        .setData(com.google.protobuf.ByteString.copyFrom(chunk))
                        .build();
                responseObserver.onNext(fileChunk);
            }
            responseObserver.onCompleted();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
