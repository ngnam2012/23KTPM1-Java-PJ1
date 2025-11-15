package ui;

import core.HistoryService;
import core.QuizService;
import core.SlangDictionary;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import model.Entry;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainView {

    private final SlangDictionary dict;
    private final HistoryService history;
    private final QuizService quiz;
    private final long loadMillis;

    private final BorderPane root = new BorderPane();

    private TableView<Entry> table;
    private TextField tfSlangSearch, tfDefSearch;
    private TextField tfSlangEdit, tfDefsEdit;
    private Label lbStatus, lbLoadedCount, lbRandom;
    private CheckBox cbDark;

    public MainView(SlangDictionary dict, HistoryService history, QuizService quiz, long loadMillis) {
        this.dict = dict;
        this.history = history;
        this.quiz = quiz;
        this.loadMillis = loadMillis;

        VBox top = new VBox(buildMenuBar(), buildToolBar());
        root.setTop(top);
        root.setCenter(buildMainArea());
        root.setBottom(buildStatusBar());

        updateCounters();
        setStatus("Loaded in " + loadMillis + " ms");
    }

    public Pane getRoot() { return root; }

    private MenuBar buildMenuBar() {
        Menu mFile = new Menu("File");
        MenuItem miSave = new MenuItem("Save (data/slang.txt)");
        miSave.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        miSave.setOnAction(e -> {
            try {
                dict.save();
                setStatus("Saved to data/slang.txt");
            }
            catch (Exception ex) {
                showErr("Save failed: " + ex.getMessage());
            }
        });
        MenuItem miExit = new MenuItem("Exit");
        miExit.setOnAction(e -> root.getScene().getWindow().hide());
        mFile.getItems().addAll(miSave, new SeparatorMenuItem(), miExit);

        Menu mView = new Menu("View");
        CheckMenuItem miDark = new CheckMenuItem("Dark mode");
        mView.getItems().add(miDark);

        Menu mHelp = new Menu("Help");
        MenuItem miAbout = new MenuItem("About");
        miAbout.setOnAction(e -> showInfo("Slang Dictionary" +
                "\nSearch / Edit / Random / Quiz / History"));
        mHelp.getItems().add(miAbout);

        MenuBar bar = new MenuBar(mFile, mView, mHelp);

        cbDark = new CheckBox("Dark");
        miDark.selectedProperty().bindBidirectional(cbDark.selectedProperty());
        cbDark.selectedProperty().addListener((o, a, b) -> {
            Scene sc = root.getScene();
            if (sc != null) applyTheme(sc, b);
        });

        return bar;
    }

    private ToolBar buildToolBar() {
        Button btSearchSlang = new Button("Search Slang");
        Button btSearchDef   = new Button("Search Def");
        Button btRandom      = new Button("Random");
        Button btQuiz1       = new Button("Quiz S→D");
        Button btQuiz2       = new Button("Quiz D→S");

        btSearchSlang.setOnAction(e -> doSearchSlang());
        btSearchDef.setOnAction(e -> doSearchDef());
        btRandom.setOnAction(e -> doRandom());
        btQuiz1.setOnAction(e -> doQuiz(true));
        btQuiz2.setOnAction(e -> doQuiz(false));

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        ToolBar tb = new ToolBar(btSearchSlang, btSearchDef, btRandom, new Separator(),
                btQuiz1, btQuiz2, spacer, cbDark);
        tb.getStyleClass().add("app-toolbar");
        return tb;
    }

    private SplitPane buildMainArea() {
        VBox left = new VBox(10);
        left.setPadding(new Insets(12));
        left.getStyleClass().add("panel-left");

        Label l1 = new Label("Search by Slang");
        tfSlangSearch = new TextField(); tfSlangSearch.setPromptText("e.g. YOLO");
        Button bt1 = new Button("Search");
        bt1.setOnAction(e -> doSearchSlang());
        tfSlangSearch.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) doSearchSlang(); });

        Label l2 = new Label("Search by Definition (keywords)");
        tfDefSearch = new TextField(); tfDefSearch.setPromptText("e.g. you only live once");
        Button bt2 = new Button("Search");
        bt2.setOnAction(e -> doSearchDef());
        tfDefSearch.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) doSearchDef(); });

        left.getChildren().add(new VBox(6, l1, tfSlangSearch, bt1, new Separator(), l2, tfDefSearch, bt2));

        table = new TableView<>();
        TableColumn<Entry, String> cSlang = new TableColumn<>("Slang");
        cSlang.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getSlang()));
        cSlang.setPrefWidth(220);

        TableColumn<Entry, String> cDefs = new TableColumn<>("Definitions");
        cDefs.setCellValueFactory(cd -> new SimpleStringProperty(String.join("; ", cd.getValue().getDefinitions())));
        cDefs.setPrefWidth(600);

        table.getColumns().addAll(cSlang, cDefs);
        refreshTable(null);
        VBox center = new VBox(table);
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox right = new VBox(10); right.setPadding(new Insets(12)); right.getStyleClass().add("panel-right");
        Label l3 = new Label("Details / Actions");
        tfSlangEdit = new TextField(); tfSlangEdit.setPromptText("SLANG (key)");
        tfDefsEdit  = new TextField(); tfDefsEdit.setPromptText("Meaning(s) separated by |");

        HBox rowBtns1 = new HBox(8);
        Button btAddAppend = new Button("Add / Append");
        Button btOverwrite = new Button("Overwrite");
        Button btDelete    = new Button("Delete");
        rowBtns1.getChildren().addAll(btAddAppend, btOverwrite, btDelete);

        Button btSave = new Button("Save to file");
        lbRandom = new Label("Random: (press on toolbar)");
        lbRandom.setWrapText(true);

        btAddAppend.setOnAction(e -> {
            var slang = tfSlangEdit.getText().trim();
            var defs  = splitDefs(tfDefsEdit.getText());
            if (slang.isEmpty() || defs.isEmpty()) { showWarn("Please enter both slang and definition(s)"); return; }
            boolean ok = dict.addOrAppend(slang, defs, true);
            if (ok) { refreshTable(null); setStatus("Appended/Added: " + slang); }
            else setStatus("No changes made");
            updateCounters();
        });

        btOverwrite.setOnAction(e -> {
            var slang = tfSlangEdit.getText().trim();
            var defs  = splitDefs(tfDefsEdit.getText());
            if (slang.isEmpty() || defs.isEmpty()) { showWarn("Please enter both slang and definition(s)"); return; }
            boolean ok = dict.edit(slang, defs);
            if (ok) { refreshTable(null); setStatus("Overwrote: " + slang); }
            else setStatus("Slang does not exist");
            updateCounters();
        });

        btDelete.setOnAction(e -> {
            var slang = tfSlangEdit.getText().trim();
            if (slang.isEmpty()) { showWarn("Please enter a slang to delete"); return; }
            boolean ok = dict.delete(slang);
            if (ok) { refreshTable(null); setStatus("Deleted: " + slang); }
            else setStatus("Slang does not exist");
            updateCounters();
        });

        btSave.setOnAction(e -> {
            try { dict.save(); setStatus("Saved to data/slang.txt"); }
            catch (Exception ex) { showErr("Save failed: " + ex.getMessage()); }
        });

        right.getChildren().addAll(l3, tfSlangEdit, tfDefsEdit, rowBtns1, btSave,
                new Separator(), new Label("Random/Quiz"), lbRandom);

        SplitPane sp = new SplitPane(left, center, right);
        sp.setDividerPositions(0.22, 0.78);
        sp.setOrientation(Orientation.HORIZONTAL);
        return sp;
    }

    private HBox buildStatusBar() {
        lbLoadedCount = new Label();
        lbStatus = new Label("Ready");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(16, lbLoadedCount, spacer, lbStatus);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(6, 12, 6, 12));
        bar.getStyleClass().add("status-bar");
        return bar;
    }

    private void doSearchSlang() {
        String key = tfSlangSearch.getText().trim();
        if (key.isEmpty()) { setStatus("Please enter a slang to search"); return; }

        var opt = dict.getSlang(key);
        if (opt.isPresent()) {
            List<String> defs = opt.get();
            Entry e = new Entry(key, defs);
            refreshTable(List.of(e));
            history.logSearch("SLANG", key, String.join("; ", e.getDefinitions()));
            setStatus("1 result for \"" + key + "\"");
        } else {
            refreshTable(List.of());
            history.logSearch("SLANG", key, "NOT FOUND");
            setStatus("Could not find \"" + key + "\"");
        }
    }

    private void doSearchDef() {
        String q = tfDefSearch.getText().trim();
        if (q.isEmpty()) { setStatus("Please enter a definition keyword"); return; }
        var list = dict.searchByDefinition(q);
        refreshTable(list);
        if (list.isEmpty()) {
            history.logSearch("DEF", q, "NOT FOUND");
        } else {
            history.logSearch("DEF", q, list.size() + " hits");
        }
        setStatus(list.size() + " results for \"" + q + "\"");
    }

    private void doRandom() {
        var en = dict.random();
        lbRandom.setText(en.getSlang() + " = " + String.join("; ", en.getDefinitions()));
        setStatus("Random: " + en.getSlang());
    }

    private void doQuiz(boolean slangToDef) {
        if (slangToDef) {
            QuizView.showQuizDialog(root, quiz.makeQuizSlang2Def());
        } else {
            QuizView.showQuizDialog(root, quiz.makeQuizDef2Slang());
        }
    }

    private void refreshTable(List<Entry> itemsOrNull) {
        if (itemsOrNull == null) {
            table.setItems(FXCollections.observableArrayList(dict.allSorted()));
        } else {
            table.setItems(FXCollections.observableArrayList(itemsOrNull));
        }
    }

    private static List<String> splitDefs(String raw) {
        if (raw == null) return List.of();
        return Arrays.stream(raw.split("\\|"))
                .map(String::trim).filter(s -> !s.isEmpty()).distinct().collect(Collectors.toList());
    }

    private void updateCounters() {
        lbLoadedCount.setText("Entries: " + dict.allSorted().size() + " | Load: " + loadMillis + " ms");
    }

    private void setStatus(String s) { lbStatus.setText(s); }

    private void applyTheme(Scene scene, boolean dark) {
        if (dark) {
            scene.getRoot().setStyle("-fx-base:#20232a; -fx-background:#20232a; -fx-control-inner-background:#2b2f36; -fx-text-fill:white;");
        } else {
            scene.getRoot().setStyle("");
        }
    }

    private static void showInfo(String s){ new Alert(Alert.AlertType.INFORMATION, s).showAndWait(); }
    private static void showWarn(String s){ new Alert(Alert.AlertType.WARNING, s).showAndWait(); }
    private static void showErr(String s){ new Alert(Alert.AlertType.ERROR, s).showAndWait(); }
}