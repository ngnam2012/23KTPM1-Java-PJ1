package core;

import io.*;
import model.*;

import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

public class SlangDictionary {
    private final FileManager file;
    private final Map<String, List<String>> dict = new HashMap<>();
    private final Map<String, Set<String>> inverted =  new HashMap<>();
    private final Random rnd = new Random();
    public SlangDictionary(FileManager file) {
        this.file = file;
    }

    public void load() throws IOException {
        dict.clear();
        inverted.clear();
        for (Entry e : file.loadAll()) {
            dict.put(e.getSlang(), new ArrayList<>(e.getDefinitions()));
            indexEntry(e.getSlang(), e.getDefinitions());
        }
    }

    public void indexEntry(String slang, List<String> definitions) {
        for (String d : definitions) {
            for (String token : d.toLowerCase().split("[^a-z0-9]+")) {
                if (token.isEmpty()) continue;
                inverted.computeIfAbsent(token, k -> new HashSet<>()).add(slang);
            }
        }
    }

    public Optional<List<String>> getSlang(String slang) {
        List<String> definitions = dict.get(slang);
        return definitions == null ? Optional.empty() : Optional.of(definitions);
    }

    public List<Entry> searchByDefinition(String query) {
        String[] tokens = query.toLowerCase().split("[^a-z0-9]+");
        Set<String> result = null;
        for (String t : tokens) {
            if (t.isEmpty()) continue;
            Set<String> s = inverted.getOrDefault(t, Collections.emptySet());
            result = (result == null) ? new HashSet<>(s) : intersect(result, s);
            if (result.isEmpty()) break;
        }
        if (result == null) result = Collections.emptySet();
        return result.stream().sorted().map(k -> new Entry(k, dict.get(k))).collect(Collectors.toList());
    }
    public static Set<String> intersect(Set<String> a, Set<String> b) {
        if (a.size() > b.size()) {
            Set<String> t = a;
            a = b;
            b = t;
        }
        Set<String> res = new HashSet<>();
        for (String s : a) {
            if (b.contains(s)) res.add(s);
        }
        return res;
    }

    public boolean addOrAppend(String slang, List<String> newDefs, boolean appendIfExists) {
        slang = slang.trim();
        if (slang.isEmpty() || newDefs.isEmpty()) return false;
        List<String> defs = dict.get(slang);
        if (defs == null) {
            dict.put(slang, new ArrayList<>(newDefs));
            indexEntry(slang, newDefs);
            return true;
        } else {
            if (!appendIfExists) return false;
            boolean added = false;
            for (String d : newDefs) {
                if (!defs.contains(d)) {
                    defs.add(d);
                    added = true;
                }
            }
            if (added) indexEntry(slang, newDefs);
            return added;
        }
    }

    private void rebuildInverted() {
        inverted.clear();
        for (Map.Entry<String, List<String>> e : dict.entrySet())
            indexEntry(e.getKey(), e.getValue());
    }

    public boolean delete(String slang) {
        if (dict.remove(slang) == null) return false;
        rebuildInverted();
        return true;
    }

    public boolean edit(String slang, List<String> newDefs) {
        if (!dict.containsKey(slang)) return false;
        dict.put(slang, new ArrayList<>(newDefs));
        rebuildInverted();
        return true;
    }

    public List<Entry> allSorted() {
        return dict.keySet().stream().sorted()
                .map(k -> new Entry(k, dict.get(k))).collect(Collectors.toList());
    }

    public Entry random() {
        if (dict.isEmpty())
            return new Entry("N/A", List.of("Empty dictionary"));
        int pos = rnd.nextInt(dict.size());
        String key = new ArrayList<>(dict.keySet()).get(pos);
        return new Entry(key, dict.get(key));
    }

    public void save() throws IOException {
        file.saveAll(allSorted());
    }
}

