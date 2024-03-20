package helloWorld;

import peersim.edsim.*;

public class Message {

    public final static int HELLOWORLD = 0;

    private int id_dest_finale;
    private int type;
    private String content;

    Message(int id_dest, int type, String content) {
	this.type = type;
	this.content = content;
	this.id_dest_finale = id_dest;
    }

    public String getContent() {
	return this.content;
    }

    public int getType() {
	return this.type;
    }
    
}