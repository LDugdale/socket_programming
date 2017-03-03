/**
 * Created by laurie on 3/3/17.
 */
public class MessageMeta {

    private String time;
    private String username;
    private String message;
    private String offset;

    public MessageMeta(String offset, String username, String time, String message) {
        this.offset = offset;
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

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }
}
