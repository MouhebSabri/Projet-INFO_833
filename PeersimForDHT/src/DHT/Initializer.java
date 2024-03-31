package DHT;

import peersim.core.*;
import peersim.config.*;
import java.util.Random;

import java.util.ArrayList;
import java.util.Comparator;

/*
  Module d'initialisation de helloWorld: 
  Fonctionnement:
    pour chaque noeud, le module fait le lien entre la couche transport et la couche applicative
    ensuite, il fait envoyer au noeud 0 un message "Hello" a tous les autres noeuds
 */
public class Initializer implements peersim.core.Control {
    Random random = new Random();
    private int tailleRandom = 100000;//1 000 000 000; //on définit la plage de valeur que va prendre ID
    private int nextNode=1;//on définit la variable qui définit quelle est le prochain noeuds que l'on peut ajouter au réseau
    private Node currentNode;
    private DHTNode mynode;
    
    private ArrayList<DHTNode> DHT = new ArrayList<>();
    public ArrayList<Integer> list_id = new ArrayList<>();
	
    private int DHTPid;

    public Initializer(String prefix) {
	//recuperation du pid de la couche applicative
    	this.DHTPid = Configuration.getPid(prefix + ".dhtProtocolPid");
    }

    public void add_a_node() {
    	int randNum = random.nextInt(1,this.tailleRandom);
    	int node_inter = random.nextInt(1,DHT.size());
    	System.out.println("noeuds à ajouter " +randNum);
    	currentNode = Network.get(this.nextNode);
		mynode = (DHTNode)currentNode.getProtocol(this.DHTPid);
		mynode.setTransportLayer(this.nextNode,randNum);
		DHTNode emitter=(DHTNode) Network.get(0).getProtocol(this.DHTPid);
		emitter.send(new Message(mynode), Network.get(node_inter));
		//DHT.get(node_inter).join(mynode);
		this.nextNode ++;

		DHT.add(mynode);
		list_id.add(randNum);  
		DHT.sort((Comparator<? super DHTNode>) new Comparator<DHTNode>() {
		    @Override
		    public int compare(DHTNode node1, DHTNode node2) {
		        return (int)(node1.getID() - node2.getID()); }});
    }
    public boolean execute() {
	int nodeNb;
	DHTNode emitter, current;
	Node dest;
	Message helloMsg;

	//recuperation de la taille du reseau
	nodeNb = Network.size();
	//creation du message
	helloMsg = new Message(0, "Hello!!");
	if (nodeNb < 1) {
	    System.err.println("Network size is not strickly positive");
	    System.exit(1);
	}


emitter = (DHTNode) Network.get(0).getProtocol(this.DHTPid);
emitter.setTransportLayer(0, 0);
	

	
	
	//définit les noeuds en liaisons au début de la simulation
	for(int i =1; i<4; i++) {
		int randNum = random.nextInt(1,tailleRandom);
		nextNode = nextNode + 1;
currentNode = Network.get(i);
		mynode = (DHTNode)currentNode.getProtocol(this.DHTPid);
		mynode.setTransportLayer(i,randNum);
		DHT.add(mynode);
		list_id.add(randNum);
		System.out.println("on initialise le noeud "+ i);
	}
	// on trie les noeuds et on attribue à chacun ses voisins
	DHT.sort((Comparator<? super DHTNode>) new Comparator<DHTNode>() {
	    @Override
	    public int compare(DHTNode node1, DHTNode node2) {
	        return (int)(node1.getID() - node2.getID());
	    }
	});
	System.out.println("taille DHT = "+ DHT.size() + "network taille = "+ Network.size());
	
	DHT.get(0).setLeftNeighboor(DHT.get(DHT.size()-1));
	DHT.get(0).setRightNeighboor(DHT.get(1));
	DHT.get(0).setIdList(list_id);
	System.out.println();

	
	DHT.get(DHT.size()-1).setRightNeighboor(DHT.get(0));
	DHT.get(DHT.size()-1).setLeftNeighboor(DHT.get(DHT.size()-2));
	DHT.get(DHT.size()-1).setIdList(list_id);
	System.out.println();
	
	for(int i =1; i<DHT.size()-1; i++) {
		DHT.get(i).setLeftNeighboor(DHT.get(i-1));
		DHT.get(i).setRightNeighboor(DHT.get(i+1));
		DHT.get(i).setIdList(list_id);
	System.out.println();
	}
/*	
	for(int i=1 ; i<DHT.size();i++ ) {
		emitter.send(helloMsg, Network.get(i));
	}
*/
	//methode pour ajouter un noeud

	for(int i=0; i<DHT.size(); i++) {
		System.out.println(DHT.get(i).show_neigh());
	}
	System.out.println("finis!!!!!!!!!!!!!");	
	for(int i=0;i<14; i++) {
	this.add_a_node();
	try {Thread.sleep(1000);}
	catch(Exception e) {System.out.println("fonctionne pas super");}
	}


/*	for(int i=0; i<10;i++) {
		this.add_a_node();
		System.out.println("au noeuds "+ this.nextNode+"\n");
	}*/

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
