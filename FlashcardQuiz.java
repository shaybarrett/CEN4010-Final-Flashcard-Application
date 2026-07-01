

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

public class FlashcardQuiz {
    private List<Flashcard> questions;
    private int correct;
    private int incorrect;

    public FlashcardQuiz(List<Flashcard> selectedFlashcards) {
        this.questions = selectedFlashcards;
        this.correct = 0;
        this.incorrect = 0;
    }

    public void startQuiz() {
        Object[] modes = {"Written", "Multiple Choice", "Mixed"};
        String mode = (String) JOptionPane.showInputDialog(
            null,
            "Choose quiz mode:",
            "Quiz Mode Selection",
            JOptionPane.QUESTION_MESSAGE,
            null,
            modes,
            modes[0]
        );

        if (mode == null) return; // cancelled

        double bestScore = 0.0;
        boolean retry;

        do {
            correct = 0;
            incorrect = 0;
            Collections.shuffle(questions); // Shuffle each time

            for (Flashcard card : questions) {
                String chosenMode = mode.equals("Mixed") ? getRandomMode() : mode;

                if (chosenMode.equals("Written")) {
                    handleWritten(card);
                } else {
                    handleMultipleChoice(card);
                }
            }

            int total = correct + incorrect;
            double currentScore = total == 0 ? 0 : (correct * 100.0 / total);
            showResults();

            // Performance comparison
            if (currentScore > bestScore) {
                bestScore = currentScore;
                JOptionPane.showMessageDialog(null, "🎉 New High Score! Great job!");
            } else if (currentScore < bestScore) {
                JOptionPane.showMessageDialog(null, "💡 You can do better — keep trying!");
            } else {
                JOptionPane.showMessageDialog(null, "👍 You matched your best score. Want to go for better?");
            }

            int tryAgain = JOptionPane.showConfirmDialog(
                null,
                "📣 Try again to improve your score?",
                "Retry Quiz",
                JOptionPane.YES_NO_OPTION
            );
            retry = (tryAgain == JOptionPane.YES_OPTION);

        } while (retry);
    }


    
    private String getRandomMode() {
        return Math.random() < 0.5 ? "Written" : "Multiple Choice";
    }

    private void handleWritten(Flashcard card) {
        String userAnswer = JOptionPane.showInputDialog(null, "❓ What is the back of:\n" + card.getFront());
        if (userAnswer == null) {
            JOptionPane.showMessageDialog(null, "Quiz cancelled.");
            System.exit(0);
        }

        // Normalize both answers
        String normalizedUser = normalizeAnswer(userAnswer);
        String normalizedCorrect = normalizeAnswer(card.getBack());

        if (normalizedUser.equalsIgnoreCase(normalizedCorrect)) {
            correct++;
            JOptionPane.showMessageDialog(null, "✅ Correct!");
        } else {
            incorrect++;
            JOptionPane.showMessageDialog(null, "❌ Incorrect!\nCorrect answer: " + card.getBack());
        }
    }
    
    private String normalizeAnswer(String input) {
        return input.replaceAll("\\(.*?\\)", "") 
                    .replaceAll("\\s+", " ")     
                    .trim()
                    .toLowerCase();             
    }

    private List<String> generateChoices(String correctAnswer) {
        Set<String> allBacks = new HashSet<>();
        for (Flashcard f : questions) {
            if (!f.getBack().equalsIgnoreCase(correctAnswer)) {
                allBacks.add(f.getBack());
            }
        }

        List<String> options = new ArrayList<>(allBacks);
        Collections.shuffle(options);

        // Pick 3 random incorrect + add the correct one
        List<String> finalChoices = new ArrayList<>();
        finalChoices.add(correctAnswer);
        for (int i = 0; i < Math.min(3, options.size()); i++) {
            finalChoices.add(options.get(i));
        }

        Collections.shuffle(finalChoices);
        return finalChoices;
    }


    private void handleMultipleChoice(Flashcard card) {
        List<String> choices = generateChoices(card.getBack());
        Object selected = JOptionPane.showInputDialog(
            null,
            "❓ What is the correct definition of:\n" + card.getFront(),
            "Multiple Choice",
            JOptionPane.QUESTION_MESSAGE,
            null,
            choices.toArray(),
            null
        );

        if (selected == null) {
            JOptionPane.showMessageDialog(null, "Quiz cancelled.");
            System.exit(0);  // exit early
        }

        if (selected.toString().equals(card.getBack())) {
            correct++;
            JOptionPane.showMessageDialog(null, "✅ Correct!");
        } else {
            incorrect++;
            JOptionPane.showMessageDialog(null, "❌ Incorrect!\nCorrect answer: " + card.getBack());
        }
    }

    private void showResults() {
        int total = correct + incorrect;
        double grade = total == 0 ? 0 : (correct * 100.0 / total);
        String message = String.format(
            "📊 Quiz Results:\nCorrect: %d\nIncorrect: %d\nGrade: %.2f%%",
            correct, incorrect, grade
        );
        JOptionPane.showMessageDialog(null, message);
    }
}
