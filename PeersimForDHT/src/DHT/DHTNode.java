package DHT;
import java.util.ArrayList;

import java.util.Queue;
import java.util.Random;
import java.util.LinkedList;

import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import java.lang.Math;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.FileHandler;

public class DHTNode implements EDProtocol{
	/*
	 * On déclare les différent Logger
	 */

	private static final Logger Logger_message= Logger.getLogger(DHTNode.class.getName());
	private static final Logger Logger_ajout_node= Logger.getLogger(DHTNode.class.getName());
	private static final Logger Logger_data= Logger.getLogger(DHTNode.class.getName());

/*
* On définit toutes les variable utilisé
*
* */
	private int transportPid;
	public int id_transp;
	private boolean addingNode = false; // on utilise un booléen pour savoir si on est en train d'ajouter un noeuds et si c'est le cas
	private Queue<Message> messQueue = new LinkedList<>(); // file d'attente dans laquelle on stocke les messages entre le moment où je leave et le moment où
	public ArrayList<Integer> list_id;
	public ArrayList<DHTNode> list_node_to_add = new ArrayList<>();

	private ArrayList<Data> list_data = new ArrayList<>();
	Random random = new Random();
    //objet couche transport
    private HWTransport transport;

    //identifiant de la couche courante (la couche applicative)
    private int mypid;

    //prefixe de la couche (nom de la variable de protocole du fichier de config)
    private String prefix;
	//on définit l'ID du noeuds, et ses voisins
	private int ID;
	private ArrayList<DHTNode> voisins = new ArrayList<>();
	private DHTNode rightNeighboor; // on définit les noeuds voisin
	private DHTNode leftNeighboor;
	
	public DHTNode( String prefix) {
		this.prefix = prefix;
		//initialisation des identifiants a partir du fichier de configuration
		this.transportPid = Configuration.getPid(prefix + ".transport");
		this.mypid = Configuration.getPid(prefix + ".myself");
		System.out.println("mypid = "+ this.mypid);
		this.transport = null;
		/*
		 * Configuration des Logger
		 */
		try{
			FileHandler fileHandler_message = new FileHandler("message.log");
			FileHandler fileHandler_join_node = new FileHandler("join_node.log");
			FileHandler fileHandler_data = new FileHandler("data.log");
			Logger_message.addHandler(fileHandler_message);
			Logger_ajout_node.addHandler(fileHandler_join_node);
			Logger_data.addHandler(fileHandler_data);
		}
		catch(IOException e){
			Logger_ajout_node.severe("erreur");
			Logger_message.severe("erreur");
		}
	}

	/*
	* On définit toutes les méthodes pour configuré ou modifier les configurations de notre noeuds
	* */
	public void setIdList(ArrayList<Integer> list_id) {this.list_id = list_id;	}
	
	
	
	public void setTransportLayer(int id_transp, int nodeId) { // fonction pour configurer la couche transport et affecter l'ID du noeuds
		this.ID = nodeId;
		this.id_transp = id_transp;
		this.transport = (HWTransport) Network.get(this.id_transp).getProtocol(this.transportPid);
	    }
	
	/*
	 * Configuration des voisins
	 */
	public void setRightNeighboor(DHTNode rightNei) {
		this.rightNeighboor = rightNei;
		System.out.println("le noeuds "+ this.getID()+" à comme voisin droit "+ this.rightNeighboor.getID());
		
	}
	
	public void setLeftNeighboor(DHTNode leftNei) {
		this.leftNeighboor = leftNei;
		System.out.println("le noeuds"+ this.getID()+" à comme voisin gauche "+ this.leftNeighboor.getID());

	}


/*
méthodes pour définir le comportement à la réception d'un événement
 */
	public void processEvent( Node node, int pid, Object event ) {
		try {Message msg = (Message) event;
			this.root(msg);// à la reception on caste l'évenement comme un message et on l'envoi à la méthode root
		}
		catch(Exception e){System.out.println(("problem" + e));}

	}

