package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {

    public TCPClient(){
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

            int lengthOftoServerBytes = toServerBytes.length;
            Socket clientSocket = new Socket (hostname, port);
            clientSocket.getOutputStream().write(toServerBytes, 0, lengthOftoServerBytes);
    
            while((staticBufferLength = clientSocket.getInputStream().read(staticByteArray)) != -1)
                outputStream.write(staticByteArray, 0, staticBufferLength);
    
            byte[] dynamicByteArray = outputStream.toByteArray();
            clientSocket.close();
            return dynamicByteArray; 
    }
}
