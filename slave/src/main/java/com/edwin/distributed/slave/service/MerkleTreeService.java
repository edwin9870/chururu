package com.edwin.distributed.slave.service;

import com.edwin.distributed.merkletree.MerkleNode;
import com.edwin.distributed.merkletree.MerkleTreeServiceImp;
import com.edwin.distributed.merkletree.util.FileUtil;
import com.edwin.distributed.merkletree.util.HashUtil;
import com.edwin.distributed.merkletree.util.MerkleTreeUtil;
import com.edwin.distributed.schema.MerkleRequest;
import com.edwin.distributed.schema.MerkleResponse;
import com.edwin.distributed.schema.MerkleResponse.Merkle;
import com.edwin.distributed.schema.MerkleTreeServiceGrpc;
import com.edwin.distributed.schema.file.FileChunk;
import com.edwin.distributed.schema.file.FileDownloadRequest;
import com.edwin.distributed.schema.file.FileServiceGrpc;
import com.edwin.distributed.schema.file.FileServiceGrpc.FileServiceStub;
import com.edwin.distributed.slave.grpc.Connection;
import io.grpc.stub.StreamObserver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MerkleTreeService {

    private static final String DATA_PATH = "/Users/eramirez/workspace/java/chururu/data/slave1";

    public void sync() throws IOException, InterruptedException {
        List<MerkleNode> merkleNodeDiffs = getDiffNodes();
        System.out.println(merkleNodeDiffs);
        downLoadFiles(new ArrayDeque<>(merkleNodeDiffs));
    }

    void downLoadFiles(Queue<MerkleNode> fileToDownload) throws FileNotFoundException, InterruptedException {

        final CountDownLatch finishLatch = new CountDownLatch(fileToDownload.size());
        while (!fileToDownload.isEmpty()) {
            MerkleNode node = fileToDownload.poll();
            FileOutputStream outputStream = new FileOutputStream(Paths.get(DATA_PATH, node.getFileName()).toString());


            FileServiceStub fileServiceStub = FileServiceGrpc.newStub(Connection.getConnection());

            fileServiceStub.download(FileDownloadRequest.newBuilder()
                    .setFileName(node.getFileName())
                    .build(), new StreamObserver<>() {
                @Override
                public void onNext(FileChunk value) {
                    try {
                        byte[] byteArray = value.getData().toByteArray();
                        outputStream.write(byteArray);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onCompleted() {
                    try {
                        outputStream.close();
                        finishLatch.countDown();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        finishLatch.await(1L, TimeUnit.MINUTES);
    }

    private List<MerkleNode> getDiffNodes() throws IOException {
        List<MerkleNode> leaderLeafs =
                getLeaderMerkleTree().getLeafsList().stream().map(e -> new MerkleNode(e.getHashBytes().toByteArray(), e.getFileName())).collect(
                        Collectors.toCollection(ArrayList::new));
        List<MerkleNode> localLeafs = getLocalLeafs().stream().map(e -> new MerkleNode(e.getHashBytes().toByteArray(), e.getFileName())).collect(
                Collectors.toCollection(ArrayList::new));
        ensureEvenLocalLeafs(localLeafs, leaderLeafs);
        MerkleTreeServiceImp merkleServiceLeader = new MerkleTreeServiceImp();
        MerkleTreeServiceImp merkleServiceSlave = new MerkleTreeServiceImp();
        MerkleNode leaderMerkleNode = merkleServiceLeader.generateMerkleTree(leaderLeafs);
        MerkleNode slaveMerkleNode = merkleServiceSlave.generateMerkleTree(localLeafs);


        return new MerkleTreeServiceImp().detectDifferences(leaderMerkleNode, slaveMerkleNode);
    }

    private static void ensureEvenLocalLeafs(List<MerkleNode> localLeafs, List<MerkleNode> leaderMerkleTree) {
        if(localLeafs.size() < leaderMerkleTree.size()) {
            int diff = leaderMerkleTree.size() - localLeafs.size();
            byte[] random = HashUtil.hash(UUID.randomUUID().toString().getBytes());
            for(int i = 0;i < diff;i++) {
                localLeafs.add(new MerkleNode(random));
            }
        }
    }

    private List<Merkle> getLocalLeafs() throws IOException {
        List<File> filesFromFolder =
                FileUtil.getFilesFromFolder(DATA_PATH);
        List<Merkle> leafs = MerkleTreeUtil.getLeafs(filesFromFolder);
        MerkleTreeUtil.getLeafs(filesFromFolder);
        return leafs;
    }

    public MerkleResponse getLeaderMerkleTree() {
        return MerkleTreeServiceGrpc.newBlockingStub(Connection.getConnection())
                .get(MerkleRequest.getDefaultInstance());
    }
}
