

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class FlashcardTest {

    @Test
    void testFlashcardCreation() {
        Flashcard card = new Flashcard("Question", "Answer");
        assertEquals("Question", card.getFront());
        assertEquals("Answer", card.getBack());
    }

    @Test
    void testSetFront() {
        Flashcard card = new Flashcard("Old Front", "Back");
        card.setFront("New Front");
        assertEquals("New Front", card.getFront());
    }

    @Test
    void testSetBack() {
        Flashcard card = new Flashcard("Front", "Old Back");
        card.setBack("New Back");
        assertEquals("New Back", card.getBack());
    }

    @Test
    void testToString() {
        Flashcard card = new Flashcard("Front", "Back");
        assertEquals("Front: Front - Back: Back", card.toString());
    }
}
