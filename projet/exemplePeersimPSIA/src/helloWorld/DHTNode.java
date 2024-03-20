package helloWorld;
import java.util.Queue;


import peersim.edsim.EDProtocol;
import peersim.edsim.*;
import peersim.core.*;
import peersim.config.*;

import java.util.ArrayList;

public class DHTNode implements EDProtocol{
	private int transportPid;
	public int id_transp;
	private boolean addingNode = false; // on utilise un booléen pour savoir si on est en train d'ajouter un noeuds et si c'est le cas
	private Queue<Message> messQueue ;

    //objet couche transport
    private HWTransport transport;

    //identifiant de la couche courante (la couche applicative)
    private int mypid;

    //prefixe de la couche (nom de la variable de protocole du fichier de config)
    private String prefix;
	//on définit l'ID du noeuds, et ses voisins
	private int ID;
	private ArrayList<DHTNode> voisins = new ArrayList<>();
	private DHTNode rightNeighboor;
	private DHTNode leftNeighboor;
	
	public DHTNode( String prefix) {
		this.prefix = prefix;
		//initialisation des identifiants a partir du fichier de configuration
		this.transportPid = Configuration.getPid(prefix + ".transport");
		this.mypid = Configuration.getPid(prefix + ".myself");
		System.out.println("mypid = "+ this.mypid);
		this.transport = null;
	}
	// on règle les voisins du noeuds
	public void setRightNeighboor(DHTNode rightNei) {
		System.out.println("le noeuds "+ this.ID+" à comme voisin droit "+ rightNei);
		this.rightNeighboor = rightNei;
	}
	
	public void setTransportLayer(int id_transp, int nodeId) {
		this.ID = nodeId;
		this.id_transp = id_transp;
		this.transport = (HWTransport) Network.get(this.id_transp).getProtocol(this.transportPid);
	    }
	
	public void setLeftNeighboor(DHTNode leftNei) {
		this.leftNeighboor = leftNei;
		System.out.println("le noeuds"+ this.ID+" à comme voisin droit "+ leftNei);

	}
	
    public void send(Message msg, Node dest) {
	this.transport.send(getMyNode(), dest, msg, this.mypid);
    }

    //affichage a la reception
    private void receive(Message msg) {
	System.out.println(this + ": Received " + msg.getContent());
    }
    
    public void processEvent( Node node, int pid, Object event ) {
    	this.receive((Message)event);
        }

    //retourne le noeud courant
    private Node getMyNode() {return Network.get(this.ID);}
    
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
	
	public void addNode(DHTNode node) {
		this.addingNode = true; // on informe qu'on est entrain d'ajouter un noeuds.
		//code pour ajouter un noeuds
		if(node.getID()<this.getID() && this.leftNeighboor.getID()>this.getID()) { // si le noeuds est inférieur au noeuds courant mais que le noeuds droit est supérieur entre le noeuds droit alors le noeuds ajouter ce situeras entre les deux
			node.setRightNeighboor(this);// au noeuds que l'on souhaite ajouter on set le voisin droit comme le noeuds courant
			node.setLeftNeighboor(this.leftNeighboor);// au noeuds que l'on ajoute on set le voisin gauche comme notre voisin
			this.setLeftNeighboor(node);// on set le nouveau voisin droit comme le noeuds que l'on a ajouté
		}
		else if (node.getID()>this.getID() && node.getID()<this.rightNeighboor.getID()) {
			node.setLeftNeighboor(this);
			node.setRightNeighboor(this.rightNeighboor);
			this.setRightNeighboor(node);
		}
		
		else if (node.getID()<this.leftNeighboor.getID()){
			this.leftNeighboor.addNode(node);
		}
		else if(node.getID()>this.rightNeighboor.getID()){
			this.rightNeighboor.addNode(node);
		}
		else if(node.getID()>this.leftNeighboor.getID() && node.getID()<this.getID()) {// si le noeuds est supérieur au noeuds gauche mais inférieur au noeuds courant
			node.setRightNeighboor(this);// au noeuds que l'on souhaite ajouter on set le voisin droit comme le noeuds courant
			node.setLeftNeighboor(this.leftNeighboor);// au noeuds que l'on ajoute on set le voisin gauche comme notre voisin
			this.setLeftNeighboor(node);// on set le nouveau voisin droit comme le noeuds que l'on a ajouté
			
		}
		else if(node.getID()<this.rightNeighboor.getID() && node.getID()>this.getID()) {
			node.setLeftNeighboor(this);
			node.setRightNeighboor(this.rightNeighboor);
			this.setRightNeighboor(node);
		}
		else if(node.getID()) {
			
		}
		this.addingNode = false;
		
	}
	
	// on ajoute une fonction appeler quand un noeuds désir quitter le réseau
	public void leave() {
		this.addingNode = true;
		this.leftNeighboor.setRightNeighboor(this.rightNeighboor);
		this.rightNeighboor.setRightNeighboor(this.leftNeighboor);
	}
	// lorsque le noeuds décide de quitter on envoi l'information au voisin qui arrête d'envoyer les message au noeuds, par la suite le noeuds donne à un de ces voisins les message reçu entre temps
	
	
	



}
