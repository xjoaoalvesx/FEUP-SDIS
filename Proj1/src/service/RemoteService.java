package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {

	// TODO :
	void backup() throws RemoteException;
	// restore()
	// delete()
	// reclaim()
	// state()

}