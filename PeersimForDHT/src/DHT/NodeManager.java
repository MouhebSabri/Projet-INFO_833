package DHT;

import peersim.config.*;
import java.util.Random;

import java.util.ArrayList;

public class NodeManager implements peersim.core.Control{
	ArrayList<Integer> list_id;
	private int tailleRandom = 1000000000;
	
	private int dhtPid;
	
	public NodeManager(String prefix, ArrayList<Integer> list_id){
		dhtPid = Configuration.getPid(prefix + ".dhtProtocolPid");
		this.list_id = list_id;
		
	}

	public boolean execute() {
		Random random = new Random();
		int randNum = random.nextInt(1,100);
		if (randNum<10) {}
		
		return false;
	}
}
