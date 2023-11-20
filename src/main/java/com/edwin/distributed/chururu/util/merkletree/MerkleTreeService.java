package com.edwin.distributed.chururu.util.merkletree;

import java.util.List;

public interface MerkleTreeService {
    MerkleNode generateMerkleTree(List<byte[]> hashes);
    void ensureEven(List<byte[]> hashes);

    byte[] generateHash(byte[] hash1, byte[] hash2);

    List<ProofItem> generateProof(MerkleNode leaf);
    MerkleNode getLeaf(byte[] hash, MerkleNode root);
    MerkleNode generateRootFromProof(List<ProofItem> proof);
}
