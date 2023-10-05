package br.gov.es.openpmo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Profiler {

  private static final Logger logger = LoggerFactory.getLogger(Profiler.class);
  private static final String TMP_PATH = "/opt/open-pmo/tmp/";
  private static final String TMP_FILE = "profiler.txt";

  private final String className;
  private final String methodName;
  private Instant instant;

  private Profiler(String className, String methodName) {
    this.className = className;
    this.methodName = methodName;
  }

  public static Profiler of(String className, String methodName) {
    return new Profiler(className, methodName);
  }

  public void start() {
    instant = Instant.now();
  }

  public void end() {
    final Instant endInstant = Instant.now();
    String content = getContent(endInstant);
    this.write(content);
  }

  private String getContent(Instant endInstant) {
    String content = String.format("%s.%s\n", className, methodName);
    content += String.format("Início  - %s\n", instant);
    content += String.format("Fim     - %s\n", endInstant);
    final long seconds = ChronoUnit.SECONDS.between(instant, endInstant);
    final long millis = ChronoUnit.MILLIS.between(instant, endInstant);
    content += String.format("Duração - %ss (%sms)\n\n", seconds, millis);
    return content;
  }

  private void write(String content) {
    logger.info(content);
    try {
      Files.write(
        Paths.get(TMP_PATH, TMP_FILE),
        content.getBytes(StandardCharsets.UTF_8),
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE,
        StandardOpenOption.APPEND
      );
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
