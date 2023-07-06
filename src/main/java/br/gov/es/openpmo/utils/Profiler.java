package br.gov.es.openpmo.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Profiler {

    private static final Logger logger = LoggerFactory.getLogger(Profiler.class);
    private static final String TMP_PATH = "/opt/open-pmo/tmp/";
    private static final String TMP_FILE = "profiler.txt";
    private static final Map<UUID, String> contents = new LinkedHashMap<>();

    private static final AtomicInteger level = new AtomicInteger(-1);

    private final String className;
    private final UUID uuid;

    private String methodName;
    private Instant instant;

    private Profiler(String className) {
        this.className = className;
        this.uuid = UUID.randomUUID();
    }

    public static Profiler of(String className) {
        return new Profiler(className);
    }

    public void start(String methodName) {
        this.methodName = methodName;
        contents.put(uuid, null);
        level.incrementAndGet();
        instant = Instant.now();
    }

    public void end() {
        final Instant endInstant = Instant.now();
        String content = getContent(endInstant);
        this.write(content);
        contents.put(uuid, content);
        if (level.get() == 0) {
            contents.values().forEach(this::write);
            contents.clear();
        }
        level.decrementAndGet();
    }

    private String getContent(Instant endInstant) {
        final String levelArrow = StringUtils.repeat("-----> ", level.get());
        String content = String.format("%s%s.%s\n", levelArrow, className, methodName);
        content += String.format("%sInício  - %s\n", levelArrow, instant);
        content += String.format("%sFim     - %s\n", levelArrow, endInstant);
        final long seconds = ChronoUnit.SECONDS.between(instant, endInstant);
        final long millis = ChronoUnit.MILLIS.between(instant, endInstant);
        content += String.format("%sDuração - %ss (%sms)\n\n", levelArrow, seconds, millis);
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
