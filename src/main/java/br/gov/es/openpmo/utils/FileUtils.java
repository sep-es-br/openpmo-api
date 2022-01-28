package br.gov.es.openpmo.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

public final class FileUtils {

  private FileUtils() {
  }

  public static void storeFile(final String directory, final String fileName, final byte[] data) throws IOException {
    createDirectoryIfDoesNotExist(directory);
    final Path path = getPathObjectFromFileName(directory, fileName);
    final File file = createFileObject(path);
    tryStoringFile(file, data);
  }

  private static void tryStoringFile(final File file, final byte[] data) throws IOException {
    try (final FileOutputStream out = new FileOutputStream(file)) {
      out.write(data);
      out.flush();
    }
  }

  private static File createFileObject(final Path path) throws IOException {
    return Files.createFile(path).toFile();
  }

  private static Path getPathObjectFromFileName(final String directory, final String fileName) {
    final String filePath = MessageFormat.format("{0}{1}", directory, fileName);
    return Paths.get(filePath);
  }

  private static void createDirectoryIfDoesNotExist(final String path) throws IOException {
    final Path dir = Paths.get(path);
    if (!Files.exists(dir)) {
      Files.createDirectory(dir);
    }
  }

}
