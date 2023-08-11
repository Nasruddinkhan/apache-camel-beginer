package com.mypractice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileCopierWithJava17 {

    public static void main(String[] args) throws IOException {
        Path inboxDirectory = Path.of("D:\\Projects\\sftp-inbound");
        Path outboxDirectory = Path.of("D:\\Projects\\sftp-inbound\\files");
        Files.createDirectories(outboxDirectory);
        try (var directoryStream = Files.newDirectoryStream(inboxDirectory)) {
            for (var source : directoryStream) {
                if (Files.isRegularFile(source)) {
                    Path dest = outboxDirectory.resolve(source.getFileName());
                    copyFile(source, dest);
                }
            }
        }
    }

    private static void copyFile(Path source, Path dest) throws IOException {
        Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
    }
}
