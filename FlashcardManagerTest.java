

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class FlashcardManagerTest {

    @Test
    void testAddFlashcard() {
        FlashcardManager manager = new FlashcardManager();
        manager.addFlashcard("Front1", "Back1");

        Flashcard retrieved = manager.getFlashcard("Front1");
        assertNotNull(retrieved);
        assertEquals("Front1", retrieved.getFront());
        assertEquals("Back1", retrieved.getBack());
    }

    @Test
    void testEditFlashcard() {
        FlashcardManager manager = new FlashcardManager();
        manager.addFlashcard("Front1", "Back1");

        boolean edited = manager.editFlashcard("Front1", "NewFront", "NewBack");
        assertTrue(edited);
        assertNull(manager.getFlashcard("Front1"));
        Flashcard updated = manager.getFlashcard("NewFront");
        assertNotNull(updated);
        assertEquals("NewBack", updated.getBack());
    }

    @Test
    void testDeleteFlashcard() {
        FlashcardManager manager = new FlashcardManager();
        manager.addFlashcard("DeleteMe", "Bye");
        assertTrue(manager.deleteFlashcard("DeleteMe"));
        assertNull(manager.getFlashcard("DeleteMe"));
    }

    @Test
    void testGetAllFlashcards() {
        FlashcardManager manager = new FlashcardManager();
        manager.addFlashcard("Front1", "Back1");
        manager.addFlashcard("Front2", "Back2");
        List<Flashcard> all = manager.getAllFlashcards();
        assertEquals(2, all.size());
    }

    @Test
    void testCreateDeck() {
        FlashcardManager manager = new FlashcardManager();
        manager.addFlashcard("A", "a");
        manager.addFlashcard("B", "b");
        manager.createDeck("TestDeck", List.of("A", "B"));
        Map<String, List<Flashcard>> decks = manager.getDecks();
        assertTrue(decks.containsKey("TestDeck"));
        assertEquals(2, decks.get("TestDeck").size());
    }

    @Test
    void testRenameDeck() {
        FlashcardManager manager = new FlashcardManager();
        manager.addFlashcard("Q", "A");
        manager.createDeck("OldDeck", List.of("Q"));
        boolean renamed = manager.renameDeck("OldDeck", "NewDeck");
        assertTrue(renamed);
        assertTrue(manager.getDecks().containsKey("NewDeck"));
        assertFalse(manager.getDecks().containsKey("OldDeck"));
    }

    @Test
    void testAddFlashcardToDeck() {
        FlashcardManager manager = new FlashcardManager();
        manager.addFlashcard("X", "Y");
        manager.createDeck("Deck1", List.of());
        manager.addFlashcardToDeck("Deck1", "X");
        assertEquals(1, manager.getDecks().get("Deck1").size());
    }

    @Test
    void testRemoveFlashcardFromDeck() {
        FlashcardManager manager = new FlashcardManager();
        manager.addFlashcard("Card1", "Back1");
        manager.createDeck("Deck2", List.of("Card1"));
        manager.removeFlashcardFromDeck("Deck2", "Card1");
        assertEquals(0, manager.getDecks().get("Deck2").size());
    }
}
