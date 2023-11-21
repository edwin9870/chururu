package com.edwin.distributed.merkletree.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtil {

    private static final String ALGORITHM = "SHA-256";

    private HashUtil() {
    }

    public static byte[] hash(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            return digest.digest(input);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
