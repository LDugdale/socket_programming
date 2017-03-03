/**
 * Created by laurie on 3/3/17.
 */
public class MessageMeta {

    private int time;
    private String sender;
    private String message;

    public MessageMeta(int time, String sender, String message) {
        this.time = time;
        this.sender = sender;
        this.message = message;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
