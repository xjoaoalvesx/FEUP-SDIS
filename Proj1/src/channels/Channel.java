package channels;

import service.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public abstract class Channel implements Runnable {

    private static final int MAX_MESSAGE_SIZE = 65000;

    private MulticastSocket multicastSocket;
    private InetAddress multicastAddr;
    private int multicastPort;
    private Peer parentPeer;

    public Channel(Peer parentPeer, String multicastAddr, String multicastPort) {
        this.parentPeer = parentPeer;

        try {
            this.multicastAddr = InetAddress.getByName(multicastAddr);
            this.multicastPort = Integer.parseInt(multicastPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        initialize();
    }

    private void init() {

        try {
            multicastSocket = new MulticastSocket(multicastPort);
            multicastSocket.setTimeToLive(1);
            multicastSocket.joinGroup(mcastAddr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        byte[] rbuf = new byte[MAX_MESSAGE_SIZE];
        DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);

        while (true) {

            try { // blocking method
                this.multicastSocket.receive(packet);
                this.parentPeer.addMsgToHandler(packet.getData(), packet.getLength());
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