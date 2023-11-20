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
        byte[][] hashes = new byte[][]{"A".getBytes(StandardCharsets.UTF_8), "Å".getBytes(
                StandardCharsets.UTF_8)};
        byte[] res = Bytes.concat(hashes);
        res = MerkleTreeServiceImp.hash(res);

        MerkleNode rootHash = merkleTreeService.generateMerkleTree(Arrays.asList(hashes));
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
        return merkleTreeService.generateMerkleTree(hashes);
    }

    @Test
    void generateTree() {
        List<byte[]> hashes = new ArrayList<>(List.of("A".getBytes(StandardCharsets.UTF_8), "B".getBytes(
                StandardCharsets.UTF_8), "C".getBytes(StandardCharsets.UTF_8), "D".getBytes(StandardCharsets.UTF_8)));
        hashes.forEach(e -> {
            System.out.println("Length array byte -> " + e.length);
        });
        MerkleNode merkleNode = merkleTreeService.generateMerkleTree(hashes);
        Assertions.assertNotNull(merkleNode);
    }
}