import java.net.DatagramPacket; 
import java.net.DatagramSocket; 
import java.net.InetAddress;
import java.io.*;

class Client
{

	public static void main(String[] args) throws IOException 
	{

        if(args.length < 4) {
            System.out.println("Usage: java Client <hostname> <port_number> <oper> <opnd>*");
            return;
        }

        InetAddress address = InetAddress.getByName(args[0]);
		int port = Integer.parseInt(args[1]);
		String request = args[2];
        String plate = args[3];

        if(!plate.matches("\\w{2}-\\w{2}-\\w{2}")){
            System.out.println("The plate format must be XX-XX-XX");
        }

        String msg = new String();

        switch(request){
            case "REGISTER":
                if(args[4].length() > 256){
                    System.out.println("Owner's name must have less than 256 characters.");
                }
                msg = "REGISTER" + args[3] + " " + args[4];
                break;
            case "LOOKUP":
                msg = "LOOKUP" + args[3];
                break;
        }


        DatagramSocket socket = sendMessage(msg, addres, port);
        String response = receiveMessage(socket, addres, port);

        System.out.println("Echoed Message:" + response);
        socket.close();

        System.out.println("Client terminated!");

      	byte[] sendData = new byte[256];
    	byte[] receiveData = new byte[256];


    	
    	
    	sendData = msg.getBytes();
    	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
    	clientSocket.send(sendPacket);
    	DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    	clientSocket.receive(receivePacket);
    	String modifiedSentence = new String(receivePacket.getData());
    	System.out.println("FROM SERVER:" + modifiedSentence);
    	clientSocket.close();

	}


    private static DatagramSocket sendMessage(String message, InetAddress address, int port) throws IOException{
        System.out.println(msg);
        DatagramSocket socket = new DatagramSocket();
        byte[] sbuf = msg.getBytes();

        DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, port);
        socket.send(packet);

        return socket;
    }

    private static String receiveMessage()

        byte[] rbuf = new byte[256];
        DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
        socket.receive(packet);

        String received = new String(packet.getData());

        return received;
	

}
