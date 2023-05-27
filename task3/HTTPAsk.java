import java.net.*;
import tcpclient3.TCPClient;
import java.io.*;

public class HTTPAsk {
    public static void main(String[] args) {
     
        int portNumber = Integer.parseInt(args[0]); 
        final int STATIC_BUFFER_SIZE = 1000;
        String input = "\r\n\r\n";
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        serverSocket = createServerSocket(portNumber);
          
            while(true){

                byte [] staticByteArray = new byte [STATIC_BUFFER_SIZE];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                
                try{
                    clientSocket = serverSocket.accept();
                   do{
                        int length = clientSocket.getInputStream().read(staticByteArray);
                        outputStream.write(staticByteArray, 0, length);
                    }
                    while(!new String(staticByteArray).contains(input));
                }
                catch(IOException e){
                    System.err.println(e);
                }
                
                String output = outputStream.toString();
                

                if(!validate400(output)){
                    byte [] badRequest400 = "HTTP/1.1 400 Bad Request\r\n\r\n".getBytes(); 
                    try{
                        clientSocket.getOutputStream().write(badRequest400);
                        clientSocket.close();
                    }
                    catch(IOException e){
                        System.err.println(e);
                    }
                    continue;
                }

                if(!validate404(output) || output.contains("favicon")){
                    byte [] notFound404 = "HTTP/1.1 404 Not Found\r\n\r\n".getBytes();
                    try{
                        clientSocket.getOutputStream().write(notFound404);
                        clientSocket.close();
                    } 
                    catch(IOException e){
                        System.err.println(e);
                    }
                    continue;
                }

                String [] outputArray = trimOutput(output);

                String hostname = null;
                Integer port = null;
                boolean shutdown = false;
                Integer limit = null;
                Integer timeout = null;
                byte [] message = new byte[0];

                for (int i = 0; i < outputArray.length; i++) {
                    if(outputArray[i].contains("timeout=")){
                        timeout = Integer.parseInt(outputArray[i].split("=")[1]);
                    }
                    else if (outputArray[i].contains("shutdown=true")){
                        shutdown = true;
                    }
                    else if (outputArray[i].contains("limit=")){
                        limit = Integer.parseInt(outputArray[i].split("=")[1]);
                    }
                    else if (outputArray[i].contains("hostname=")){
                        hostname = outputArray[i].split("=")[1];
                    }
                    else if (outputArray[i].contains("port=")){
                        port = Integer.parseInt(outputArray[i].split("=")[1]);
                    }
                    else if (outputArray[i].contains("string=")){
                        message = outputArray[i].split("=")[1].getBytes();
                    }
                }

                TCPClient tcpClient = new tcpclient3.TCPClient(shutdown, timeout, limit);
                byte [] response = null;

                try{
                    String messageOnWebsite = "HTTP/1.1 200 OK\r\n\r\n" + new String((tcpClient.askServer(hostname, port, message)));
                    response = messageOnWebsite.getBytes();
                }
                catch(UnknownHostException e){
                    response = "HTTP/1.1 404 Not found\r\n\r\n".getBytes();
                }
                catch(IOException e){
                    System.err.println(e);
                }
                try{
                    clientSocket.getOutputStream().write(response);
                    clientSocket.close();
                }
                catch(IOException e){
                    System.err.println(e);
                }
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

    private static boolean validate400 (String output){
        String firstLine = output.split("\r\n")[0];
        boolean containsHTTP = firstLine.substring(firstLine.lastIndexOf(" ") + 1).equals("HTTP/1.1");
        boolean containsGET = firstLine.substring(0, 3).equals("GET");

        if(containsHTTP && containsGET){
            return true;
        }
        else{
            return false;
        }
    }

    private static boolean validate404(String output){
        String firstLine = output.split("\r\n")[0];
        int startIndex = (firstLine.indexOf(" ") + 1);
        int endIndex = startIndex + 5;
        return firstLine.substring(startIndex, endIndex).equals("/ask?");
    }

    private static String [] trimOutput (String output){
        int startIndex = output.indexOf("ask?") + 4;
        int endIndex = output.indexOf("HTTP") - 1;
        return output.substring(startIndex, endIndex).split("&");
    }
}


