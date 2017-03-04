import Protocol.SimpleProtocol;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Laurie Dugdale
 */
public class ClientThread extends Thread {

    private Server server; // and instance of the server is passed in to pass messages between the data structures.
    private DataOutputStream output; // output stream to server
    private BufferedReader input; // input stream from the client
    private SimpleProtocol protocol; // The protocol contains methods used for communicating with client
    private Socket clientSocket; // the client socket
    private String username; // stores the Username of client the Thread is talking to.
    private boolean isStopped; // when false the loop in the run method stops used for shutting the thread down.


    /**
     * Constructor setup input output stream and assign socket to field variable.
     *
     * @param clientSocket the client socket
     * @param server an instance of the server is passed in to pass messages between the data structures.
     */
    public ClientThread(Server server, Socket clientSocket) {

        this.protocol = new SimpleProtocol();
        this.server = server;
        this.clientSocket = clientSocket;

        try {

            // Setup the input and output streams for communicating with the client.
            input = new BufferedReader( new InputStreamReader( clientSocket.getInputStream()));
            output = new DataOutputStream(clientSocket.getOutputStream());

            // Send initial welcome message
            output.writeBytes(protocol.createMessage("welcome","Welcome to the most amazing chat app in the world!") + "\n");

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Setter for the set username variable
     *
     * @param username Represents the username of the client the thread is talking too.
     */
    private void setUsername(String username){

        this.username = username;
    }

    /**
     * The main run method for the ClientThread checks for initial sign in and sign up messages and then enters loop
     * to continuously check for get-message and send-message messages.
     */
    public void run(){

        try {

            // respond to initial sign-in and sign-up messages
            initialProtocol();

            // loop while isStopped boolean is false
            while (! isStopped) {

                // respond to get-message and send-message while looping
                loopProtocol();
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Parses the first messages sent from client
     */
    private void initialProtocol() throws IOException {

        String [] response = getMessage();

        if (response != null) {

            switch (response[0]) {

                case "sign-up":
                    signUp(response[1], response[2]);
                    break;

                case "sign-in":
                    signIn(response[1], response[2]);
                    break;

                default:
                    throw new IllegalArgumentException("An unrecognised protocol has been received!");
            }
        }
    }

    /**
     * Parses all messages sent after the user is signed in
     */
    private void loopProtocol() throws IOException {

        String [] response = getMessage();

        if (response != null) {

            switch (response[0]) {

                case "get-message":
                    getMessage(Integer.parseInt(response[1]));
                    break;

                case "send-message":
                    sendMessage(response[1]);
                    break;

                default:
                    throw new IllegalArgumentException("An unrecognised protocol has been received!");
            }
        }
    }

    /**
     * Handles sign up requests
     *
     * @param username Represents a users username to be added.
     * @param password Represents a users password to be added.
     * @throws IOException Passing IOException to be handled in one place in the run method
     */
    private void signUp(String username, String password) throws IOException {

        boolean success = server.addUser(username, password);
        if (success) {

            output.writeBytes(protocol.createMessage("sign-up", "true", "Congratulations! You have successfully signed up!") + "\n");
        } else {


            output.writeBytes(protocol.createMessage("sign-up", "false", "Oops! your details are wrong who knows why?") + "\n");
        }

        stopThread();
    }

    /**
     * Handles sign in requests
     *
     * @param username Represents a users username to be checked and signed in to the server.
     * @param password Represents a users password to be checked and signed in to the server.
     * @throws IOException Passing IOException to be handled in one place in the run method
     */
    private void signIn(String username, String password) throws IOException {

        setUsername(username);

        if (server.detailsCorrect(username, password)) {

            output.writeBytes(protocol.createMessage("sign-in", "true", "Credentials accepted") + "\n");
        } else {

            output.writeBytes(protocol.createMessage("sign-in", "false", "Credentials rejected") + "\n");
            stopThread();
        }
    }

    /**
     * Handles a get message requests. Adds all the messages specfied by the offset to an array and sends them to the client
     * using the createMessage method.
     *
     * @param offset Defines which messages to be returned from the messageList
     * @throws IOException Passing IOException to be handled in one place in the run method
     */
    private void getMessage(int offset) throws IOException {

        if(offset == -1){
            offset = 0;
        }

        List<MessageMeta> messages = server.getMessages(offset);
        List<String> messageArray = new ArrayList<>();
        messageArray.add("get-message");

        // loop through the messages and add all the elements of the MessageMeta object to the messageArray in the correct order
        for (MessageMeta m : messages) {

            messageArray.add(m.getOffset());
            messageArray.add(m.getSender());
            messageArray.add(m.getTime());
            messageArray.add(m.getMessage());
        }

        // convert the message array to a string and send to the client
        output.writeBytes(protocol.createMessage(messageArray.toArray(new String [messageArray.size()])) + "\n");

    }

    /**
     * Send message sends the message offset to the client if message cannot be added to list reason for failure is
     * sent instead.
     *
     * @param message The message that has been sent that needs to be stored
     * @throws IOException Passing IOException to be handled in one place in the run method
     */
    private void sendMessage(String message) throws IOException {

        int offset = server.addMessage(this.username, message);

        if(offset == -666) {

            output.writeBytes(protocol.createMessage("send-message", "false", "message could not be added") + "\n");
        } else {

            output.writeBytes(protocol.createMessage("send-message", "true", "" + offset) + "\n");
        }
    }

    /**
     * Responsible to bringing the thread cleanly to a stop.
     *
     */
    private void stopThread() {

        this.isStopped = true;
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.interrupt();
    }

    /**
     * Read a line from the client and unpack it using SimpleProtocol
     *
     * @return Returns a String array containing the message from the client.
     * @throws IOException Passing IOException to be handled in one place in the run method
     */
    public String[] getMessage() throws IOException {

        String line = input.readLine();

            if (line != null) {

                return protocol.decodeMessage(line);
            }

        return null;
    }


}
