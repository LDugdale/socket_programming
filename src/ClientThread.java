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
//            input = new DataInputStream(clientSocket.getInputStream());
            input = new BufferedReader( new InputStreamReader( clientSocket.getInputStream()));
            output = new DataOutputStream(clientSocket.getOutputStream());

            output.writeBytes(protocol.createMessage("welcome","welcome", "welcome") + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
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

            signUp();
        } else if (response[0].equals("sign-in")) {

            signIn();
        } else if (response[0].equals("get-message")) {

            getMessage(Integer.parseInt(response[1]));
        } else if (response[0].equals("send-message")) {

            sendMessage();
        }
    }




    public void signUp(){

        try {

            output.writeBytes(protocol.createMessage("sign-up", "true", "some messages from server") + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void signIn(){

        try {

            output.writeBytes(protocol.createMessage("sign-in", "true", "some messages from server") + "\n");
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

    public void sendMessage(){

        try {

            output.writeBytes(protocol.createMessage("send-message", "true", "some messages from server") + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
