package com.edwin.distributed.merkletree;

import java.util.Arrays;
import java.util.HexFormat;

public class MerkleNode {
    private byte[] hash;
    private String fileName;
    private MerkleNode parent;
    private MerkleNode left;
    private MerkleNode right;

    public MerkleNode(byte[] hash) {
        this.hash = hash;
    }

    public MerkleNode(byte[] hash, String fileName) {
        this.hash = hash;
        this.fileName = fileName;
    }

    public MerkleNode(byte[] hash, MerkleNode parent, MerkleNode left, MerkleNode right) {
        this.hash = hash;
        this.parent = parent;
        this.left = left;
        this.right = right;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public MerkleNode getParent() {
        return parent;
    }

    public void setParent(MerkleNode parent) {
        this.parent = parent;
    }

    public MerkleNode getLeft() {
        return left;
    }

    public void setLeft(MerkleNode left) {
        this.left = left;
    }

    public MerkleNode getRight() {
        return right;
    }

    public void setRight(MerkleNode right) {
        this.right = right;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "MerkleNode{" +
                "hash='0x" + HexFormat.of().formatHex(hash) + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MerkleNode that)) {
            return false;
        }

        return Arrays.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }
}
