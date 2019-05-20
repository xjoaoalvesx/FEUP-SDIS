package transport;

import java.io.*;


public class SocketSender extends SocketClient{

	private String host;

	public SocketSender(String host, int port){

		super();
		this.host = host;
		this.port = port;
	}
}