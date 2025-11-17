package ui;

import core.HistoryService;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.util.List;

public class HistoryView {

    public static void showHistoryDialog(Window owner, HistoryService history) {
        Dialog<Void> dlg = new Dialog<>();
        dlg.initOwner(owner);
        dlg.initModality(Modality.NONE);
        dlg.setTitle("Search History");
        dlg.setHeaderText("Search History");

        ListView<String> listView = new ListView<>();

        List<String> historyItems = history.readAll();

        java.util.Collections.reverse(historyItems);

        listView.setItems(FXCollections.observableArrayList(historyItems));

        listView.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 13;");

        VBox content = new VBox(listView);
        VBox.setVgrow(listView, Priority.ALWAYS);
        content.setPrefSize(750, 400);

        dlg.getDialogPane().setContent(content);
        dlg.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dlg.show();
    }
}