	/*
	*  méthode root sert à orienté les message en fonction de leurs nature
	* */
	private void root(Message msg){
		if (!this.addingNode) {
			switch (msg.getType()) { // on fait la différence entre les message et les noeuds à ajouter et la data
				case "MSG":
					this.receive(msg);
					break;
				case "NODE":
					this.root_node(msg);
					break;
				case "DATA":
					this.put_data(msg);
					break;
			}
		}
		else {messQueue.add(msg);}
	}
    public void send(Message msg, Node dest) {
    //System.out.println("envoi message " +msg);
	this.transport.send(getMyNode(), dest, msg, this.mypid);
    }

	/*Méthode qui traite les message dans la liste d'attente*/
    /private void treat_waiting_mess() {// fonction qui redirige les message après qu'un noeuds est été ajouté
		while(!this.messQueue.isEmpty()) {
			root(this.messQueue.poll());
		}
	}

	//affichage a la reception
	/*
	* Méthode receive réceptionne les messages de types messages
	* soit les renvoie en direction du destinataire
	* soit il est le destinataire et il peut renvoyer le message à un de ses voisin, il peut aussi créer une nouvelle data et demander à la placer dans la DHT
	* */
    private void receive(Message msg ) {
		// on vérifie le destinataire et on renvoi le message si nécessaire
    	if (this.getID()>msg.getID()) {
    		this.send(msg, this.rightNeighboor.getMyNode());
    	}
    	else if(this.getID()<msg.getID()) {
    		this.send(msg,(Node) this.rightNeighboor.getMyNode());
    	}
    	else {
    	System.out.println("message pour moi");
		Logger_data.info(" noeud "+ getID()+" reçoit message"  );
		// on définit l'action qu'on effectue en fonction d'un nombre aléatoire
    		int rand =random.nextInt(1,100);
    		if (rand<=10) {
       			this.send(msg, (Node) this.leftNeighboor);
       		}
       		else if(rand>=90) {
       			this.send(msg, (Node) this.rightNeighboor);
    		}
			else if (rand<=25) {
				Data data = new Data();
				Logger_data.info("le noeuds : "+getID()+" a crée la donnée "+ data.getID());
				this.put_data(new Message(data));}
		}
    }

	/*
	* Méthode pour orienté les noeuds, savoir si il faut le transmettre à un de ses voisin où si celui ci doit être ajouté comme voisin
	* */
	private void root_node(Message msg){
		if (msg.getSubtype().equals("NODE_TO_ADD")){
			join(msg.getNode());
		}
		else if(msg.getContent().equals("LEFT")){
			Logger_ajout_node.info("le node "+ getID() +" a ajouté"+ msg.getNode().getID()+" comme voisin gauche" );
			this.addingNode=true;
			this.setLeftNeighboor(msg.getNode());
			this.addingNode=false;
		}
		else if(msg.getContent().equals("RIGHT")){
			Logger_ajout_node.info("le node "+ getID() +" a pour voisin "+ msg.getNode().getID() );
			this.addingNode = true;
			this.setRightNeighboor(msg.getNode());
			this.addingNode=false;

		}
	}
	

	/*
	* Méthode qui envoi vers la méthode get si on désir placer une donnée ou si on souhaite en récupérer
	* */
	private void root_data(Message msg) {
		switch(msg.getSubtype()) {
		case "PUT":
			put_data(msg);
			break;
		case "GET":
			get_data(msg);
			break;
		}
	}
    
	private void put_data(Message msg){
		Data data = msg.getData();
		if(msg.getContent().equals("ADD")){ //si on est chargé de la stocké 
			Logger_data.info("Le noeuds : "+ getID() +" prend la data "+ data.getID());
			list_data.add(data);
		}
		else {
			int distance = Math.abs(getID() - data.getID());
			int distance_data_leftneig = Math.abs(leftNeighboor.getID() - data.getID());
			int distance_data_rightneig = Math.abs(rightNeighboor.getID() - data.getID());
			Message sendData = new Message(data,"PUT","");
			if (distance > distance_data_rightneig) {
				this.send(sendData, rightNeighboor.getMyNode());
			} else if (distance > distance_data_leftneig) {
				this.send(sendData, leftNeighboor.getMyNode());
			} else {
				sendData = new Message(data, "PUT","ADD");
				this.send(sendData, leftNeighboor.getMyNode());
				this.send(sendData, rightNeighboor.getMyNode());
				Logger_data.info("Le noeuds : "+ getID() +" prend la data "+ data.getID());
				this.list_data.add(data);
			}
		}
	}

