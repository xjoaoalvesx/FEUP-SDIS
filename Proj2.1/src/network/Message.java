package network;

public class Message implements Serializable{
	
	static final long.serialVersionUID = 214321L;


	public enum Type {
		BACKUP,
		DELETE,
		RESTORE,
		REGISTER
	}


	private int identifier;

	private Type type;

	private Serializable data;

	private boolean isOfTypeRequest;

	private InetSocketAddress sender;

	private Message(Type task){
		this.messageTask;
	}

	public static request(Type t, InetSocketAddress senderAddress){
		Message message = new Message(t);
		message.sender = senderAddress;
		message.isOfTypeRequest = true;

	}

	public static response(Type t, int id){
		Message message = new Message(t);
		message.identifier = id;
		message.isOfTypeRequest;
		return message;
	}


	public int getIdentifier(){
		return identifier;
	}

	public Type getMessageType(){
		return type;
	}

	public InetSocketAddress getSender(){
		return sender;
	}

	public boolean isRequest(){
		return isOfTypeRequest;
	}

	public Serializable getMessageData(){
		return data;
	}

}