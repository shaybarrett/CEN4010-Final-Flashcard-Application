

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class FlashcardQuizTest {

	@Test
    void testNormalizeAnswer() {
        List<Flashcard> cards = List.of(new Flashcard("A", "Answer (extra)"));
        FlashcardQuiz quiz = new FlashcardQuiz(cards);

        // Accessing normalizeAnswer via reflection
        try {
            var method = FlashcardQuiz.class.getDeclaredMethod("normalizeAnswer", String.class);
            method.setAccessible(true);

            String result = (String) method.invoke(quiz, " Answer (details) ");
            assertEquals("answer", result);
        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }
    }

    @Test
    void testGenerateChoices() {
        List<Flashcard> cards = Arrays.asList(
            new Flashcard("Q1", "A1"),
            new Flashcard("Q2", "A2"),
            new Flashcard("Q3", "A3"),
            new Flashcard("Q4", "A4")
        );
        FlashcardQuiz quiz = new FlashcardQuiz(cards);

        try {
            var method = FlashcardQuiz.class.getDeclaredMethod("generateChoices", String.class);
            method.setAccessible(true);

            List<String> choices = (List<String>) method.invoke(quiz, "A1");
            assertTrue(choices.contains("A1"));
            assertEquals(4, choices.size());
        } catch (Exception e) {
            fail("Reflection error: " + e.getMessage());
        }
    }

}
