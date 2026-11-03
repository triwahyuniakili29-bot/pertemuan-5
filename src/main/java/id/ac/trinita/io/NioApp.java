
package id.ac.trinita.io;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

public class NioApp {
    public static void main(String[] args) {

        System.out.println("=== SISTEM LOGGING AUDIT BERBASIS JAVA NIO.2 & JACKSON ===");

        Path storageDir = Paths.get("data", "logs");
        Path auditFile = storageDir.resolve("system-audit.ndjson");

        AuditStorageService storageService = new AuditStorageService();

        List<AuditRecord> dummyLogs = List.of(
            new AuditRecord("EVT-001", "admin", "AUTH_LOGIN",
                    "192.168.1.10", LocalDateTime.now(), 200),

            new AuditRecord("EVT-002", "guest", "ACCESS_RESOURCE",
                    "10.0.0.5", LocalDateTime.now(), 403),

            new AuditRecord("EVT-003", "developer", "UPDATE_SCHEMA",
                    "192.168.1.50", LocalDateTime.now(), 200),

            new AuditRecord("EVT-004", "hacker", "SQL_INJECTION_ATTEMPT",
                    "203.0.113.195", LocalDateTime.now(), 500)
        );

        try {
            // 1. Ekspor Data ke File Berbasis NIO.2
            System.out.println("\nMenyimpan log ke: " + auditFile.toAbsolutePath());

            storageService.exportLogsToJsonLines(auditFile, dummyLogs);

            System.out.println("[SUKSES] Berkas log berhasil ditulis!");

            // 2. Analisis File Log
            System.out.println("\nMemindai file log untuk mendeteksi event error/peringatan...");

            long errorCount = storageService.countErrorEvents(auditFile);

            System.out.println("[HASIL AUDIT] Total event berstatus error (>= 400): "
                    + errorCount);

        } catch (Exception e) {
            System.err.println("[ERROR] Terjadi kegagalan I/O: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
