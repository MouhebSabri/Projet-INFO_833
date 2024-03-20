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
/*
  Module d'initialisation de helloWorld: 
  Fonctionnement:
    pour chaque noeud, le module fait le lien entre la couche transport et la couche applicative
    ensuite, il fait envoyer au noeud 0 un message "Hello" a tous les autres noeuds
 */
public class Initializer implements peersim.core.Control {
    Random random = new Random();
    private int tailleRandom = 1000000000; //on définit la plage de valeur que va prendre ID
    private int nextNode=1;//on définit la variable qui définit quelle est le prochain noeuds que l'on peut ajouter au réseau
    private Node currentNode;
    private DHTNode mynode;
    
    private ArrayList<DHTNode> DHT = new ArrayList<>();
	
    private int helloWorldPid;

    public Initializer(String prefix) {
	//recuperation du pid de la couche applicative
//	this.helloWorldPid = Configuration.getPid(prefix + ".helloWorldProtocolPid");
    	this.helloWorldPid = Configuration.getPid(prefix + ".dhtProtocolPid");
    }

    public boolean execute() {
	int nodeNb;
	DHTNode emitter, current;
	Node dest;
	Message helloMsg;

	//recuperation de la taille du reseau
	nodeNb = Network.size();
	//creation du message
	helloMsg = new Message(0, Message.HELLOWORLD,"Hello!!");
	if (nodeNb < 1) {
	    System.err.println("Network size is not strickly positive");
	    System.exit(1);
	}

	//recuperation de la couche applicative de l'emetteur (le noeud 0)
//	emitter = (HelloWorld)Network.get(0).getProtocol(this.helloWorldPid);
//	emitter.setTransportLayer(0);
	
emitter = (DHTNode) Network.get(0).getProtocol(this.helloWorldPid);
emitter.setTransportLayer(0, 0);
	
	//define node in dht
	/*
	ArrayList<HelloWorld> DHT = new ArrayList<HelloWorld>();
	int nb_in_dht =10;
	for(int i =1; i<nb_in_dht; i++) {
		Node no = Network.get(i);
		DHT.add(((HelloWorld)no.getProtocol(this.helloWorldPid)));
	}*/
	
	
	//définit les noeuds en liaisons au début de la simulation
	for(int i =1; i<10; i++) {
		int randNum = random.nextInt(1,tailleRandom);
		nextNode = nextNode + 1;
		currentNode = Network.get(i);
		mynode = (DHTNode)currentNode.getProtocol(this.helloWorldPid);
		mynode.setTransportLayer(i,randNum);
		DHT.add(mynode);
	}
	// on trie les noeuds et on attribue à chacun ses voisins
	DHT.sort((Comparator<? super DHTNode>) new Comparator<DHTNode>() {
	    @Override
	    public int compare(DHTNode node1, DHTNode node2) {
	        return (int)(node1.getID() - node2.getID());
	    }
	});
	
	DHT.get(0).setLeftNeighboor(DHT.get(DHT.size()-1));
	DHT.get(0).setRightNeighboor(DHT.get(0));

	
	DHT.get(DHT.size()-1).setRightNeighboor(DHT.get(0));
	DHT.get(DHT.size()-1).setLeftNeighboor(DHT.get(DHT.size()-2));
	
	for(int i =1; i<DHT.size()-1; i++) {
		DHT.get(i).setRightNeighboor(DHT.get(i-1));
		DHT.get(i).setRightNeighboor(DHT.get(i+1));
	}

	//pour chaque noeud, on fait le lien entre la couche applicative et la couche transport
	//puis on fait envoyer au noeud 0 un message "Hello"
//	for (int i = 1; i < nodeNb; i++) {
//	    dest = Network.get(i);
//	    current = (HelloWorld)dest.getProtocol(this.helloWorldPid);
//	    current.setTransportLayer(i);
//	    emitter.send(helloMsg, dest);
//	}

	System.out.println("Initialization completed");
	return false;
    }
}