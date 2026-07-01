import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class FlashcardApp {
	private Color appBackgroundColor;
	private Color appForegroundColor;
	private Color appButtonColor;
    private FlashcardFacade facade;
    private JFrame frame;
    private JComboBox<String> themeSelector, deckSelector;
    private JLabel cardLabel, counterLabel;
    private JButton flipButton, prevButton, nextButton;
    private boolean showingFront = true;
    private int currentIndex = 0;
    private boolean isDarkTheme = false;
    private List<Flashcard> currentDeckCards = new ArrayList<>();
    
    private void applyEmojiFont() {
        Font emojiFont = null;
        String[] emojiFonts = {"Segoe UI Emoji", "Apple Color Emoji", "Noto Color Emoji"};
        for (String name : emojiFonts) {
            Font f = new Font(name, Font.PLAIN, 16);
            if (!f.getFamily().equals("Dialog")) {
                emojiFont = f;
                break;
            }
        }
        if (emojiFont != null) {
            applyFontToComponents(frame.getContentPane(), emojiFont);
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

    public FlashcardApp() {
        facade = new FlashcardFacade();
        facade.loadFromFile("flashcards.dat");
        frame = new JFrame("🎨 Flashcard App");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(frame, "Save before exit?", "Exit", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    facade.saveToFile("flashcards.dat");
                }
                System.exit(0);
            }
        });
        frame.setLocationRelativeTo(null);

        // Theme and deck selectors
        themeSelector = new JComboBox<>(new String[]{
        	    "Light", "Dark", "Ocean", "Sunset", "Forest", "Retro",
        	    "Lavender Dream", "Cyberpunk", "Autumn Leaves", "Space Night", "Mint Fresh", "Sakura Blossom", "Slate Grey", "Vintage Paper", "➕ Create Custom Theme"
        	});
        themeSelector.setSelectedItem("Ocean");
        themeSelector.addActionListener(e -> selectTheme());

        deckSelector = new JComboBox<>();
        deckSelector.addItem("All Cards");
        deckSelector.addActionListener(e -> selectDeck());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topBar.add(new JLabel("🎯 Select Deck:"));
        topBar.add(deckSelector);
        topBar.add(new JLabel("🎨 Theme:"));
        topBar.add(themeSelector);

        // Flash card display
        cardLabel = new JLabel("No Flashcards Available", SwingConstants.CENTER);
        cardLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        cardLabel.setOpaque(true);
        cardLabel.setPreferredSize(new Dimension(600, 200));

        counterLabel = new JLabel("Card 0 of 0", SwingConstants.CENTER);

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardPanel.add(cardLabel, BorderLayout.CENTER);
        cardPanel.add(counterLabel, BorderLayout.SOUTH);

        // Navigation buttons
        flipButton = createStyledButton("🔄 Flip Card", e -> flipCard());
        prevButton = createStyledButton("⬅️ Prev", e -> previousCard());
        nextButton = createStyledButton("Next ➡️", e -> nextCard());

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        navPanel.add(prevButton);
        navPanel.add(flipButton);
        navPanel.add(nextButton);

        // Control buttons
        JButton createButton = createStyledButton("➕ Create", e -> createFlashcard());
        JButton editButton = createStyledButton("✏️ Edit", e -> editFlashcard());
        JButton deleteButton = createStyledButton("❌ Delete", e -> deleteFlashcard());
        JButton showButton = createStyledButton("📜 Show All", e -> showFlashcards());
        JButton quizSelectButton = createStyledButton("🧠 Quiz Me", e -> selectFlashcardsForQuiz());
        JButton createDeckButton = createStyledButton("📁 New Deck", e -> createDeck());
        JButton renameDeckButton = createStyledButton("📝 Rename Deck", e -> renameDeck());
        JButton manageDeckButton = createStyledButton("🛠 Manage Deck", e -> manageDeck());
        JButton loadToDeckButton = createStyledButton("📂 Load File to Deck", e -> loadFileToDeck());
        JButton deleteDeckButton = createStyledButton("❌ Delete Deck", e -> deleteDeck());

        JPanel buttonPanel = new JPanel(new GridLayout(4, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.add(createButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(showButton);
        buttonPanel.add(quizSelectButton);
        buttonPanel.add(createDeckButton);
        buttonPanel.add(renameDeckButton);
        buttonPanel.add(manageDeckButton);
        buttonPanel.add(loadToDeckButton);
        buttonPanel.add(deleteDeckButton);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(navPanel, BorderLayout.SOUTH);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        refreshDeckList();
        applyEmojiFont();
        
        applyCustomTheme(
        	    new Color(0, 51, 102),     
        	    new Color(255, 255, 255),  
        	    new Color(0, 102, 204),    
        	    new Color(102, 204, 255)   
        	);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.addActionListener(action);
        return button;
    }
    
    private void selectTheme() {
        String selected = (String) themeSelector.getSelectedItem();
        switch (selected) {
            case "Light":
                isDarkTheme = false;
                applyTheme();
                break;
            case "Dark":
                isDarkTheme = true;
                applyTheme();
                break;
            case "Ocean":
                applyCustomTheme(new Color(0, 51, 102), new Color(255, 255, 255), new Color(0, 102, 204), new Color(102, 204, 255));
                break;
            case "Sunset":
                applyCustomTheme(new Color(255, 204, 153), new Color(51, 0, 25), new Color(255, 229, 204), new Color(255, 102, 102));
                break;
            case "Forest":
                applyCustomTheme(new Color(34, 45, 34), new Color(240, 255, 240), new Color(46, 139, 87), new Color(60, 179, 113));
                break;
            case "Retro":
                applyCustomTheme(new Color(0, 0, 0), new Color(0, 255, 0), new Color(10, 10, 10), new Color(0, 100, 0));
                break;
            case "Lavender Dream":
                applyCustomTheme(new Color(230, 230, 250), new Color(75, 0, 130), new Color(255, 255, 255), new Color(200, 162, 200));
                break;
            case "Cyberpunk":
                applyCustomTheme(new Color(0, 0, 0), new Color(255, 0, 255), new Color(30, 30, 30), new Color(0, 255, 0));
                break;
            case "Autumn Leaves":
                applyCustomTheme(new Color(255, 228, 181), new Color(139, 69, 19), new Color(255, 222, 173), new Color(255, 140, 0));
                break;
            case "Space Night":
                applyCustomTheme(new Color(11, 19, 43), Color.WHITE, new Color(25, 25, 112), new Color(75, 0, 130));
                break;
            case "Mint Fresh":
                applyCustomTheme(new Color(170, 240, 209), new Color(0, 80, 60), new Color(200, 250, 230), new Color(100, 200, 180));
                break;
            case "Sakura Blossom":
                applyCustomTheme(new Color(255, 209, 220), new Color(120, 0, 30), new Color(255, 240, 245), new Color(255, 182, 193));
                break;
            case "Slate Grey":
                applyCustomTheme(new Color(112, 128, 144), new Color(230, 230, 230), new Color(169, 169, 169), new Color(70, 130, 180));
                break;
            case "Vintage Paper":
                applyCustomTheme(new Color(245, 222, 179), new Color(101, 67, 33), new Color(250, 235, 215), new Color(107, 142, 35));
                break;
            case "➕ Create Custom Theme":
                openCustomThemeCreator();
                break;
        }
    }
    
    
    private void openCustomThemeCreator() {
        Color bgColor = JColorChooser.showDialog(frame, "Pick Background Color", appBackgroundColor);
        if (bgColor == null) return;

        Color fgColor = JColorChooser.showDialog(frame, "Pick Text Color", appForegroundColor);
        if (fgColor == null) return;

        Color cardColor = JColorChooser.showDialog(frame, "Pick Card Background Color", appBackgroundColor);
        if (cardColor == null) return;

        Color buttonColor = JColorChooser.showDialog(frame, "Pick Button Color", appButtonColor);
        if (buttonColor == null) return;

        applyCustomTheme(bgColor, fgColor, cardColor, buttonColor);
    }
        
        private void applyCustomTheme(Color bg, Color fg, Color cardBg, Color buttonBg) {
            frame.getContentPane().setBackground(bg);
            cardLabel.setBackground(cardBg);
            cardLabel.setForeground(fg);
            counterLabel.setForeground(fg);

            Component[] all = frame.getContentPane().getComponents();
            for (Component comp : all) {
                if (comp instanceof JPanel) {
                    for (Component c : ((JPanel) comp).getComponents()) {
                        if (c instanceof JButton || c instanceof JComboBox) {
                            c.setBackground(buttonBg);
                            c.setForeground(fg);
                        } else if (c instanceof JLabel) {
                            c.setForeground(fg);
                        }
                    }
                    comp.setBackground(bg);
                }
            }
            
            appBackgroundColor = bg;
            appForegroundColor = fg;
            appButtonColor = buttonBg;
        }


    private void applyTheme() {
        Color bg = isDarkTheme ? new Color(45, 45, 45) : new Color(255, 245, 238);
        Color fg = isDarkTheme ? Color.WHITE : Color.BLACK;
        Color cardBg = isDarkTheme ? new Color(90, 90, 90) : new Color(255, 250, 205);
        Color buttonBg = isDarkTheme ? new Color(70, 130, 180) : new Color(173, 216, 230);

        frame.getContentPane().setBackground(bg);
        cardLabel.setBackground(cardBg);
        cardLabel.setForeground(fg);

        Component[] all = frame.getContentPane().getComponents();
        for (Component comp : all) {
            if (comp instanceof JPanel) {
                for (Component c : ((JPanel) comp).getComponents()) {
                    if (c instanceof JButton) {
                        c.setBackground(buttonBg);
                        c.setForeground(fg);
                    } else if (c instanceof JLabel) {
                        c.setForeground(fg);
                    }
                }
                comp.setBackground(bg);
            }
        }
        
        appBackgroundColor = bg;
        appForegroundColor = fg;
        appButtonColor = buttonBg;
    }

  

    private void selectDeck() {
        String selected = (String) deckSelector.getSelectedItem();
        currentIndex = 0;
        if (selected == null || selected.equals("All Cards")) {
            currentDeckCards = facade.getAllFlashcards();
        } else {
            Map<String, List<Flashcard>> decks = facade.getDecks();
            currentDeckCards = decks.getOrDefault(selected, new ArrayList<>());
        }
        updateCardLabel();
    }
    
    private void updateCounter() {
        int total = currentDeckCards.size();
        counterLabel.setText(total > 0 ? "Card " + (currentIndex + 1) + " of " + total : "No Cards");
        
    }

    private void updateCardLabel() {
    	 if (currentDeckCards.isEmpty()) {
    	        cardLabel.setText("No Flashcards Available");
    	        //counterLabel.setText("No Cards");
    	        return;
    	    }

    	    Flashcard card = currentDeckCards.get(currentIndex);
    	    cardLabel.setText("");
    	    Timer timer = new Timer(20, null);
    	    final int[] alpha = {0};
    	    String text = card.getFront();

    	    timer.addActionListener(e -> {
    	        alpha[0] += 15;
    	        if (alpha[0] >= 255) {
    	            cardLabel.setText(text);
    	            ((Timer) e.getSource()).stop();
    	        } else {
    	            cardLabel.setText("<html><div style='color: rgba(0,0,0," + (alpha[0] / 255.0) + ");'>" + text + "</div></html>");
    	        }
    	    });
    	    timer.start();
    	    updateCounter();
    }

    private void refreshDeckList() {
        Set<String> deckNames = facade.getDecks().keySet();
        deckSelector.removeAllItems();
        deckSelector.addItem("All Cards");
        for (String name : deckNames) deckSelector.addItem(name);
        selectDeck();
    }

    private void createFlashcard() {
        String front = JOptionPane.showInputDialog(frame, "Front of card:");
        String back = JOptionPane.showInputDialog(frame, "Back of card:");
        if (front != null && back != null && !front.isEmpty() && !back.isEmpty()) {
            facade.addFlashcard(front, back);
            JOptionPane.showMessageDialog(frame, "✅ Flashcard Created");
            refreshDeckList();
        }
    }

    private void deleteFlashcard() {
        String front = JOptionPane.showInputDialog(frame, "Front of card to delete:");
        if (front != null && facade.deleteFlashcard(front)) {
            JOptionPane.showMessageDialog(frame, "🗑️ Flashcard Deleted");
            refreshDeckList();
        }
    }


    private void editFlashcard() {
        String front = JOptionPane.showInputDialog(frame, "Enter Front of Card to Edit:");
        if (front != null && !front.isEmpty()) {
            String[] options = {"Front", "Back"};
            int choice = JOptionPane.showOptionDialog(frame, "Edit Front or Back?", "Edit Flashcard", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            String newFront = null, newBack = null;
            if (choice == 0) newFront = JOptionPane.showInputDialog(frame, "Enter New Front:");
            else if (choice == 1) newBack = JOptionPane.showInputDialog(frame, "Enter New Back:");
            if (facade.editFlashcard(front, newFront, newBack))
            	JOptionPane.showMessageDialog(frame, "✏️ Flashcard Edited");
            else
                JOptionPane.showMessageDialog(frame, "Flashcard not found");
            refreshDeckList();
        }
    }

    private void showFlashcards() {
        StringBuilder sb = new StringBuilder();
        for (Flashcard card : facade.getAllFlashcards()) {
            sb.append("🃏 Front: ").append(card.getFront()).append(" | Back: ").append(card.getBack()).append("\n");
        }

        if (sb.length() > 0) {
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Arial", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400)); // control how big the scroll window is
            JOptionPane.showMessageDialog(frame, scrollPane, "All Flashcards", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "No flashcards yet!");
        }
    }


    private void flipCard() {
    	if (currentDeckCards.isEmpty()) return;
        Flashcard currentCard = currentDeckCards.get(currentIndex);
        String fromText = showingFront ? currentCard.getFront() : currentCard.getBack();
        String toText = showingFront ? currentCard.getBack() : currentCard.getFront();

        Timer timer = new Timer(15, null);
        final int[] step = {0};
        timer.addActionListener(e -> {
            int scale = 10 - Math.abs(5 - step[0]);
            cardLabel.setFont(cardLabel.getFont().deriveFont((float)(28 - scale)));
            if (step[0] == 5) {
                cardLabel.setText(toText);
            }
            if (++step[0] > 10) {
                timer.stop();
                showingFront = !showingFront;
            }
        });
        timer.start();
    }

    private void nextCard() {
    	 if (!currentDeckCards.isEmpty()) {
    	        currentIndex = (currentIndex + 1) % currentDeckCards.size();
    	        cardLabel.setText(currentDeckCards.get(currentIndex).getFront());
    	        showingFront = true;
    	        updateCounter();
    	    }
    }

    private void previousCard() {
    	 if (!currentDeckCards.isEmpty()) {
    	        currentIndex = (currentIndex - 1 + currentDeckCards.size()) % currentDeckCards.size();
    	        cardLabel.setText(currentDeckCards.get(currentIndex).getFront());
    	        showingFront = true;
    	        updateCounter();
    	    }
    	
    }

    private void createDeck() {
        String deckName = JOptionPane.showInputDialog(frame, "Enter Deck Name:");
        if (deckName != null && !deckName.isEmpty()) {
            List<String> selectedFlashcards = new ArrayList<>();
            for (Flashcard card : facade.getAllFlashcards()) {
                selectedFlashcards.add(card.getFront());
            }
            facade.createDeck(deckName, selectedFlashcards);
            JOptionPane.showMessageDialog(frame, "📁 Deck Created");
            refreshDeckList();
        }
    }

    private void renameDeck() {
        String oldName = JOptionPane.showInputDialog(frame, "Enter Old Deck Name:");
        String newName = JOptionPane.showInputDialog(frame, "Enter New Deck Name:");
        if (facade.renameDeck(oldName, newName)) {
            JOptionPane.showMessageDialog(frame, "📝 Deck Renamed");
            refreshDeckList();
        } else {
            JOptionPane.showMessageDialog(frame, "Deck not found!");
        }
    }

   
    private void manageDeck() {
        String deckName = JOptionPane.showInputDialog(frame, "Enter Deck Name to Manage:");
        if (deckName == null || deckName.isEmpty()) return;
        List<Flashcard> allCards = facade.getAllFlashcards();
        List<Flashcard> currentDeck = new ArrayList<>(facade.getDecks().getOrDefault(deckName, new ArrayList<>()));

        JCheckBox[] checkboxes = new JCheckBox[allCards.size()];
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));

        for (int i = 0; i < allCards.size(); i++) {
            Flashcard card = allCards.get(i);
            checkboxes[i] = new JCheckBox("🃏 " + card.getFront() + " | " + card.getBack(), currentDeck.contains(card));
            panel.add(checkboxes[i]);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        int result = JOptionPane.showConfirmDialog(frame, scrollPane,
            "Manage Deck: " + deckName, JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            List<String> selected = new ArrayList<>();
            for (int i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i].isSelected()) {
                    selected.add(allCards.get(i).getFront());
                }
            }
            facade.createDeck(deckName, selected);
            JOptionPane.showMessageDialog(frame, "Deck updated!");
            refreshDeckList();
        }
    }
    
    // Quiz Portion of the Flash Card Application
    private void startQuiz() {
        if (currentDeckCards.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No cards to quiz on.");
            return;
        }
        FlashcardQuiz quiz = new FlashcardQuiz(currentDeckCards);
        quiz.startQuiz();
    }
    
    private void loadFlashcardsFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            facade.loadFlashcardFromFile(path);
            JOptionPane.showMessageDialog(frame, "📥 Flashcards loaded!");
            refreshDeckList();
        }
    }
    
    private void selectFlashcardsForQuiz() {
        if (currentDeckCards.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No flashcards available to select.");
            return;
        }

        while (true) {
            JCheckBox[] checkboxes = new JCheckBox[currentDeckCards.size()];
            JPanel cardPanel = new JPanel(new GridLayout(0, 1, 5, 5));

            for (int i = 0; i < currentDeckCards.size(); i++) {
                Flashcard card = currentDeckCards.get(i);
                checkboxes[i] = new JCheckBox("🃏 " + card.getFront() + " — " + card.getBack());
                cardPanel.add(checkboxes[i]);
            }

            JCheckBox selectAllCheckbox = new JCheckBox("✅ Select All");
            selectAllCheckbox.addActionListener(e -> {
                boolean checked = selectAllCheckbox.isSelected();
                for (JCheckBox cb : checkboxes) cb.setSelected(checked);
            });

            JScrollPane scrollPane = new JScrollPane(cardPanel);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.add(selectAllCheckbox, BorderLayout.NORTH);
            wrapper.add(scrollPane, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(frame, wrapper,
                "Select Flashcards for Quiz", JOptionPane.OK_CANCEL_OPTION);

            if (result != JOptionPane.OK_OPTION) return;

            List<Flashcard> selectedCards = new ArrayList<>();
            for (int i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i].isSelected()) {
                    selectedCards.add(currentDeckCards.get(i));
                }
            }

            if (!selectedCards.isEmpty()) {
            	FlashcardQuizWindow.launchQuizModeChooser(selectedCards, appBackgroundColor, appForegroundColor, appButtonColor);
                return;
            }

            // Show warning and reopen
            JOptionPane.showMessageDialog(frame, "⚠️ Please select at least one flashcard.");
        }
    }

    private void loadFileToDeck() {
        String deckName = (String) JOptionPane.showInputDialog(frame, "Enter deck name to load into:");
        if (deckName == null || deckName.isEmpty()) return;

        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(frame);
        if (option == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            facade.loadToDeck(deckName, path);
            JOptionPane.showMessageDialog(frame, "📥 Flashcards loaded into deck: " + deckName);
            refreshDeckList();
        }
    }
    
    private void deleteDeck() {
        String deckName = (String) JOptionPane.showInputDialog(
            frame,
            "Enter the name of the deck you want to delete:",
            "Delete Deck",
            JOptionPane.PLAIN_MESSAGE
        );

        if (deckName == null || deckName.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(
            frame,
            "Are you sure you want to delete the deck \"" + deckName + "\"?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (facade.deleteDeck(deckName)) {
                JOptionPane.showMessageDialog(frame, "🗑️ Deck deleted: " + deckName);
                refreshDeckList();
            } else {
                JOptionPane.showMessageDialog(frame, "Deck not found.");
            }
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(FlashcardApp::new);
    }
}