package channels;

import service.Peer;
import messages.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import java.util.Arrays;

public class Channel implements Runnable {

    private static final int MAX_SIZE = 65000;

    private MulticastSocket multicastSocket;
    private InetAddress multicastAddr;
    private int multicastPort;
    private Peer parentPeer;
    private String channelType;

    public Channel(String channelType, Peer parentPeer, String multicastAddr, String multicastPort) {

        this.channelType = channelType;
        this.parentPeer = parentPeer;

        try {
            this.multicastAddr = InetAddress.getByName(multicastAddr);
            this.multicastPort = Integer.parseInt(multicastPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        init();
        System.out.println(channelType + " channel initialized!");
    }

    private void init() {
        try {
            multicastSocket = new MulticastSocket(multicastPort);
            multicastSocket.setTimeToLive(1);
            multicastSocket.joinGroup(multicastAddr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        byte[] rbuf = new byte[MAX_SIZE];
        DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);

        while (true) {

            try { 
                this.multicastSocket.receive(packet);
                int packet_length = packet.getLength();
                byte[] temp = packet.getData();
                byte[] msg_data = Arrays.copyOfRange(temp, 0, packet_length);
                this.parentPeer.executeMessageHandler(new Message(msg_data));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    synchronized public void sendMessage(byte[] message) throws IOException {
        DatagramPacket packet = new DatagramPacket(message, message.length, multicastAddr, multicastPort);
        multicastSocket.send(packet);
    }

    public void close() {
        multicastSocket.close();
    }

}