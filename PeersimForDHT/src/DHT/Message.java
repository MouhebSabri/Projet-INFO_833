package DHT;

public class Message {

    public final static int HELLOWORLD = 0;

    private int id_dest_finale;
 //   private int type;
    private String content;
    private String type;
    private DHTNode node_to_add;
    private Data data;
    private String subtype;



    Message(int id_dest,  String content) {
	this.content = content;
	this.id_dest_finale = id_dest;
    this.type="MSG";
    }

    Message(DHTNode node_to_add){//constructeur Le noeuds n'a pas été ajouté
	this.type= "NODE";
	this.subtype = "NODE_TO_ADD";
	this.node_to_add = node_to_add;
    }

    Message(String content, DHTNode node_to_add ){//constructeur le destinataire doir ajouter le noeuds comme un de ses voisins
    this(node_to_add);
    this.content = content;
    }



    Message(Data data){
        this.type = "DATA";
        this.data =data;
    }
    Message(Data data, String qqc){
        this(data);
        this.subtype = "ADD_DATA";
    }

    public DHTNode getNode(){return this.node_to_add;}

    public String getType(){return this.type;}
    public String getSubtype(){return this.subtype;}

    public String getContent() {
	return this.content;
    }

 //   public int getType() {return this.type;}
    
    public int getID() {return this.id_dest_finale;}

    public Data getData(){return this.data;}
    
    public String toString(){return "content = "+content+" type : "+type+" subtype = "+ subtype + " node ="+ node_to_add;}
}
