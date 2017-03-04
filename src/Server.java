import ChatClient.ChatClientApp;

import java.awt.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Laurie Dugdale
 */
public class Server {

    private int port; // Represents the port to be used
    private String host; // Represents the host
    private ServerSocket serverSocket; // The server socket
    private ExecutorService threadPool; // The thread pool
    private List<MessageMeta> messageList; // The message List
    private Map<String, String> userInformation; // The username and password of each user

    /**
     * Server constructor
     *
     */
    public Server(){

        this.port = 0;
        this.host = "";
        this.serverSocket = null;
        this.messageList = new ArrayList<>();
        this.threadPool = Executors.newCachedThreadPool();
        userInformation = new HashMap<>();
    }

    public void setHost(String host){
        this.host = host;
    }

    public void setPort(int port){
        this.port = port;
    }

    /**
     * Start - Method to start the server in an endless while loop no method of stopping the server seems to be
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


    /*
     * Server client methods
     */
    /**
     * Returns a List of MessageMeta used by the ClientThread. The amount of messages returned is defined by the
     * offset.
     *
     * @param offset Defines which messages to be returned from the messageList
     * @return List containing MessageMeta
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
     * @param username Represents a users username to be added.
     * @param password Represents a users password to be added.
     * @return True if adding the user was successful.
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
     * Adds a message to the messageList List.
     *
     * @param username Represents the username the message belongs to.
     * @param message The message that has been sent that needs to be stored
     * @return The offset of the message that has been added.
     */
    public int addMessage(String username, String message){

        String offset = "" + (messageList.size());
        String time = new SimpleDateFormat("HH:mm").format(new java.util.Date());

        if(this.messageList.add( new MessageMeta(offset, username, time , message))){

            // offset of the message
            return messageList.size() - 1;
        } else {
            // the message could not be added
            return -666;
        }


    }

    /**
     * Used for checking a login username and password against a stored username and password in the userInformation map
     * if details are correct return true, else return false.
     *
     * @param username Represents a users username to be checked.
     * @param password Represents a users password to be checked.
     * @return True if details are correct
     */
    public boolean detailsCorrect(String username, String password){

        if (userInformation.containsKey(username) && userInformation.containsValue(password)){
            return true;
        }

        return false;
    }

    /*
     * Main method
     */
    /**
     * Main method used for starting the server.
     *
     * @param args
     */
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Server server = new Server();
                    server.setHost(args[0]);
                    server.setPort(Integer.parseInt(args[1]));
                    server.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}