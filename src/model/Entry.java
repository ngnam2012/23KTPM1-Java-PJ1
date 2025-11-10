package model;

import java.util.*;

public class Entry {
    private final String slang;
    private final List<String> definitions;
    public Entry(String slang, List<String> defs) {
        this.slang = slang;
        this.definitions = new ArrayList<>();
        for (String def : defs) {
            if (def != null) {
                String t = def.trim();
                if (!t.isEmpty()) {this.definitions.add(t);}
            }
        }
    }
    public String getSlang() {
        return slang;
    }
    public List<String> getDefinitions() {
        return Collections.unmodifiableList(definitions);
    }
    public String toDataLine() {
        return slang + "`" + String.join("| ", definitions);
    }

    @Override
    public String toString() {
        return slang + " = " + String.join("; ", definitions);
    }
}
