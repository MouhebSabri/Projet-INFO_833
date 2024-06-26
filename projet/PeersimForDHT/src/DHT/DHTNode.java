package helloWorld;
import java.util.ArrayList;

import java.util.Queue;
import java.util.Random;
import java.util.LinkedList;

import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;

public class DHTNode implements EDProtocol{
	private int transportPid;
	public int id_transp;
	private boolean addingNode = false; // on utilise un booléen pour savoir si on est en train d'ajouter un noeuds et si c'est le cas
	private Queue<Message> messQueue = new LinkedList<>(); // file d'attente dans laquelle on stocke les messages entre le moment où je leave et le moment où
	public ArrayList<Integer> list_id;

	private ArrayList<Data> data = new ArrayList<>();
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
	}
	
	public void setIdList(ArrayList<Integer> list_id) {this.list_id = list_id;	}
	
	// on règle les voisins du noeuds
	
	
	public void setTransportLayer(int id_transp, int nodeId) {
		this.ID = nodeId;
		this.id_transp = id_transp;
		this.transport = (HWTransport) Network.get(this.id_transp).getProtocol(this.transportPid);
	    }
	// on réalise deux fonction pour setter les voisin du noeuds
	public void setRightNeighboor(DHTNode rightNei) {
		this.rightNeighboor = rightNei;
		//System.out.println("le noeuds "+ this.id_transp+" à comme voisin droit "+ this.rightNeighboor.id_transp);
		
	}
	
	public void setLeftNeighboor(DHTNode leftNei) {
		this.leftNeighboor = leftNei;
		//System.out.println("le noeuds"+ this.id_transp+" à comme voisin  "+ this.leftNeighboor.id_transp);

	}
	
    public void send(Message msg, Node dest) {
	this.transport.send(getMyNode(), dest, msg, this.mypid);
    }

    //affichage a la reception
    private void receive(Message msg ) {
    	if(this.addingNode == true) {this.messQueue.add(msg);}
    	
    	else {
    		System.out.println("ID = "+ this.id_transp );
    		if (this.getID()>msg.getID()) {
    			this.send(msg, this.rightNeighboor.getMyNode());
    		}
    		else if(this.getID()<msg.getID()) {
    			this.send(msg,(Node) this.rightNeighboor.getMyNode());
    		}
    		else {
    			System.out.println(this + ": Received " + msg.getContent());
    			int rand =random.nextInt(1,100); 
    			if (rand<=15) {
        			this.send(msg, (Node) this.leftNeighboor);
        		}
        		else if(rand>=75) {
        			this.send(msg, (Node) this.rightNeighboor);
        		}
    		}
    		
    		
    	}
    }
    
    private void root_mess(Message mess) {}
    
    private void root() {// fonction qui redirige les message après qu'un noeuds est été ajouté
    	while(!this.messQueue.isEmpty()) {
    		receive(this.messQueue.poll());
    	}
    } 
    public void processEvent( Node node, int pid, Object event ) {
    	if(event instanceof Data){ // si c'est une data on la route comme il faut
		this.rootDATA(event)
	}
    	this.receive((Message)event);
        }

    //retourne le noeud courant
    private Node getMyNode() {return Network.get(this.id_transp);}
    
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
	
	
	public void join(DHTNode node) {
		this.addingNode = true; // on informe qu'on est entrain d'ajouter un noeuds.
		//code pour ajouter un noeuds
		if(node.getID()<this.getID() ) {
			if(node.getID()>this.leftNeighboor.getID()|| this.getID()< this.leftNeighboor.getID()) { // si le noeuds est inférieur au noeuds courant mais que le noeuds droit est supérieur entre le noeuds droit alors le noeuds ajouter ce situeras entre les deux
				node.setRightNeighboor(this);// au noeuds que l'on souhaite ajouter on set le voisin droit comme le noeuds courant
				node.setLeftNeighboor(this.leftNeighboor);// au noeuds que l'on ajoute on set le voisin gauche comme notre voisin
				this.leftNeighboor.setRightNeighboor(node);
				this.setLeftNeighboor(node);// on set le nouveau voisin droit comme le noeuds que l'on a ajouté
			}
			else {
				this.leftNeighboor.join(node);
			}
			
		}
		else if(node.getID()>this.getID()){
			if (node.getID()<this.rightNeighboor.getID() || this.getID() > this.rightNeighboor.getID()) { // si le noeuds à ajouter > noeuds courant mais <noeuds droit on ahoute entre les deux
				node.setLeftNeighboor(this);
				node.setRightNeighboor(this.rightNeighboor);
				this.rightNeighboor.setLeftNeighboor(node);
				this.setRightNeighboor(node);
			}
			else {
				this.rightNeighboor.join(node);
			}
			
		}
			
		this.addingNode = false;
		this.root();
		
	}
	
	// on ajoute une fonction appeler quand un noeuds désir quitter le réseau
	public void leave() {
		this.leftNeighboor.setRightNeighboor(this.rightNeighboor);
		this.rightNeighboor.setRightNeighboor(this.leftNeighboor);
	}
	// lorsque le noeuds décide de quitter on envoi l'information au voisin qui arrête d'envoyer les message au noeuds, par la suite le noeuds donne à un de ces voisins les message reçu entre temps
	
	public String show_neigh() {
		return "noeuds courant " + this.getID()+" voisin gauche = "+ this.leftNeighboor.getID() + " voisin droit = "+ this.rightNeighboor.getID();
	}

	//on va représenter une donnée comme un message est un ID, l'ID sera désigné de façon aléatoire
	// pour ajouter une data on peut se dire que 
	public void put_data(Data data){
	// on va regardé l'ID du message et on routera si nécessaire
	}
	
	



}
