package com.edwin.distributed.chururu.util.merkletree;

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
        byte[][] hashes = new byte[][]{"A".getBytes(StandardCharsets.UTF_8), "B".getBytes(
                StandardCharsets.UTF_8)};
        byte[] res = Bytes.concat(hashes);
        res = MerkleTreeServiceImp.hash(res);

        MerkleNode rootHash = merkleTreeService.generateMerkleTree(Arrays.asList(hashes));
        System.out.println(rootHash);
        Assertions.assertArrayEquals(res, rootHash.hash);
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
        MerkleNode root = merkleTreeService.generateMerkleTree(hashes);
        return root;
    }

//    @Test
//    void generateTree() {
//        List<String> hashes = List.of("A", "B", "C", "D");
//        List<List<String>> result = merkleTreeService.generateTree(hashes);
//        System.out.println(result);
//    }
}