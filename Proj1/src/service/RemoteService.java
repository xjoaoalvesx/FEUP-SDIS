package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {

	// TODO :
	void backup() throws RemoteException;
	void restore() throws RemoteException;
	void delete() throws RemoteException;
	void reclaim() throws RemoteException;
	void state() throws RemoteException;

}