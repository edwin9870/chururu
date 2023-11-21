package com.edwin.distributed.chururu.util.merkletree;

import com.edwin.distributed.merkletree.MerkleNode;
import com.edwin.distributed.merkletree.MerkleTreeService;
import com.edwin.distributed.merkletree.MerkleTreeServiceImp;
import com.edwin.distributed.merkletree.ProofItem;
import com.google.common.primitives.Bytes;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.*;

class MerkleTreeServiceImpTest {

    MerkleTreeService merkleTreeService;

    @BeforeEach
    void setup() {
        merkleTreeService = new MerkleTreeServiceImp();
    }

    @Test
    void getRoot() {
        byte[][] hashes = new byte[][]{"A".getBytes(StandardCharsets.UTF_8), "Å".getBytes(
                StandardCharsets.UTF_8)};
        byte[] res = Bytes.concat(hashes);
        res = MerkleTreeServiceImp.hash(res);

        MerkleNode rootHash = merkleTreeService.generateMerkleTree(Arrays.asList(hashes));
        Assertions.assertArrayEquals(res, rootHash.getHash());
    }

    @Test
    void verifyingProof() {
        MerkleNode root = getMerkleNode();

        MerkleNode leaf = merkleTreeService.getLeaf("B".getBytes(StandardCharsets.UTF_8), root);
        List<ProofItem> proof = merkleTreeService.generateProof(leaf);
        MerkleNode rootFromProof = merkleTreeService.generateRootFromProof(proof);
        Assertions.assertEquals(root, rootFromProof);
    }

    private MerkleNode getMerkleNode() {
        List<byte[]> hashes = new ArrayList<>(List.of("A".getBytes(StandardCharsets.UTF_8), "B".getBytes(
                StandardCharsets.UTF_8), "C".getBytes(StandardCharsets.UTF_8), "D".getBytes(StandardCharsets.UTF_8)));
        return merkleTreeService.generateMerkleTree(hashes);
    }

    @Test
    void generateTree() {
        List<byte[]> hashes = new ArrayList<>(List.of("A".getBytes(StandardCharsets.UTF_8), "B".getBytes(
                StandardCharsets.UTF_8), "C".getBytes(StandardCharsets.UTF_8), "D".getBytes(StandardCharsets.UTF_8)));
        MerkleNode merkleNode = merkleTreeService.generateMerkleTree(hashes);
        Assertions.assertNotNull(merkleNode);
    }

    @Test
    void shouldDetectDifferences() {
        byte[] differenceByte = "D".getBytes(StandardCharsets.UTF_8);
        List<byte[]> hashes = new ArrayList<>(List.of("A".getBytes(StandardCharsets.UTF_8), "B".getBytes(
                StandardCharsets.UTF_8), "C".getBytes(StandardCharsets.UTF_8), differenceByte));
        MerkleNode merkleNode1 = merkleTreeService.generateMerkleTree(hashes);

        hashes = new ArrayList<>(List.of("A".getBytes(StandardCharsets.UTF_8), "B".getBytes(
                StandardCharsets.UTF_8), "C".getBytes(StandardCharsets.UTF_8), "P".getBytes(StandardCharsets.UTF_8)));
        MerkleNode merkleNode2 = merkleTreeService.generateMerkleTree(hashes);

        List<MerkleNode> differences = merkleTreeService.detectDifferences(merkleNode1, merkleNode2);
        Assertions.assertEquals(differenceByte, differences.get(0).getHash());
    }
}