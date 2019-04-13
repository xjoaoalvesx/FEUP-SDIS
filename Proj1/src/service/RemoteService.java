package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {

	// TODO :
	void backup(String path, int replicationDegree) throws RemoteException;
	void restore(String path) throws RemoteException;
	void delete(String path) throws RemoteException;
	void reclaim() throws RemoteException;
	void state() throws RemoteException;

}