package ui;

import core.QuizService;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class QuizView {

    public static void showQuizDialog(Parent parent, QuizService.Quiz q) {
        Dialog<Boolean> dlg = new Dialog<>();
        dlg.setTitle("Quiz");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label lbQ = new Label(q.question);
        lbQ.setWrapText(true);

        ToggleGroup group = new ToggleGroup();
        RadioButton[] opts = {new RadioButton(), new RadioButton(), new RadioButton(), new RadioButton()};
        for (int i = 0; i < 4; i++) {
            opts[i].setText(q.answer.get(i));
            opts[i].setToggleGroup(group);
        }

        VBox box = new VBox(10, lbQ, opts[0], opts[1], opts[2], opts[3]);
        dlg.getDialogPane().setContent(box);

        dlg.setResultConverter(bt -> {
            if (bt == ButtonType.OK) {
                int chosen = -1;
                for (int i = 0; i < 4; i++) {
                    if (opts[i].isSelected()) {
                        chosen = i;
                        break;
                    }
                }

                boolean ok = (chosen == q.correctIndex);
                Alert a;

                if (ok) {
                    a = new Alert(Alert.AlertType.INFORMATION);
                    a.setHeaderText(null);
                    a.setContentText("Right");
                } else {
                    a = new Alert(Alert.AlertType.WARNING);
                    a.setHeaderText(null);
                    a.setContentText("Wrong, Answer is: " + q.answer.get(q.correctIndex));
                }

                a.showAndWait();
                return ok;
            }
            return null;
        });

        dlg.initOwner(parent.getScene().getWindow());
        dlg.showAndWait();
    }

    private QuizView() {}
}
