import peersim.core.Protocol;

// Permettre l'utilisation de cette classe dans le cadre de PeerSim
public class DHTNode implements Protocol {
    
    public long nodeId;
    public long leftNeighborId;
    public long rightNeighborId;

    public DHTNode(String prefix) {
        nodeId = -1;
        leftNeighborId = -1;
        rightNeighborId = -1;
    }

    // Créer une copie de l'objet DHTNode pour dupliquer les nœuds.
    public Object clone() {
        DHTNode dhtNode = null;
        try {
            //Clonage de l'objet en utilisant la méthode clone de la superclasse Object
            dhtNode = (DHTNode) super.clone();
            // Copie des valeurs des identifiants du nœud et des voisins dans l'objet cloné
            dhtNode.nodeId = this.nodeId;
            dhtNode.leftNeighborId = this.leftNeighborId;
            dhtNode.rightNeighborId = this.rightNeighborId;
        } catch (CloneNotSupportedException e) {
            // Traitement de l'exception dans le cas où le clonage n'est pas supporté, en affichant la trace de l'exception
            e.printStackTrace();
        }
        return dhtNode;
    }
    
    
}
