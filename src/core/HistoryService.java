package core;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;

public class HistoryService {
    private final File logFile;
    private final DateTimeFormatter fmt =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public HistoryService(File logFile) {
        this.logFile = logFile;
    }
    private static String ellips(String s, int max) {
        if (s == null) return "";
        s = s.trim();
        return s.length() <= max ? s : s.substring(0, max-3) + "...";
    }

    public synchronized void logSearch(String type, String keyword, String result) {
        try {
            logFile.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(logFile, true), StandardCharsets.UTF_8))) {
                pw.printf("[%s] %-10s | %-30s | %s%n",
                        LocalDateTime.now().format(fmt), type, ellips(keyword, 30), ellips(result, 80));
            }
        } catch (IOException ignored) {}
    }

    public List<String> readAll() {
        List<String> result = new ArrayList<>();
        if (!logFile.exists()) return result;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(logFile), StandardCharsets.UTF_8))) {
            String line;
            while((line = br.readLine()) != null)
                result.add(line);
        }   catch (IOException ignored) {}
        return result;
    }
}
