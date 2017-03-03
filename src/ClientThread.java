import Protocol.SimpleProtocol;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mnt_x on 28/02/2017.
 */
public class ClientThread extends Thread {

    private Server server;
    private DataOutputStream output;	// output stream to server
    private BufferedReader input;	// input stream from the client
    private SimpleProtocol protocol;
    private Socket clientSocket;
    private String username;
    private boolean isStopped;


    /**
     * Constructor setup input output stream and assign socket to field variable.
     *
     * @param clientSocket
     * @param server
     */
    public ClientThread(Server server, Socket clientSocket) {
        this.protocol = new SimpleProtocol();
        this.server = server;
        this.clientSocket=clientSocket;
        try {
            input = new BufferedReader( new InputStreamReader( clientSocket.getInputStream()));
            output = new DataOutputStream(clientSocket.getOutputStream());

            output.writeBytes(protocol.createMessage("welcome","Welcome to the most amazing chat app in the world!") + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param username
     */
    public void setUsername(String username){

        this.username = username;
    }

    /**
     *
     */
    public void run(){


        while (! isStopped) {

            protocol();
        }

    }

    /**
     *
     */
    public void protocol() {

        String [] response = null;
        try {

            response = protocol.decodeMessage(input.readLine());
            if (response != null) {

                if (response[0].equals("sign-up")) {

                    signUp(response[1], response[2]);
                } else if (response[0].equals("sign-in")) {

                    signIn(response[1], response[2]);
                } else if (response[0].equals("get-message")) {

                    getMessage(Integer.parseInt(response[1]));
                } else if (response[0].equals("send-message")) {

                    sendMessage(response[1]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param username
     * @param password
     * @throws IOException
     */
    public void signUp(String username, String password) throws IOException {

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
    public void signIn(String username, String password) throws IOException {

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
    public void getMessage(int offset) throws IOException {

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
    public void sendMessage(String message) throws IOException {

        output.writeBytes(protocol.createMessage("send-message", "true", "" + server.addMessage(username, message)) + "\n");
    }

    public void stopThread() throws IOException {

        this.isStopped = true;
        clientSocket.close();
        this.interrupt();
    }


}
