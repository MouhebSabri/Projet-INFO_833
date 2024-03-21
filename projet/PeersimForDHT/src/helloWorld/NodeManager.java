package helloWorld;

import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;
import java.util.Random;
import java.util.Collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
