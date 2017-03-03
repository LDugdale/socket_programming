import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private int port;
    private ServerSocket serverSocket;
    private boolean isStopped;
    private ExecutorService threadPool;
    private List<String []> messageList;
    private SimpleProtocol protocol = new SimpleProtocol();

    public Server(int port){

        this.port = port;
        this.serverSocket = null;
        this.isStopped = false;
        this.messageList = new ArrayList<>();
        this.threadPool = Executors.newCachedThreadPool();
    }



    public void start(){

        openServerSocket();

        while(! isStopped()){

            Socket clientSocket = null;
            try {

                clientSocket = this.serverSocket.accept();

            } catch (IOException e) {

                if(isStopped()) {

                    break;
                }

                throw new RuntimeException( "Error accepting client connection", e);
            }

            this.threadPool.execute( new ClientThread(this, clientSocket));
        }
        this.threadPool.shutdown();
        System.out.println("Server Stopped.") ;
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }



    public void signIn(){
//        String true = protocol.createMessage("sign-up", "true", "some messages from server")
//
//
//        String false = protocol.createMessage("sign-up", "false", "the reason of the failure")
    }

    public static void main(String[] args) {
        Server server = new Server(8081);
        server.start();
    }
}