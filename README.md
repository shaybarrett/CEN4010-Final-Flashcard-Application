# CEN4010-Final-Flashcard-Application
A software engineering semester-long project that was presented at the University of North Florida Computing Symposium and was done alongside Lena Francisco.

Original README from the Project:
Project Name: Flashcard App
Names: Sharaya Diets & Lena Francisco
Course: CEN4010

#1 External File Dependencies
- No external libraries
- Standard Java SDK libraries were used - java.util and java.io
- Flashcard loading from files uses BufferedReader, FileReader
- GUI dependencies rely only on standard Java Swing
- No external logging frameworks used

Note: If user loads in flashcards from a file, the file must be formatted as
	front::back (one flashcard per line)

#2 How to Run:
- Command Line: 
	Comple all Java files:

	java -d bin src/cen4010/itr1/*.java

	Run the program:

	java -cp bin cen4010.itr1.FlashcardApp

- Eclipse:
	Import the project or create a new Java project and place all files under src/cen4010/itr1/. Right-click FlashcardApp.java -> Run As -> Java Application

#3 Deviations
- The original project planned a simple flashcard flip system
- Actual Changes: 
	+ Written, Multiple Choice, and Mixed Mode for quiz options
	+ Themes and deck management which includes create, rename, delete, manage flashcard, and etc.
	+ Facade layer wasm added for cleaner separation between UI and logic
- Reasons:
	+ Improve user engagement and replayability
	+ Allow better organization for a larger number of flashcards
	+ Future-proof for more features

#4 Design Patterns
- Facade Pattern: FlashcardFacade acts as a unified interface to FlashcardManager, simpliftying interactions from the GUI. It also is used to decouple the complex backend operations from the UI which improves modularity

#5 Test Case Design
 - Example: Valid flashcard inputs vs missing/empty flashcard data for creation/editing
  - Example: Deck operations (existent deck vs nonexistent deck).

- Boundary Value Analysis:
  - Testing behavior when decks are empty, flashcard lists are empty, or loading from empty files

- White-Box Testing:
  - Directly testing internal utility methods like `normalizeAnswer()` and `generateChoices()` via reflection
  - Ensuring internal helper methods behave correctly even outside of UI flow

- Error Guessing:
  - Anticipating user errors like invalid input (nulls, empty strings) and simulating cancellations in the quiz (e.g., user clicks cancel)

Each unit test class (`FlashcardTest`, `FlashcardManagerTest`, `FlashcardFacadeTest`, `FlashcardQuizTest`) was created to focus individually on:
- Object creation and getter/setter integrity
- Business logic correctness
- Data structure management (decks, flashcards)
- Scoring and normalization logic for quizzes
