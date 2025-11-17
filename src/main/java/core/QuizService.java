package core;

import model.Entry;
import java.util.*;

public class QuizService {
    private final SlangDictionary dict;
    private final Random rnd =  new Random();

    public QuizService(SlangDictionary dict) {
        this.dict = dict;
    }

    public static class Quiz {
        public final String question;
        public final List<String> answer;
        public final int correctIndex;
        public Quiz(String question, List<String> answer, int correctIndex) {
            this.question = question;
            this.answer = answer;
            this.correctIndex = correctIndex;
        }
    }

    public Quiz makeQuizSlang2Def() {
        Entry correct = dict.random();
        String correctAns = correct.getDefinitions().get(0);
        Set<String> options = new LinkedHashSet<>();
        options.add(correctAns);
        while (options.size() < 4) options.add(dict.random().getDefinitions().get(0));
        List<String> opts = new ArrayList<>(options);
        Collections.shuffle(opts, rnd);
        int correctIndex = opts.indexOf(correctAns);
        return new Quiz("What is the definition of: " + correct.getSlang(), opts, correctIndex);
    }

    public Quiz makeQuizDef2Slang() {
        Entry correct = dict.random();
        String def = correct.getDefinitions().get(0);
        Set<String> options = new LinkedHashSet<>();
        options.add(correct.getSlang());
        while (options.size() < 4) options.add(dict.random().getSlang());
        List<String> opts = new ArrayList<>(options);
        Collections.shuffle(opts, rnd);
        int correctIndex = opts.indexOf(correct.getSlang());
        return new Quiz("Which slang means: '" + def + "'?", opts, correctIndex);
    }
}
