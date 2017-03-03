import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private int port;
    private ServerSocket serverSocket;
    private boolean isStopped;
    private ExecutorService threadPool;
    private List<MessageMeta> messageList;
    private Map<String, UserInfo> userInformation;

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

    public String getMessages(int offset){

        if (offset == -1){
            offset = 0;
        }

        String messageString = "";

        for(int i = offset; i < messageList.size(); i++){

            messageString += i + "," + messageList.get(i).getSender() + "," + messageList.get(i).getTime() + "," + messageList.get(i).getMessage();
        }

        return messageString;
    }

    public void addUser(String username, String password){

        this.userInformation.put(username, new UserInfo(username, password, new ArrayList<>()));
    }

    public int addMessage(String username, int time, String message){

        this.messageList.add( new MessageMeta(username, time , message));
        return messageList.size();
    }

    public boolean detailsCorrect(String username, String password){
        UserInfo user = userInformation.get(username);
        if (user.getPassword().equals(password)){
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        Server server = new Server(8081);
        server.start();
    }
}