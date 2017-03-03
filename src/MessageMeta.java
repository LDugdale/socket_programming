/**
 * This class is for storing the information required for each message.
 *
 * @author Laurie Dugdale
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
