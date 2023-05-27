import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConcHTTPAsk {

    public static void main(String[] args) {

        int portNumber = Integer.parseInt(args[0]); 
        ServerSocket serverSocket = createServerSocket(portNumber);
        Socket clientSocket = null;
        
        while(true){
            try {
                clientSocket = serverSocket.accept();     
            } catch (Exception e) {
                System.err.println(e);
            }
    
            MyRunnable myRunnable = new MyRunnable(clientSocket);
            Thread thread = new Thread(myRunnable);
            thread.start();
        }
    }
    
    private static ServerSocket createServerSocket(int portNumber){
        try{
            return new ServerSocket(portNumber);
        }
        catch(IOException e){
            System.err.println(e);
            return null;
        }
    }
}
