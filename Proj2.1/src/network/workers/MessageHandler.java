package workers;

public class MessageHandler extends Thread{
	
	private Server server;
	private ExecutorService executor;


	public MessageHandler(Server server){
		this.server = server;
		this.executor = Executors.newFixedThreadPool(3);

	}

	


}