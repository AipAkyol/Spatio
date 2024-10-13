import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

public class DynamicGraphExample {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("org.graphstream.ui", "swing");
        // Create a graph
        Graph graph = new SingleGraph("Dynamic Graph Example");
        
        // Enable auto-layout and visualization
        graph.display();

        // Add nodes and edges dynamically
        for (int i = 0; i < 10; i++) {
            String nodeId = "Node" + i;

            // Add a new node
            Node node = graph.addNode(nodeId);
            node.setAttribute("ui.label", nodeId);

            if (i > 0) {
                // Add an edge between the new node and the previous one
                String edgeId = "Edge" + (i - 1) + "-" + i;
                graph.addEdge(edgeId, "Node" + (i - 1), nodeId);
            }

            // Sleep for a second to simulate dynamic changes over time
            Thread.sleep(1000);
        }

        // Remove a node after some time (e.g., 2 seconds later)
        Thread.sleep(2000);
        graph.removeNode("Node5");

        // Add more nodes or modify graph as needed
    }
}