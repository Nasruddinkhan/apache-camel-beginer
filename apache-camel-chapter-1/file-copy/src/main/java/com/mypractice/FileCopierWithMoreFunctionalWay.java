package com.mypractice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileCopierWithMoreFunctionalWay {
    public static void main(String[] args) throws IOException {
        Path inboxDirectory = Path.of("D:\\Projects\\sftp-inbound");
        Path outboxDirectory = Path.of("D:\\Projects\\sftp-inbound\\files");
        Files.createDirectories(outboxDirectory);
        final ExceptionConsumer<Path, IOException> exceptionConsumer = (source) -> {
            Path dest = outboxDirectory.resolve(source.getFileName());
            Files.copy(source, dest);
        };
        try (var directoryStream = Files.newDirectoryStream(inboxDirectory)) {
            directoryStream.forEach(ExceptionHandling.handleExceptions(exceptionConsumer));
        }
    }


}


