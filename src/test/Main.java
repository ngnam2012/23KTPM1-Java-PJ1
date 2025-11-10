package test;

import model.Entry;
import core.SlangDictionary;
import io.FileManager;

import java.util.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static SlangDictionary dict;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        FileManager file = new FileManager("data/slang.txt");
        dict = new SlangDictionary(file);

        try {
            dict.load();
            System.out.println("📘 Slang dictionary loaded!");
            System.out.println("Total slangs: " + dict.allSorted().size());
        } catch (IOException e) {
            System.err.println("Critical error loading file: " + e.getMessage());
            System.err.println("Please check 'data/slang.txt' and restart.");
            return;
        }

        boolean running = true;
        while (running) {
            showMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    findSlang();
                    break;
                case "2":
                    findByDefinition();
                    break;
                case "3":
                    addSlang();
                    break;
                case "4":
                    editSlang();
                    break;
                case "5":
                    randomSlang();
                    break;
                case "6":
                    showAllSlangs();
                    break;
                case "7":
                    running = false;
                    break;
                case "8":
                    saveAndExit();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
        System.out.println("Goodbye!");
    }

    private static void showMenu() {
        System.out.println("\n" + String.join("", Collections.nCopies(30, "=")));
        System.out.println("SLANG DICTIONARY MENU");
        System.out.println(String.join("", Collections.nCopies(30, "=")));
        System.out.println("1. Search by slang (key)");
        System.out.println("2. Search by definition");
        System.out.println("3. Add new slang");
        System.out.println("4. Edit slang (overwrite)");
        System.out.println("5. Get random slang");
        System.out.println("6. Show all slangs (first 20)");
        System.out.println("---");
        System.out.println("7. Exit (Don't save)");
        System.out.println("8. Save and Exit");
        System.out.print("Enter your choice: ");
    }

    private static void findSlang() {
        System.out.print("Nhập slang cần tìm: ");
        String slang = scanner.nextLine().trim();

        // Gọi hàm mới bạn vừa tạo ở SlangDictionary
        dict.getSlang(slang).ifPresentOrElse(
                definitionsList -> { // 'definitionsList' là List<String>
                    // Dùng String.join để in cho đẹp
                    System.out.println("✅ Kết quả: " + slang + " = " + String.join("; ", definitionsList));
                },
                () -> System.out.println("❌ Không tìm thấy slang '" + slang + "'")
        );
    }

    private static void findByDefinition() {
        System.out.print("Enter keyword to find in definition: ");
        String query = scanner.nextLine().trim();
        List<Entry> results = dict.searchByDefinition(query);

        if (results.isEmpty()) {
            System.out.println("❌ No slangs found containing '" + query + "'");
        } else {
            System.out.println("✅ Found " + results.size() + " results:");
            for (Entry e : results) {
                System.out.println("  -> " + e);
            }
        }
    }

    private static void addSlang() {
        System.out.print("Enter new slang: ");
        String slang = scanner.nextLine().trim();
        System.out.print("Enter definitions (separated by comma ','): ");
        String defsInput = scanner.nextLine();

        List<String> definitions = Arrays.stream(defsInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (slang.isEmpty() || definitions.isEmpty()) {
            System.out.println("❌ Slang or definitions cannot be empty.");
            return;
        }

        System.out.print("If slang exists, do you want to APPEND definitions (Y/N)? (N = Skip): ");
        boolean append = scanner.nextLine().trim().equalsIgnoreCase("Y");

        boolean success = dict.addOrAppend(slang, definitions, append);
        if (success) {
            System.out.println("✅ Successfully added/updated slang '" + slang + "'.");
        } else {
            System.out.println("❌ Slang '" + slang + "' already exists and you chose not to append.");
        }
    }

    private static void editSlang() {
        System.out.print("Enter slang to edit (will OVERWRITE all definitions): ");
        String slang = scanner.nextLine().trim();
        System.out.print("Enter NEW definitions (separated by comma ','): ");
        String defsInput = scanner.nextLine();

        List<String> definitions = Arrays.stream(defsInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (slang.isEmpty() || definitions.isEmpty()) {
            System.out.println("❌ Slang or definitions cannot be empty.");
            return;
        }

        boolean success = dict.edit(slang, definitions);
        if (success) {
            System.out.println("✅ Successfully edited slang '" + slang + "'.");
        } else {
            System.out.println("❌ Slang '" + slang + "' not found to edit.");
        }
    }

    private static void randomSlang() {
        Entry randomEntry = dict.random();
        System.out.println("🎲 Random slang of the day:");
        System.out.println("  -> " + randomEntry);
    }

    private static void showAllSlangs() {
        List<Entry> all = dict.allSorted();
        if (all.isEmpty()) {
            System.out.println("Dictionary is empty.");
            return;
        }

        System.out.println("--- Showing " + Math.min(20, all.size()) + "/" + all.size() + " slangs (alphabetical order) ---");
        for (int i = 0; i < Math.min(20, all.size()); i++) {
            System.out.println(all.get(i));
        }
    }

    private static void saveAndExit() {
        try {
            dict.save();
            System.out.println("💾 Changes saved to file.");
        } catch (IOException e) {
            System.err.println("Critical error saving file: " + e.getMessage());
        }
    }
}