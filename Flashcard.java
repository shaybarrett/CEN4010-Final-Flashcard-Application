import java.io.Serializable;

public class Flashcard implements Serializable {
    private static final long serialVersionUID = 1L;
    private String front;
    private String back;

    public Flashcard(String front, String back) {
        this.front = front;
        this.back = back;
    }

    public String getFront() {
        return front;
    }

    public String getBack() {
        return back;
    }

    public void setFront(String newFront) {
        this.front = newFront;
    }

    public void setBack(String newBack) {
        this.back = newBack;
    }

    @Override
    public String toString() {
        return "Front: " + front + " - Back: " + back;
    }
}
