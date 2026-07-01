import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlashcardManager {
    private List<Flashcard> flashcards;
    private Map<String, List<Flashcard>> decks;

    public FlashcardManager() {
        this.flashcards = new ArrayList<>();
        this.decks = new HashMap<>();
    }

    public void addFlashcard(String front, String back) {
        flashcards.add(new Flashcard(front, back));
    }

  
    public Flashcard getFlashcard(String front) {
        for (Flashcard card : flashcards) {
            if (card.getFront().equalsIgnoreCase(front)) {
                return card;
            }
        }
        return null;
    }

    public boolean editFlashcard(String front, String newFront, String newBack) {
        for (Flashcard card : flashcards) {
            if (card.getFront().equalsIgnoreCase(front)) {
                if (newFront != null && !newFront.isEmpty()) {
                    card.setFront(newFront);
                }
                if (newBack != null && !newBack.isEmpty()) {
                    card.setBack(newBack);
                }
                return true;
            }
        }
        return false;
    }
    public boolean deleteFlashcard(String front) {
        return flashcards.removeIf(card -> card.getFront().equalsIgnoreCase(front));
    }

    public List<Flashcard> getAllFlashcards() {
        return flashcards;
    }

    public void createDeck(String deckName, List<String> selectedFlashcards) {
        List<Flashcard> deck = new ArrayList<>();
        for (String front : selectedFlashcards) {
            for (Flashcard card : flashcards) {
                if (card.getFront().equalsIgnoreCase(front)) {
                    deck.add(card);
                    break;
                }
            }
        }
        decks.put(deckName, deck);
    }
    
    public boolean deleteDeck(String deckName) {
        String matchedKey = null;

        // Find the key (case insensitive)
        for (String key : decks.keySet()) {
            if (key.equalsIgnoreCase(deckName.trim())) {
                matchedKey = key;
                break;
            }
        }

        if (matchedKey != null) {
            List<Flashcard> deckFlashcards = decks.remove(matchedKey);

            // Remove only those flash cards that are not used in any other decks
            for (Flashcard card : deckFlashcards) {
                boolean isUsedElsewhere = false;
                for (List<Flashcard> otherDeck : decks.values()) {
                    if (otherDeck.contains(card)) {
                        isUsedElsewhere = true;
                        break;
                    }
                }
                if (!isUsedElsewhere) {
                    flashcards.remove(card);
                }
            }
            return true;
        }

        return false;
    }

    public boolean renameDeck(String oldName, String newName) {
        if (decks.containsKey(oldName)) {
            decks.put(newName, decks.remove(oldName));
            return true;
        }
        return false;
    }
    
    public void addFlashcardToDeck(String deckName, String flashcardFront) {
        if (!decks.containsKey(deckName)) {
            return;
        }
        for (Flashcard card : flashcards) {
            if (card.getFront().equalsIgnoreCase(flashcardFront) && !decks.get(deckName).contains(card)) {
                decks.get(deckName).add(card);
                break;
            }
        }
    }

    public void removeFlashcardFromDeck(String deckName, String flashcardFront) {
        if (decks.containsKey(deckName)) {
            decks.get(deckName).removeIf(card -> card.getFront().equalsIgnoreCase(flashcardFront));
        }
    }
    
    public Map<String, List<Flashcard>> getDecks() {
        return decks;
    }
    
    public void loadFlashcardsFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("::");
                if (parts.length == 2) {
                    String front = parts[0].trim();
                    String back = parts[1].trim();
                    addFlashcard(front, back);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load flashcards: " + e.getMessage());
        }
    }
    
    public void loadFlashcardsToDeck(String deckName, String filename) {
        List<Flashcard> deck = decks.getOrDefault(deckName, new ArrayList<>());

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("::");
                if (parts.length == 2) {
                    String front = parts[0].trim();
                    String back = parts[1].trim();
                    Flashcard newCard = new Flashcard(front, back);
                    flashcards.add(newCard);
                    deck.add(newCard);
                }
            }
            decks.put(deckName, deck); // update the deck
        } catch (IOException e) {
            System.err.println("Failed to load flashcards into deck: " + e.getMessage());
        }
    }
    
    public void saveToFile(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(flashcards);
            out.writeObject(decks);
        } catch (IOException e) {
            System.err.println("Failed to save flashcards: " + e.getMessage());
        }
        
    }
        
        @SuppressWarnings("unchecked")
        public void loadFromFile(String filename) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
                flashcards = (List<Flashcard>) in.readObject();
                decks = (Map<String, List<Flashcard>>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("No saved flashcards found or failed to load: " + e.getMessage());
                flashcards = new ArrayList<>();
                decks = new HashMap<>();
            }
    }


    
}