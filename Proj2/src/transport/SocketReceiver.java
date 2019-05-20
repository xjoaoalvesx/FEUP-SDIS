package transport;

import java.io.*;


public class SocketReceiver extends SocketClient{

	private String host;

	public SocketReceiver(String host, int port){

		super();
		this.host = host;
		this.port = port;
	}
}