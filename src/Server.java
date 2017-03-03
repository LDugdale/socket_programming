import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private int port; // Represents the port to be used
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private List<MessageMeta> messageList;
    private Map<String, String> userInformation;

    /**
     * Server constructor
     * @param port
     */
    public Server(int port){

        this.port = port;
        this.serverSocket = null;
        this.messageList = new ArrayList<>();
        this.threadPool = Executors.newCachedThreadPool();
        userInformation = new HashMap<>();
    }

    /**
     * Method to start the server in an endless while loop no method of stopping the server seems to be
     * required in the brief
     *
     */
    public void start(){

        openServerSocket();

        while(true){

            Socket clientSocket = null;
            try {

                clientSocket = this.serverSocket.accept();
                this.threadPool.execute( new ClientThread(this, clientSocket));

            } catch (IOException e) {

                System.err.println(e.getMessage());
            }

        }
    }

    /**
     * Opens the server socket and handles any exceptions that may arise
     */
    private void openServerSocket() {

        try {

            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {

            throw new RuntimeException("Cannot open port " + this.port, e);
        }
    }

    /**
     * Returns a List of MessageMeta used by the ClientThread. The ammount of messages returned is defined by the
     * offset.
     *
     * @param offset
     * @return
     */
    public List<MessageMeta> getMessages(int offset) {


        return messageList.subList(offset, messageList.size());
    }

    /**
     * Adds a user to the userInformation Map and returns true or false depending on whether the conditions are met
     * for a user to be added.
     * The username must not already exist, the password must be between 8 and 32 characters and the username must
     * be between 5 and 20 characters.
     *
     * @param username
     * @param password
     * @return
     */
    public boolean addUser(String username, String password){

        int uLength = username.length();
        int pLength = password.length();

        if (!userInformation.containsKey(username) && pLength >= 8 && 32 >= pLength && uLength >= 5 && 20 >= uLength) {

            this.userInformation.put(username, password);
            return true;
        }

        return false;
    }

    /**
     *
     * @param username
     * @param message
     * @return
     */
    public int addMessage(String username, String message){

        String offset = "" + (messageList.size());
        String time = new SimpleDateFormat("HH:mm").format(new java.util.Date());

        this.messageList.add( new MessageMeta(offset, username, time , message));

        return messageList.size() - 1;
    }

    /**
     * Used for checking a login username and password against a stored username and password in the userInformation map
     * if details are correct return true, else return false.
     *
     * @param username Represents a users username to be checked.
     * @param password Represents a users password to be checked.
     * @return
     */
    public boolean detailsCorrect(String username, String password){

        if (userInformation.containsKey(username) && userInformation.containsValue(password)){
            return true;
        }

        return false;
    }

    /**
     * Main method used for starting the server.
     *
     * @param args
     */
    public static void main(String[] args) {

        Server server = new Server(8081);

        server.start();
    }
}