package app;

import core.HistoryService;
import core.QuizService;
import core.SlangDictionary;
import io.FileManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.MainView;

import java.io.File;

public class Main extends Application {

    private static String projectRoot() { return new File("").getAbsolutePath(); }

    @Override
    public void start(Stage stage) {
        String root = projectRoot();
        String dataPath    = root + File.separator + "data"    + File.separator + "slang.txt";
        String historyPath = root + File.separator + "storage" + File.separator + "history.log";

        // Services
        SlangDictionary dict = new SlangDictionary(new FileManager(dataPath));
        HistoryService history = new HistoryService(new File(historyPath));
        QuizService quiz = new QuizService(dict);

        // Load data
        long t0 = System.currentTimeMillis();
        try { dict.load(); } catch (Exception ex) { ex.printStackTrace(); }
        long loadMillis = System.currentTimeMillis() - t0;

        // Build UI
        MainView mainView = new MainView(dict, history, quiz, loadMillis);
        Scene scene = new Scene(mainView.getRoot(), 1100, 680);

        stage.setTitle("Slang Dictionary");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}
