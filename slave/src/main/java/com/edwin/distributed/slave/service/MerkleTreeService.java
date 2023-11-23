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
import com.edwin.distributed.slave.grpc.Connection;
import io.grpc.ManagedChannel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MerkleTreeService {

    private static final String DATA_PATH = "/Users/eramirez/workspace/java/chururu/data/slave1";

    public void sync() throws IOException {
        List<MerkleNode> leaderLeafs =
                getLeaderMerkleTree().getLeafsList().stream().map(e -> new MerkleNode(e.getHashBytes().toByteArray(), e.getFileName())).collect(
                        Collectors.toCollection(ArrayList::new));
        List<MerkleNode> localLeafs = getLocalLeafs().stream().map(e -> new MerkleNode(e.getHashBytes().toByteArray(), e.getFileName())).collect(
                Collectors.toCollection(ArrayList::new));

        ensureEvenLocalLeafs(localLeafs, leaderLeafs);
        MerkleTreeServiceImp merkleTreeService = new MerkleTreeServiceImp();
        MerkleNode leaderMerkleNode = merkleTreeService.generateMerkleTree(leaderLeafs);
        MerkleNode slaveMerkleNode = merkleTreeService.generateMerkleTree(localLeafs);

        List<MerkleNode> merkleNodeDiffs = new MerkleTreeServiceImp().detectDifferences(leaderMerkleNode, slaveMerkleNode);
        System.out.println(merkleNodeDiffs);
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
