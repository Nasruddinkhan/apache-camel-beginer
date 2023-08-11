package com.mypractice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileCopierWithJavaFunctionalWay {
    public static void main(String[] args) throws IOException {
        Path inboxDirectory = Path.of("D:\\Projects\\sftp-inbound");
        Path outboxDirectory = Path.of("D:\\Projects\\sftp-inbound\\files");
        Files.createDirectories(outboxDirectory);
        try (Stream<Path> directoryStream = Files.list(inboxDirectory)) {
            directoryStream
                    .filter(Files::isRegularFile)
                    .forEach(source -> {
                        Path dest = outboxDirectory.resolve(source.getFileName());
                        try {
                            copyFile(source, dest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    private static void copyFile(Path source, Path dest) throws IOException {
        Files.copy(source, dest);
    }
}
