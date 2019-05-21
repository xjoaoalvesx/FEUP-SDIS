package workers;

public abstract class Worker extends Thread{
		
	private boolean runningThread = true;		// Variable that allows the thread to keep running

	private abstract void runListenerSocket();
	private abstract void closeListenerSocket();

	@Override
	public void run(){

		while(runningThread){

			runListenerSocket();
		}

		closeListenerSocket();
	}


	/*
	 * Kills the thread
	 */
	public void killThread(){

		this.runningThread = false;
	}
	
}