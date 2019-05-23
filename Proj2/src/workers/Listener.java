package workers;

import network.PeerNode;


public abstract class Listener extends Worker{
		
	private PeerNode pee_node;
	private SSLServerSocket ssl_socket;
	private ExecutorService executor_service;

	public Listener(PeerNode peer_node){

		this.peer_node = peer_node;

		try{

			SSLServerSocketFactory ssl_socket_factory = SSLServerSocketFactory.getDefault();

			int port = this.peer_node.getAddress().getPort();
			this.ssl_socket = (SSLServerSocket) ssl_socket_factory.createServerSocket(port);
		}
		catch(IOException ioException){

			ioException.printStackTrace();
		}

		this.executor_service = Executors.newFixedThreadPool(5);


		// sslSocket.setNeedClientAuth(true); 
	}


	/*
	 * Keeps trying to accept connections and then handles the messages received
	 */
	@Override
	private void runListenerSocket(){

		SSLSocket new_ssl_socket;

		try{

			new_ssl_socket = (SSLSocket) ssl_socket.accept();
		}
		catch(IOException ioException){

			ioException.printStackTrace();
		}

		handleSocket(new_ssl_socket);
	}


	/*
	 * Closes the listener's socket
	 */
	@Override
	public void closeListenerSocket(){

		try{

			this.ssl_socket.close();
		}
		catch(IOException ioException){

			ioException.printStackTrace();
		}
	}

	/*
	 * Handles the socket messages (idk)
	 */
	public void handleSocket(SSLSocket socket){

		System.out.println("handleSocket");
	}
}