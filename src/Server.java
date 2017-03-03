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

    private int port;
    private ServerSocket serverSocket;
    private boolean isStopped;
    private ExecutorService threadPool;
    private List<MessageMeta> messageList;
    private Map<String, UserInfo> userInformation;

    /**
     *
     * @param port
     */
    public Server(int port){

        this.port = port;
        this.serverSocket = null;
        this.isStopped = false;
        this.messageList = new ArrayList<>();
        this.threadPool = Executors.newCachedThreadPool();
        userInformation = new HashMap<>();
    }

    /**
     *
     */
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

    /**
     *
     * @return
     */
    private synchronized boolean isStopped() {

        return this.isStopped;
    }

    /**
     *
     */
    public synchronized void stop(){

        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    /**
     *
     */
    private void openServerSocket() {

        try {

            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {

            throw new RuntimeException("Cannot open port " + this.port, e);
        }
    }

    /**
     *
     * @param offset
     * @return
     */
    public List<MessageMeta> getMessages(int offset) {


        return messageList.subList(offset, messageList.size());
    }

    /**
     *
     * @param username
     * @param password
     */
    public boolean addUser(String username, String password){

        int uLength = username.length();
        int pLength = password.length();

        if (!userInformation.containsKey(username) && pLength >= 8 && 32 >= pLength && uLength >= 5 && 20 >= uLength) {

            this.userInformation.put(username, new UserInfo(username, password, new ArrayList<>()));
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

        this.userInformation.get(username).getMessages().add(new MessageMeta(offset, username, time , message));
        this.messageList.add( new MessageMeta(offset, username, time , message));

        return messageList.size() - 1;
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
    public boolean detailsCorrect(String username, String password){

        UserInfo user = userInformation.get(username);

        if (user.getPassword().equals(password)){
            return true;
        }

        return false;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Server server = new Server(8081);
        server.start();
    }
}