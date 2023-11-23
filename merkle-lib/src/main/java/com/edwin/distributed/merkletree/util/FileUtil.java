package com.edwin.distributed.merkletree.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class FileUtil {

    private FileUtil() {
    }

    public static List<File> getFilesFromFolder(String folderPath) throws IOException {
        List<File> res = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            paths.filter(Files::isRegularFile).map(Path::toFile).forEach(res::add);
        }
        return res;
    }
}
