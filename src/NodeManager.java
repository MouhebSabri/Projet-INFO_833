import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

// Implémenter l'interface Control pour gérer des actions de contrôle dans PeerSim
public class NodeManager implements Control {
    //Représenter l'index du protocole DHTNode associé à chaque nœud
    private static final int PID = 0;

    public NodeManager(String prefix) {
    }

    // Exécuter une action de contrôle
    public boolean execute() {
        addNode();
        removeNode();
        //Indiquer que la simulation ne doit pas être arrêtée à ce point
        return false;
    }
    
    private void addNode() {
        // Création d'un nouveau nœud par clonage du prototype de nœud de PeerSim
        Node newNode = (Node) Network.prototype.clone();
        // Obtention du protocole DHTNode du nouveau nœud pour accéder à ses propriétés spécifiques
        DHTNode dhtNewNode = (DHTNode) newNode.getProtocol(PID);
        
        // Attribution d'un ID unique au nouveau nœud 
        dhtNewNode.nodeId = Network.size();
        
        // Si c'est le premier nœud ajouté , il est son propre voisin
        if (Network.size() == 1) {
            dhtNewNode.leftNeighborId = dhtNewNode.nodeId;
            dhtNewNode.rightNeighborId = dhtNewNode.nodeId;
        } else {
            // Insertion du nouveau nœud dans l'anneau DHT à sa place appropriée basée sur l'ID
            boolean inserted = false;
            for (int i = 0; i < Network.size(); i++) {
                Node currentNode = Network.get(i);
                DHTNode dhtCurrentNode = (DHTNode) currentNode.getProtocol(PID);
                
                // Recherche de la position correcte pour insérer le nouveau nœud basée sur l'ID
                if (dhtNewNode.nodeId < dhtCurrentNode.nodeId) {
                    // Mise à jour des références de voisins en fonction de la position du nouveau nœud
                    if (i > 0) {
                        Node leftNode = Network.get(i - 1);
                        DHTNode dhtLeftNode = (DHTNode) leftNode.getProtocol(PID);
                        dhtLeftNode.rightNeighborId = dhtNewNode.nodeId;
                        dhtNewNode.leftNeighborId = dhtLeftNode.nodeId;
                    } else {
                        // Traitement spécial si le nouveau nœud a le plus petit ID et doit être inséré au début
                        Node lastNode = Network.get(Network.size() - 1);
                        DHTNode dhtLastNode = (DHTNode) lastNode.getProtocol(PID);
                        dhtLastNode.rightNeighborId = dhtNewNode.nodeId;
                        dhtNewNode.leftNeighborId = dhtLastNode.nodeId;
                    }
                    
                    // Finalisation de l'insertion du nouveau nœud
                    dhtNewNode.rightNeighborId = dhtCurrentNode.nodeId;
                    dhtCurrentNode.leftNeighborId = dhtNewNode.nodeId;
                    inserted = true;
                    break;
                }
            }
            
            // Si le nouveau nœud a le plus grand ID et n'a pas encore été inséré
            if (!inserted) {
                Node firstNode = Network.get(0);
                Node lastNode = Network.get(Network.size() - 1);
                DHTNode dhtFirstNode = (DHTNode) firstNode.getProtocol(PID);
                DHTNode dhtLastNode = (DHTNode) lastNode.getProtocol(PID);
                
                // Mise à jour des références pour insérer le nouveau nœud à la fin de l'anneau
                dhtLastNode.rightNeighborId = dhtNewNode.nodeId;
                dhtNewNode.leftNeighborId = dhtLastNode.nodeId;
                dhtNewNode.rightNeighborId = dhtFirstNode.nodeId;
                dhtFirstNode.leftNeighborId = dhtNewNode.nodeId;
            }
        }
        
        // Ajout du nouveau nœud au réseau PeerSim
        Network.add(newNode);
    }

    // Méthode pour supprimer un nœud du réseau
    private void removeNode() {
        // Vérification qu'il y a plus d'un nœud dans le réseau pour permettre la suppression
        if (Network.size() > 1) {
            Node nodeToRemove = Network.get(0); // Sélection du premier nœud pour la suppression
            DHTNode dhtNodeToRemove = (DHTNode) nodeToRemove.getProtocol(PID);

            Node leftNeighbor = null;
            Node rightNeighbor = null;
            for (int i = 0; i < Network.size(); i++) {
                Node n = Network.get(i);
                DHTNode dhtNode = (DHTNode) n.getProtocol(PID);
                if (dhtNode.nodeId == dhtNodeToRemove.leftNeighborId) {
                    leftNeighbor = n;
                }
                if (dhtNode.nodeId == dhtNodeToRemove.rightNeighborId) {
                    rightNeighbor = n;
                }
            }

            // Mise à jour des références de voisins après la suppression du nœud
            if (leftNeighbor != null && rightNeighbor != null) {
                DHTNode leftDHTNode = (DHTNode) leftNeighbor.getProtocol(PID);
                DHTNode rightDHTNode = (DHTNode) rightNeighbor.getProtocol(PID);

                leftDHTNode.rightNeighborId = rightDHTNode.nodeId;
                rightDHTNode.leftNeighborId = leftDHTNode.nodeId;
            }

            // Suppression effective du nœud du réseau PeerSim
            Network.remove(0); // Ajoutez cette ligne pour supprimer le premier nœud
        }
    }

    
}
