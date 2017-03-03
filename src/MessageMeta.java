/**
 * Created by laurie on 3/3/17.
 */
public class MessageMeta {

    private String time;
    private String username;
    private String message;

    public MessageMeta(String username, String time, String message) {
        this.time = time;
        this.username = username;
        this.message = message;
    }

    public MessageMeta(){
        this.time = null;
        this.username = null;
        this.message = null;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSender() {
        return username;
    }

    public void setSender(String sender) {
        this.username = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
