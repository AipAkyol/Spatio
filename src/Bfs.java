import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;
import org.graphstream.algorithm.generator.*;
import java.util.*;

public class Bfs {
    public static void main(String[] args) throws Exception {
        System.setProperty("org.graphstream.ui", "swing");

        Graph graph = new SingleGraph("BFS Example Grid");

        String styleSheet = 
            "node {" +
            "   size: 9px;" + 
            "   fill-color: black;" + // Set a default color for nodes
            "   text-size: 12px;" + // Control label size (optional)
            "}" +
            "edge {" +
            "   size: 3px;" + // Set edge size
            "   fill-color: grey;" +  // Set default color for edges
            "}";

        // Set the style sheet
        graph.setAttribute("ui.stylesheet", styleSheet);
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        // Generate a grid graph
        Generator gen = new GridGenerator();
        gen.addSink(graph);
        gen.begin();

        // Generate the grid with 4 rows and 4 columns (16 nodes in total)
        for (int i = 0; i < 4; i++) {
            gen.nextEvents();
        }
        gen.end();

        // Display the graph
        Viewer viewer = graph.display(false);

        // Perform BFS starting from the top-left corner (Node0)
        bfsWithHighlight(graph, "0_0");
    }

    // Manual BFS implementation with edge highlighting
    public static void bfsWithHighlight(Graph graph, String startNodeId) throws InterruptedException {
        // Queue for BFS and Set to keep track of visited nodes
        Queue<Node> queue = new LinkedList<>();
        Set<Node> visited = new HashSet<>();  // Track visited nodes
        List<Edge> previousLayerEdges = new ArrayList<>(); // Store edges from the previous layer

        // Start from the initial node (Node0 in the grid)
        Node startNode = graph.getNode(startNodeId);
        queue.add(startNode);
        visited.add(startNode);

        // Perform BFS traversal layer by layer
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Edge> currentLayerEdges = new ArrayList<>(); // Store edges for the current layer

            // Process nodes at the current layer
            for (int i = 0; i < size; i++) {
                Node currentNode = queue.poll();

                // Explore each edge of the current node
                currentNode.edges().forEach(edge -> {
                    Node neighbor = edge.getOpposite(currentNode);

                    // If the neighbor hasn't been visited, add it to the queue
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                        currentLayerEdges.add(edge); // Add edge to the current layer
                    }
                });
            }

            // Highlight forward edges leading to the current layer nodes
            highlightEdges(currentLayerEdges, "red", 0.3);

            // Highlight backward edges leading to the previous layer nodes
            // Iterate over the nodes in the queue to find previously visited edges
            for (Node currentNode : queue) { // Loop over the current layer nodes
                currentNode.edges().forEach(edge -> {
                    Node previousNode = edge.getOpposite(currentNode);
                    // Check if the previous node was visited and if the edge is not already highlighted
                    if (visited.contains(previousNode) && !currentLayerEdges.contains(edge)) {
                        currentLayerEdges.add(edge); // Add backward edge to the current layer
                    }
                });
            }

            // Highlight backward edges leading to the previous layer nodes
            highlightEdges(currentLayerEdges, "blue", 0.3); // Color them differently if desired

            // De-highlight edges from the previous layer before moving to the current layer
            dehighlightEdges(previousLayerEdges, "grey");

            // Update previous layer edges to the current layer for the next iteration
            previousLayerEdges.clear(); // Clear the previous layer edges
            previousLayerEdges.addAll(currentLayerEdges); // Store the current layer edges for the next iteration
        }
    }

    // Function to highlight a list of edges with a specific color for a given duration
    public static void highlightEdges(List<Edge> edges, String color, double durationInSeconds) throws InterruptedException {
        // Change the color of all edges in the list
        for (Edge edge : edges) {
            edge.setAttribute("ui.style", "fill-color: " + color + ";");
        }

        // Sleep for the given duration (convert seconds to milliseconds)
        Thread.sleep((int)(durationInSeconds * 1000));
    }

    // Function to dehighlight edges after highlighting
    public static void dehighlightEdges(List<Edge> edges, String defaultColor) {
        // Reset the color of all edges in the list back to default (grey)
        for (Edge edge : edges) {
            edge.setAttribute("ui.style", "fill-color: " + defaultColor + ";");
        }
    }
}
