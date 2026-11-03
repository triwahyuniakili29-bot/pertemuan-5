
package id.ac.trinita.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class AuditStorageService {
    private final ObjectMapper objectMapper;

    public AuditStorageService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Menulis daftar log ke berkas JSON Lines (NDJSON)
     * menggunakan Java NIO.2
     */
    public void exportLogsToJsonLines(
            Path targetFile, List<AuditRecord> records) throws IOException {

        if (targetFile.getParent() != null) {
            Files.createDirectories(targetFile.getParent());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                targetFile,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            for (AuditRecord record : records) {
                String jsonLine = objectMapper.writeValueAsString(record);
                writer.write(jsonLine);
                writer.newLine();
            }
        }
    }

    /**
     * Membaca dan memfilter log menggunakan Files.readAllLines()
     */
    public long countErrorEvents(Path logFile) throws IOException {
        List<String> lines =
                Files.readAllLines(logFile, StandardCharsets.UTF_8);

        return lines.stream()
                .map(line -> {
                    try {
                        return objectMapper.readValue(line, AuditRecord.class);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(record ->
                        record != null && record.statusCode() >= 400)
                .count();
    }
}
