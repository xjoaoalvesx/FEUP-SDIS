package network; 
 
public class Node { 
   
 
  private String hostIP; 
  private int port; 
 
  public Node(String host, int port){ 
    this.hostIP = host; 
    this.port = port; 
  } 
 
  public String toString(){ 
    return this.hostIP + ":" + this.port; 
  } 
 
  public String getHost(){ 
    return this.hostIP; 
  } 
 
  public int getPort(){ 
    return this.port; 
  } 
 
  public boolean equals(Node node){ 
    return host.equalsIgnoreCase(node.getHost()) && this.port == node.getPort(); 
  } 
 
 
}