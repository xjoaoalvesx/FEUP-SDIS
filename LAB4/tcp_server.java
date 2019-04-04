import java.io.*;
import java.net.*;

class TCPServer {
 public static void main(String[] args) throws Exception {

  if (args.length != 1 && args.length != 2) {
    System.out.println("Usage: java Server <port_number> [<timeout>] ");
    return;
  }

  int port = Integer.parseInt(args[0]);



  String clientSentence;
  String capitalizedSentence;
  ServerSocket welcomeSocket = new ServerSocket(port);

  Map<String, String> data = new HashMap<>();

  while (true) {
   System.out.println("Server ready!");
   Socket connectionSocket = welcomeSocket.accept();
   BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
   DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

   clientSentence = inFromClient.readLine();
   
   
   System.out.println("Received: " + clientSentence);
   capitalizedSentence = clientSentence.toUpperCase() + '\n';
   outToClient.writeBytes(capitalizedSentence);
   connectionSocket.close();
  }
 }
}