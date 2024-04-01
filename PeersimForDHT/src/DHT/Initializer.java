package DHT;

import peersim.core.*;
import peersim.config.*;
import java.util.Random;

import java.util.ArrayList;
import java.util.Comparator;


    private int DHTPid;

    public Initializer(String prefix) {
	//recuperation du pid de la couche applicative
    	this.DHTPid = Configuration.getPid(prefix + ".dhtProtocolPid");
    }

	/*
	Fonction pour ajouter un noeud à la DHT
	 */
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


	/*
	On définit un noeuds emitter dont lequel on va se servir pour envoyer des messagges au noeuds de la DHT depuis le fichier Initializer
	 */
	emitter = (DHTNode) Network.get(0).getProtocol(this.DHTPid);
	emitter.setTransportLayer(0, 0);
	

	
	
	//définit les noeuds en liaisons au début de la simulation
	for(int i =1; i<4; i++) {
		int randNum = random.nextInt(1,tailleRandom);
		nextNode = nextNode + 1; // variables qui permets de connaitre la place du nouveau noeuds que l'on peut ajouter à la DHT
		currentNode = Network.get(i);
		mynode = (DHTNode)currentNode.getProtocol(this.DHTPid);
		mynode.setTransportLayer(i,randNum);
		DHT.add(mynode);
		list_id.add(randNum);
	}
	// on trie les noeuds et on attribue à chacun ses voisins
	DHT.sort((Comparator<? super DHTNode>) new Comparator<DHTNode>() {
	    @Override
	    public int compare(DHTNode node1, DHTNode node2) {
	        return (int)(node1.getID() - node2.getID());
	    }
	});

	/*
	On mets en réseau les noeuds de la DHT
	 */
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
	On interragit avec la DHT, on ajoute des noeuds, et on envoie des messages à des noeuds de la DHT qui effectuerons des actions en fonction

	On ne peut pas ajouter plusieurs noeuds, puisque les actions dans l'initializer se passe de façons simultanné pour la DHT et entraîne des problèmes
	et malheuresement nous n'avons pas trouvé le moyen d'ajouter des noeuds au sein de la simulation.
	 */

	this.add_a_node();
	/*
	for(int i = 0; i<10; i++) {
		this.add_a_node();
	}
	*/
	for(int i=0 ; i<DHT.size();i++ ) {
		emitter.send(new Message(DHT.get(i).getID(), "hello"),DHT.get(i).getMyNode()); //envoie de message à tous les membres de la DHT
	}
	//methode pour ajouter un noeud

	


	System.out.println("Initialization completed");
	return false;
    }
}
