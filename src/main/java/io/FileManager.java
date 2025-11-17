package io;
import model.Entry;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileManager {
    private final File dataFile;

    public FileManager(String dataPath) {
        this.dataFile = new File(dataPath);
    }
    public List<Entry> loadAll() throws IOException {
        List<Entry> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader( new InputStreamReader
                (new FileInputStream(dataFile), StandardCharsets.UTF_8))){
            String line;
            while((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String slang = "";
                String defsRaw = "";
                if (line.contains("`")) {
                    int idx = line.indexOf("`");
                    slang = line.substring(0, idx).trim();
                    defsRaw = line.substring(idx + 1).trim();
                }
                else {
                    continue;
                }
                List<String> defs = new ArrayList<>();
                String[] defsArray = defsRaw.split("\\|");
                for (String def : defsArray) {
                    String t = def.trim();
                    if (!t.isEmpty()) {
                        defs.add(t);
                    }
                }
                if (!slang.isEmpty() && !defs.isEmpty()) {
                    out.add(new Entry(slang, defs));
                }
            }
        }
        return out;
    }
    public void saveAll(Collection<Entry> entries) throws IOException {
        try (PrintWriter pw = new PrintWriter( new OutputStreamWriter
                (new FileOutputStream(dataFile, false), StandardCharsets.UTF_8))) {
            for (Entry e : entries) pw.println(e.toDataLine());
        }
    }
}
