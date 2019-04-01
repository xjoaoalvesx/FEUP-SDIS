import java.net.DatagramPacket; 
import java.net.DatagramSocket; 
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.io.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


class Server
{

	public static void main(String[] args) throws IOException 
	{


              if (args.length != 1 && args.length != 2) {
                     System.out.println("Usage: java Server <port_number> [<timeout>] ");
                     return;
              }

              int port = Integer.parseInt(args[0]);
              int timeout = args.length > 1 ? Integer.parseInt(args[1]) : 0;

		DatagramSocket socket = new DatagramSocket(port);
              socket.setSoTimeout(timeout * 1000);

		byte[] receiveData = new byte[1024];
              DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);

              System.out.println("Server has been initialized!");

              Map<String, String> data = new HashMap<>();


		while(true)
		{

                     try{
                            socket.receive(packet);
                     }catch (SocketTimeoutException e) {
                            System.out.println("Timeout");
                            break;
                     }

                     String msg = new String(packet.getData(), 0, packet.getLength());
                     System.out.println("Received: " + msg.trim() + " from " + packet.getAddress() + ":" + packet.getPort());

                     String[] request = msg.split("\\s+");
                     String response = new String();

                     switch(request[0]){

                            case "REGISTER":

                            if(! isValidPlate(request[1]) || database.containsKey(request[1])){
                                   System.out.println("The plate was registered successfully");
                                   response = "-1";
                            }
                            else{
                                   database.put(request[1], request[2])
                                   response = Integer.tostring(database.size());
                            }


                     }
       		


		}
	}


	




}
