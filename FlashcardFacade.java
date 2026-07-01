import java.util.List;
import java.util.Map;

class FlashcardFacade {
    private final FlashcardManager manager = new FlashcardManager();

    public void addFlashcard(String front, String back) {
        manager.addFlashcard(front, back);
    }

    public boolean deleteFlashcard(String front) {
        return manager.deleteFlashcard(front);
    }

    public boolean editFlashcard(String front, String newFront, String newBack) {
        return manager.editFlashcard(front, newFront, newBack);
    }

    public List<Flashcard> getAllFlashcards() {
        return manager.getAllFlashcards();
    }

    public void createDeck(String deckName, List<String> flashcardFronts) {
        manager.createDeck(deckName, flashcardFronts);
    }
    
    public boolean deleteDeck(String deckName) {
        return manager.deleteDeck(deckName);
    }


    public boolean renameDeck(String oldName, String newName) {
        return manager.renameDeck(oldName, newName);
    }

    public void addFlashcardToDeck(String deckName, String front) {
        manager.addFlashcardToDeck(deckName, front);
    }

    public void removeFlashcardFromDeck(String deckName, String front) {
        manager.removeFlashcardFromDeck(deckName, front);
    }

    public Map<String, List<Flashcard>> getDecks() {
        return manager.getDecks();
    }
    
    public void loadFlashcardFromFile(String filename) {
        manager.loadFlashcardsFromFile(filename);
    }
    
    public void loadToDeck(String deckName, String filename) {
        manager.loadFlashcardsToDeck(deckName, filename);
    }


    public void saveToFile(String filename) {
        manager.saveToFile(filename);
    }
    
    public void loadFromFile(String filename) { // loads binary .dat file
        manager.loadFromFile(filename);
    }
}


