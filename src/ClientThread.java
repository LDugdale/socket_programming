import Protocol.SimpleProtocol;
import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

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
     * @param clientSocket
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
     *
     * @param username
     */
    private void setUsername(String username){

        this.username = username;
    }

    /**
     *
     */
    public void run(){

        while (! isStopped) {

            try {

                protocol();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        stopThread();
    }

    /**
     *
     */
    private void protocol() throws IOException {

        String [] response;

        response = getResponse();

        if (response != null) {

            switch (response[0]) {

                case "sign-up":
                    signUp(response[1], response[2]);
                    break;

                case "sign-in":
                    signIn(response[1], response[2]);
                    break;

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
     *
     * @param username
     * @param password
     * @throws IOException
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
     *
     * @param username
     * @param password
     * @throws IOException
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
     *
     * @param offset
     * @throws IOException
     */
    private void getMessage(int offset) throws IOException {

        if(offset == -1){
            offset = 0;
        }

        List<MessageMeta> messages = server.getMessages(offset);

        List<String> messageArray = new ArrayList<>();
        messageArray.add("get-message");

        for(int i = 0; i < messages.size(); i++){

            messageArray.add(messages.get(i).getOffset());
            messageArray.add(messages.get(i).getSender());
            messageArray.add(messages.get(i).getTime());
            messageArray.add(messages.get(i).getMessage());
        }

        output.writeBytes(protocol.createMessage(messageArray.toArray(new String [messageArray.size()])) + "\n");

    }

    /**
     *
     * @param message
     * @throws IOException
     */
    private void sendMessage(String message) throws IOException {

        output.writeBytes(protocol.createMessage("send-message", "true", "" + server.addMessage(this.username, message)) + "\n");
    }

    /**
     *
     * @throws IOException
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
     * Read a line from server and unpack it using SimpleProtocol
     *
     * @return
     */
    public String[] getResponse() throws IOException {

        String line = input.readLine();

            if (line != null) {

                return protocol.decodeMessage(line);
            }

        return null;
    }


}