	/*
	* Méthodes qui permets d'ajouter un noeuds à la DHT
	* */

	public void join(DHTNode node) {
		this.addingNode = true; // on informe qu'on est entrain d'ajouter un noeuds.
		//code pour ajouter un noeuds
		System.out.println(node);
		if(node.getID()<this.getID() ) {
			if(node.getID()>this.leftNeighboor.getID()|| this.getID()< this.leftNeighboor.getID()) { // si le noeuds est inférieur au noeuds courant mais que le noeuds droit est supérieur entre le noeuds droit alors le noeuds ajouter ce situeras entre les deux
				Logger_ajout_node.info("node "+ this.getID() +" a ajouté "+node.getID()+ " comme noeuds droits");
				send(new Message("RIGHT",this), node.getMyNode());//setRightNeighboor(this);// au noeuds que l'on souhaite ajouter on set le voisin droit comme le noeuds courant
				send(new Message("LEFT", leftNeighboor), node.getMyNode());//setLeftNeighboor(this.leftNeighboor);// au noeuds que l'on ajoute on set le voisin gauche comme notre voisin
				send(new Message("RIGHT", node), leftNeighboor.getMyNode());//this.leftNeighboor.setRightNeighboor(node);
				this.setLeftNeighboor(node);// on set le nouveau voisin droit comme le noeuds que l'on a ajouté
			}
			else {
				send(new Message(node), leftNeighboor.getMyNode());//this.leftNeighboor.join(node);
			}

		}
		else if(node.getID()>this.getID()){
			if (node.getID()<this.rightNeighboor.getID() || this.getID() > this.rightNeighboor.getID()) { // si le noeuds à ajouter > noeuds courant mais <noeuds droit on ahoute entre les deux
				Logger_ajout_node.info("node " + this.getID() +" a ajouté "+node.getID()+ " comme noeuds gauche");
				send(new Message("LEFT", this), node.getMyNode());//node.setLeftNeighboor(this);
				send(new Message("RIGHT", this.rightNeighboor), node.getMyNode());//node.setRightNeighboor(this.rightNeighboor);
				send(new Message("LEFT", node), rightNeighboor.getMyNode());//this.rightNeighboor.setLeftNeighboor(node);
				this.setRightNeighboor(node);
			}
			else {
				send(new Message(node), rightNeighboor.getMyNode());//this.rightNeighboor.join(node);
			}


			this.addingNode = false;
			this.treat_waiting_mess();
		}
	}

	//  pour la fonction leave on triche, on ne passe pas par la couche réseau
	public void leave() {
		this.leftNeighboor.setRightNeighboor(this.rightNeighboor);
		this.rightNeighboor.setRightNeighboor(this.leftNeighboor);
	}
	/**
	 *Toutes les méthodes d'accesseurs au variable du noeuds
	 */

	private void get_data(Message msg) {} // fonction pour récupérer une donné, pas eu le temps de l'implémenter

    //retourne le noeud courant
    public Node getMyNode() {return Network.get(this.id_transp);}

    public int getID() {return this.ID;}

    public String toString() {return "Node "+ this.ID;}

	public int getFailState() {return 0;}

	public Object clone() {
        try {
            // Utilisez la méthode super.clone() pour créer une copie superficielle
            DHTNode clone = (DHTNode) super.clone();
            // Ajoutez ici la logique pour cloner d'autres propriétés si nécessaire
            return clone;
        } catch (CloneNotSupportedException e) {
            // Gérer l'exception si la classe n'est pas clonable
            throw new AssertionError("La classe n'est pas clonable", e);
        }
    }


	// lorsque le noeuds décide de quitter on envoi l'information au voisin qui arrête d'envoyer les message au noeuds, par la suite le noeuds donne à un de ces voisins les message reçu entre temps

	public String show_neigh() {
		return "noeuds courant " + this.getID()+" voisin gauche = "+ this.leftNeighboor.getID() + " voisin droit = "+ this.rightNeighboor.getID();
	}

	}






