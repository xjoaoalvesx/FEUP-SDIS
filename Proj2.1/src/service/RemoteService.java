package service;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {

	void backup(String path, int replicationDegree) throws RemoteException;

	void delete(String path) throws RemoteException;

	void restore(String path) throws RemoteException;
}