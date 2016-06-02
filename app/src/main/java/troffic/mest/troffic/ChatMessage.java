package troffic.mest.troffic;

/**
 * Created by Abu-Bakr Siddique on 5/31/2016.
 */
public class ChatMessage extends Object {

    public boolean left;
    public String message;

    public ChatMessage(boolean left, String message) {
        super();
        this.left = left;
        this.message = message;
    }
}
