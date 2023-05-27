package tcpclient3;
import java.net.*;
import java.io.*;

public class TCPClient {

    boolean shutdown;
    Integer timeout;
    Integer maxSize;

    /**
    * Constuctor for TCPClient.
    * 
    * @param shutdown If the shutdown boolean parameter is true, TCPClient will shut
    * down the connection in the outgoing direction (but only in that direction) after
    * having sent the (optional) data to the server. Otherwise, TCPClient will not shut
    * down the connection.
    * @param timeout When askServer has not received any data from the server during
    * a period of time, it closes the connection and returns.
    * @param maxSize When askServer has received a certain amount of bytes from the
    * server, it closes the connection and returns.
    */

    public TCPClient(boolean shutdown, Integer timeout, Integer maxSize){
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.maxSize = maxSize;
    }

    private final int STATIC_BUFFER_SIZE = 1000;
    byte[] staticByteArray = new byte [STATIC_BUFFER_SIZE];
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    int staticBufferLength;

    /**
    * Send bytes to server receive bytes in response.
    * 
    * @param hostname Domain name of the server to which the client should connect.
    * @param port The TCP port number on the server.
    * @param toServerBytes Array with data to send to the server.
    * @return Server response as byte [].
    * @throws IOException 
    */

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        int lengthOfToServerBytes = toServerBytes.length;
        Socket clientSocket = new Socket (hostname, port);
        Integer currentSize = 0;
        
        clientSocket.getOutputStream().write(toServerBytes, 0, lengthOfToServerBytes);
        if(shutdown) clientSocket.shutdownOutput();

        if(this.timeout != null){
            clientSocket.setSoTimeout(this.timeout);
        }
        
        try{
            while((staticBufferLength = clientSocket.getInputStream().read(staticByteArray)) != -1){
                
                if(maxSize != null && (currentSize += staticBufferLength) >= maxSize){
                    outputStream.write(staticByteArray, 0, (staticBufferLength - currentSize + maxSize));
                    break;
                }
                outputStream.write(staticByteArray, 0, staticBufferLength);
            }
            clientSocket.close();
            byte[] dynamicByteArray = outputStream.toByteArray();
            return dynamicByteArray; 
        }

        catch(SocketTimeoutException e){
            System.out.println("Exception: " + e);
            clientSocket.close();
            byte[] dynamicByteArray = outputStream.toByteArray();
            return dynamicByteArray; 
        }
    }    
}
