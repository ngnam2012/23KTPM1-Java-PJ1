package ui;


import core.*;
import model.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainView {
    private final HistoryService history;
    private final QuizService quiz;
    private final SlangDictionary dict;
    private final long loadmillis;

    private final BorderPane root = new  BorderPane();

    private TableView<Entry> table;
    private TextField tfSlangSearch, tfDefSearch;
    private TextField tfSlangEdit, tfDefsEdit;
    private Label lbStatus, lbLoadedCount, lbRandom;
    private CheckBox cbDark;

}