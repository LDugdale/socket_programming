import java.util.List;

/**
 * @author Laurie Dugdale
 */
public class UserInfo {

    private String username;
    private String password;
    private List<MessageMeta> Messages;

    public UserInfo(String username, String password, List<MessageMeta> messages) {
        this.username = username;
        this.password = password;
        Messages = messages;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<MessageMeta> getMessages() {
        return Messages;
    }

    public void setMessages(List<MessageMeta> messages) {
        Messages = messages;
    }
}
