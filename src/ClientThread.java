import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

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

            output.writeBytes(protocol.createMessage("welcome","welcome", "welcome") + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username){

        this.username = username;
    }

    public synchronized void run(){


        while (true) {
            try {
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            protocol(getResponse());


        }

    }

    public String[] getResponse(){

        try {

            return protocol.decodeMessage(input.readLine());
        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }

    public void protocol(String [] response) {

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




    public void signUp(String username, String password){

        server.addUser(username, password);
        try {

            output.writeBytes(protocol.createMessage("sign-up", "true", "some messages from server") + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void signIn(String username, String password){

        setUsername(username);
        int test;
        try {
            if (server.detailsCorrect(username, password)) {
                output.writeBytes(protocol.createMessage("sign-in", "true", "some messages from server") + "\n");
            } else {
                output.writeBytes(protocol.createMessage("sign-up", "false", "the reason of the failure") + "\n");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getMessage(int offset){

        try {

            output.writeBytes(protocol.createMessage("get-message", server.getMessages(offset)) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){

        try {

            output.writeBytes(protocol.createMessage("send-message", "true", "" + server.addMessage(username, message)) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
