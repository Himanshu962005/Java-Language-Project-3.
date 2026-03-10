import java.util.*;

public class Question {
    String question;
    String[] options;
    int correct;

    public Question(String q, String[] o, int c) {
        question = q;
        // Create Array of Option Indices with Their Original Correct Index.
        Integer[] indices = { 0, 1, 2, 3 };
        List<Integer> indicesList = Arrays.asList(indices);
        // Shuffle the Indices to Randomize Option Positions.
        Collections.shuffle(indicesList);
        // Rearrange Options Based on Shuffled Indices.
        String[] randomizedOptions = new String[4];
        int newCorrect = 0;
        for (int i = 0; i < 4; i++) {
            randomizedOptions[i] = o[indicesList.get(i)];
            if (indicesList.get(i) == c) {
                newCorrect = i;
            }
        }
        this.options = randomizedOptions;
        this.correct = newCorrect;
    }
}