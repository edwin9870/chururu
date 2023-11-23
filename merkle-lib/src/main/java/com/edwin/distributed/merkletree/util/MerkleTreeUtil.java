package com.edwin.distributed.merkletree.util;

import com.edwin.distributed.schema.MerkleResponse.Merkle;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

public final class MerkleTreeUtil {

    private MerkleTreeUtil() {
    }

    public static List<Merkle> getLeafs(List<File> filesFromFolder) {
        return filesFromFolder.stream().map(e -> {
            try {
                byte[] fileBytes = Files.toByteArray(e);
                String name = e.getName();
                return Merkle.newBuilder()
                        .setHash(HexFormat.of().formatHex(Objects.requireNonNull(HashUtil.hash(fileBytes))))
                        .setFileName(name)
                        .build();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }).toList();
    }
}
