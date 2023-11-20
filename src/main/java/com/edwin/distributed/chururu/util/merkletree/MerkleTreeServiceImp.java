package com.edwin.distributed.chururu.util.merkletree;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.SignedBytes;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;

public class MerkleTreeServiceImp implements MerkleTreeService {

    List<MerkleNode> leafs;
    private Comparator<MerkleNode> merkleNodeComparator;

    public MerkleTreeServiceImp() {
        leafs = new ArrayList<>();
        merkleNodeComparator = (a, b) -> SignedBytes.lexicographicalComparator().compare(a.hash, b.hash);
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
    public MerkleNode generateMerkleTree(List<byte[]> hashes) {
        ensureEven(hashes);
        ArrayDeque<MerkleNode> queue = new ArrayDeque<>();
        hashes.stream().map(MerkleNode::new).forEach(e -> {
            queue.add(e);
            leafs.add(e);
        });
        leafs.sort(merkleNodeComparator);
        while (queue.size() > 1) {
            int n = queue.size();
            for (int i = 0;i < n;i +=2) {
                MerkleNode left = queue.poll();
                MerkleNode right = queue.poll();
                byte[] parentHash = generateHash(left.getHash(), right.getHash());
                MerkleNode parent = new MerkleNode(parentHash, null, left, right);
                left.setParent(parent);
                right.setParent(parent);
                queue.add(parent);
            }
        }
        return queue.poll();
    }

    @Override
    public void ensureEven(List<byte[]> hashes) {
        if(hashes.size() % 2 == 0) {
            return;
        }
        hashes.add(hashes.get(hashes.size() -1));
    }

    @Override
    public byte[] generateHash(byte[] hash1, byte[] hash2) {
        return hash(Bytes.concat(hash1, hash2));
    }

    /**
     * Null if a root node is passed.
     * Time Complexity: H (height of the tree)
     * @param leaf
     * @return
     */
    @Override
    public List<ProofItem> generateProof(MerkleNode leaf) {
        if(leaf.parent == null) {
            return null;
        }

        MerkleNode current = leaf;
        List<ProofItem> items = new ArrayList<>();
        if (current.parent.left == current) {
            items.add(new ProofItem(current.getHash(), NodeSide.LEFT));
        } else {
            items.add(new ProofItem(current.getHash(), NodeSide.RIGHT));
        }
        while (current.parent != null) {
            MerkleNode sibling;
            if(current.parent.left == current) {
                sibling = current.parent.right;
                items.add(new ProofItem(sibling.hash, NodeSide.RIGHT));
            } else {
                sibling = current.parent.left;
                items.add(new ProofItem(sibling.hash, NodeSide.LEFT));
            }
            current = current.parent;
        }
        return items;
    }

    /**
     * Return null if the leaf node is not found.
     *
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
                hash = generateHash(newElement.hash(), hash);
            } else {
                hash = generateHash(hash, newElement.hash());
            }
        }
        return new MerkleNode((hash));
    }

    public static byte[] hash(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String bytesToText(byte[] hash) {
        String str = "0x";
        str = str.concat(HexFormat.of().formatHex(hash));
        return str;
    }
}
