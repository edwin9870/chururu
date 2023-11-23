package com.edwin.distributed.merkletree;

import com.edwin.distributed.merkletree.util.HashUtil;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.SignedBytes;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MerkleTreeServiceImp implements MerkleTreeService {

    List<MerkleNode> leafs;
    private final Comparator<MerkleNode> merkleNodeComparator;

    public MerkleTreeServiceImp() {
        leafs = new ArrayList<>();
        merkleNodeComparator = (a, b) -> SignedBytes.lexicographicalComparator().compare(a.getHash(), b.getHash());
    }

    /**
     * Time Complexity: O((n * log n) +  (Log N) * 2)
     *  Sorting: O(n∗logn)
     *  Processing: O((Log N) * 2)
     *
     * @param hashes
     * @return
     */
    @Override
    public MerkleNode generateMerkleTree(List<MerkleNode> hashes) {
        ensureEven(hashes);
        ArrayDeque<MerkleNode> queue = new ArrayDeque<>(hashes);
        leafs.addAll(hashes);

        leafs.sort(merkleNodeComparator);
        while (queue.size() > 1) {
            int n = queue.size();
            for (int i = 0;i < n;i +=2) {
                MerkleNode left = queue.poll();
                MerkleNode right = queue.poll();
                byte[] parentHash = combine(left.getHash(), right.getHash());
                MerkleNode parent = new MerkleNode(parentHash, null, left, right);
                left.setParent(parent);
                right.setParent(parent);
                queue.add(parent);
            }
        }
        return queue.poll();
    }

    private void ensureEven(List<MerkleNode> hashes) {
        if(hashes.size() % 2 == 0) {
            return;
        }
        hashes.add(hashes.get(hashes.size() -1));
    }

    @Override
    public byte[] combine(byte[] hash1, byte[] hash2) {
        return HashUtil.hash(Bytes.concat(hash1, hash2));
    }

    /**
     * Null if a root node is passed.
     * Time Complexity: H (height of the tree)
     * @param leaf
     * @return
     */
    @Override
    public List<ProofItem> generateProof(MerkleNode leaf) {
        if(leaf.getParent() == null) {
            return null;
        }

        MerkleNode current = leaf;
        List<ProofItem> items = new ArrayList<>();
        if (current.getParent().getLeft() == current) {
            items.add(new ProofItem(current.getHash(), NodeSide.LEFT));
        } else {
            items.add(new ProofItem(current.getHash(), NodeSide.RIGHT));
        }
        while (current.getParent() != null) {
            MerkleNode sibling;
            if(current.getParent().getLeft() == current) {
                sibling = current.getParent().getRight();
                items.add(new ProofItem(sibling.getHash(), NodeSide.RIGHT));
            } else {
                sibling = current.getParent().getLeft();
                items.add(new ProofItem(sibling.getHash(), NodeSide.LEFT));
            }
            current = current.getParent();
        }
        return items;
    }

    /**
     * Return null if the leaf node is not found.
     * Time complexity: Log(n)
     *
     * @param hash
     * @param node
     * @return
     */
    @Override
    public MerkleNode getLeaf(byte[] hash, MerkleNode node) {
        if(node == null) return null;
        MerkleNode merkleNode = new MerkleNode(hash);
        int index = Collections.binarySearch(leafs, merkleNode, merkleNodeComparator);
        if(index < 0) {
            return null;
        }
        return leafs.get(index);
    }

    @Override
    public MerkleNode generateRootFromProof(List<ProofItem> proof) {
        ProofItem current = proof.get(0);
        byte[] hash = current.hash();
        for(int i = 1;i < proof.size();i++) {
            ProofItem newElement = proof.get(i);
            if(newElement.side() == NodeSide.LEFT) {
                hash = combine(newElement.hash(), hash);
            } else {
                hash = combine(hash, newElement.hash());
            }
        }
        return new MerkleNode((hash));
    }

    /**
     * Both Merkle Trees must be of the same height. It will return all node1 differences
     *
     * @param node1
     * @param node2
     * @return
     */
    @Override
    public List<MerkleNode> detectDifferences(MerkleNode node1, MerkleNode node2) {
        List<MerkleNode> res = new ArrayList<>();
        if(node1 == null && node2 == null) {
            return res;
        }
        diff(node1, node2, res);
        return res;
    }


    private void diff(MerkleNode node1, MerkleNode node2, List<MerkleNode> res) {
        if(node1.equals(node2)) return;

        if(isLeaf(node1) && isLeaf(node2)) {
            res.add(node1);
            return;
        }

        diff(node1.getLeft(), node2.getLeft(), res);
        diff(node1.getRight(), node2.getRight(), res);
    }

    private boolean isLeaf(MerkleNode node) {
        return node.getLeft() == null && node.getRight() == null;
    }
}
