package test;

import model.Entry;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Tạo danh sách nghĩa (definitions)
        List<String> defs = List.of("Emphatic Yes", "You are Sexy");

        // Tạo đối tượng Entry
        Entry e = new Entry("YAS", defs);

        // In ra các giá trị để kiểm tra
        System.out.println("🔹 Slang: " + e.getSlang());
        System.out.println("🔹 Definitions: " + e.getDefinitions());
        System.out.println("🔹 toString(): " + e.toString());
        System.out.println("🔹 toDataLine(): " + e.toDataLine());

        // Test bất biến (immutable list)
        try {
            e.getDefinitions().add("Another Meaning");
        } catch (UnsupportedOperationException ex) {
            System.out.println("✅ Không thể chỉnh sửa danh sách: " + ex);
        }
    }
}
