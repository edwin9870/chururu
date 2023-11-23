package com.edwin.distributed.merkletree;

import java.util.List;

public interface MerkleTreeService {
    MerkleNode generateMerkleTree(List<MerkleNode> hashes);
    byte[] combine(byte[] hash1, byte[] hash2);
    List<ProofItem> generateProof(MerkleNode leaf);
    MerkleNode getLeaf(byte[] hash, MerkleNode root);
    MerkleNode generateRootFromProof(List<ProofItem> proof);
    List<MerkleNode> detectDifferences(MerkleNode node1, MerkleNode node2);
}
