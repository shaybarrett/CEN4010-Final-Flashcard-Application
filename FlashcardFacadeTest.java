

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class FlashcardFacadeTest {

	@Test
	void testFacadeDelegation() {
		FlashcardFacade facade = new FlashcardFacade();
        facade.addFlashcard("Front", "Back");

        List<Flashcard> flashcards = facade.getAllFlashcards();
        assertEquals(1, flashcards.size());

        facade.createDeck("Deck1", List.of("Front"));
        assertTrue(facade.getDecks().containsKey("Deck1"));

        facade.renameDeck("Deck1", "Deck2");
        assertTrue(facade.getDecks().containsKey("Deck2"));

        facade.deleteDeck("Deck2");
        assertFalse(facade.getDecks().containsKey("Deck2"));
	}

}
