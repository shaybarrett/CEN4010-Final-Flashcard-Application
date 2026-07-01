import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class FlashcardQuizWindow extends JFrame {
 private List<Flashcard> questions;
 private int currentIndex = 0;
 private int correct = 0;
 private int attempted = 0;

 private JLabel questionLabel;
 private JButton[] answerButtons;
 private JLabel scoreLabel;
 
 private void applyEmojiFont() {
	    Font emojiFont = null;
	    String[] emojiFonts = {"Segoe UI Emoji", "Apple Color Emoji", "Noto Color Emoji"};
	    for (String name : emojiFonts) {
	        Font f = new Font(name, Font.PLAIN, 18);
	        if (!f.getFamily().equals("Dialog")) {
	            emojiFont = f;
	            break;
	        }
	    }
	    if (emojiFont != null) {
	        applyFontToComponents(this.getContentPane(), emojiFont);
	    }
	}

	private void applyFontToComponents(Container container, Font font) {
	    for (Component c : container.getComponents()) {
	        c.setFont(font);
	        if (c instanceof Container) {
	            applyFontToComponents((Container) c, font);
	        }
	    }
	}

 public FlashcardQuizWindow(List<Flashcard> flashcards, Color bgColor, Color fgColor, Color buttonColor) {
     this.questions = new ArrayList<>(flashcards);
     Collections.shuffle(this.questions);

     setTitle("🧠 Flashcard Quiz");
     setSize(600, 400);
     setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
     setLocationRelativeTo(null);
     setLayout(new BorderLayout(10, 10));

     questionLabel = new JLabel("Question", SwingConstants.CENTER);
     questionLabel.setFont(new Font("Arial", Font.BOLD, 24));
     add(questionLabel, BorderLayout.NORTH);

     JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
     answerButtons = new JButton[4];
     for (int i = 0; i < 4; i++) {
         answerButtons[i] = new JButton();
         answerButtons[i].setFont(new Font("Arial", Font.PLAIN, 18));
         answerButtons[i].addActionListener(e -> handleAnswer(((JButton)e.getSource()).getText()));
         buttonPanel.add(answerButtons[i]);
     }
     add(buttonPanel, BorderLayout.CENTER);

     scoreLabel = new JLabel("Score: 0 Correct / 0 Attempted", SwingConstants.CENTER);
     add(scoreLabel, BorderLayout.SOUTH);

     applyTheme(bgColor, fgColor, buttonColor);
     applyEmojiFont();
     loadNextQuestion();

     setVisible(true);
 }

 private void applyTheme(Color bgColor, Color fgColor, Color buttonColor) {
     this.getContentPane().setBackground(bgColor);
     questionLabel.setForeground(fgColor);
     scoreLabel.setForeground(fgColor);
     for (JButton button : answerButtons) {
         button.setBackground(buttonColor);
         button.setForeground(fgColor);
     }
 }

 public static void launchQuizModeChooser(List<Flashcard> selectedFlashcards, Color bgColor, Color fgColor, Color buttonColor) {
     String[] options = {"Classic Quiz", "Modern Quiz"};
     int choice = JOptionPane.showOptionDialog(
         null,
         "Choose your quiz style:",
         "Quiz Mode",
         JOptionPane.DEFAULT_OPTION,
         JOptionPane.INFORMATION_MESSAGE,
         null,
         options,
         options[0]
     );

     if (choice == 0) {
         FlashcardQuiz classicQuiz = new FlashcardQuiz(selectedFlashcards);
         classicQuiz.startQuiz();
     } else if (choice == 1) {
         new FlashcardQuizWindow(selectedFlashcards, bgColor, fgColor, buttonColor);
     }
 }

 private void loadNextQuestion() {
     if (currentIndex >= questions.size()) {
         showResults();
         return;
     }

     Flashcard card = questions.get(currentIndex);
     questionLabel.setText("❓ " + card.getFront());

     List<String> choices = generateChoices(card.getBack());
     for (int i = 0; i < 4; i++) {
         answerButtons[i].setText(choices.get(i));
         answerButtons[i].setBackground(null);
         answerButtons[i].setEnabled(true);
     }
 }

 private List<String> generateChoices(String correctAnswer) {
     Set<String> options = new HashSet<>();
     options.add(correctAnswer);
     Random random = new Random();

     while (options.size() < 4 && !questions.isEmpty()) {
         String wrongAnswer = questions.get(random.nextInt(questions.size())).getBack();
         options.add(wrongAnswer);
     }

     List<String> list = new ArrayList<>(options);
     Collections.shuffle(list);
     return list;
 }

 private void handleAnswer(String selected) {
     Flashcard current = questions.get(currentIndex);
     boolean isCorrect = selected.equals(current.getBack());

     attempted++;
     if (isCorrect) correct++;

     for (JButton btn : answerButtons) {
         btn.setEnabled(false);
         if (btn.getText().equals(current.getBack())) {
             btn.setBackground(Color.GREEN);
         } else if (btn.getText().equals(selected)) {
             btn.setBackground(Color.RED);
         }
     }

     scoreLabel.setText("Score: " + correct + " Correct / " + attempted + " Attempted");

     Timer timer = new Timer(1000, e -> {
         currentIndex++;
         loadNextQuestion();
     });
     timer.setRepeats(false);
     timer.start();
 }

 private void showResults() {
     JOptionPane.showMessageDialog(this, "Quiz Finished!\nCorrect: " + correct + "\nIncorrect: " + (attempted - correct));
     dispose();
 }
}
