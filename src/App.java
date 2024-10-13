import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;

import org.graphstream.algorithm.generator.*;;

public class App {
    public static void main(String[] args) throws Exception {
        
        System.setProperty("org.graphstream.ui", "swing"); // Use the Swing display

		Graph graph = new SingleGraph("Grid"); // Create the grid

        // Increase the quality of the display
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        // Load the external CSS file
        String cssFilePath = ".\\resources/Grid-Style.css"; // Replace with the absolute path to your CSS file
        graph.setAttribute("ui.stylesheet", "url('" + cssFilePath + "')");

        // Generate a grid graph
        Generator gen = new GridGenerator();

        gen.addSink(graph);
        gen.begin();

        for(int i=0; i+1 < 5; i++) { // Change i for the number of nodes you want (i*i)
	        gen.nextEvents();
        }

        gen.end();

         

        graph.nodes().forEach(node -> {
            node.setAttribute("ui.class", "idle");
        });

        graph.edges().forEach(e -> {
            e.setAttribute("ui.class", "idle");
        });

        Viewer viewer = graph.display(false);
        Thread.sleep(2000); // Wait for 2 seconds to see the initial graph

        // String arraylist to hold the initial nodes to be interrupted
        ArrayList<String> startingNodes = new ArrayList<String>();
        startingNodes.add("0_0");

        // Edge arraylist to hold the connecting edges, initially empty
        ArrayList<String> connectingEdges = new ArrayList<String>();

        // Interrupt the nodes
        interruptNodes(graph, startingNodes, connectingEdges);
    }

    // Method to interrupt the nodes
    public static void interruptNodes(Graph graph, ArrayList<String> nodes, ArrayList<String> connectingEdges) throws InterruptedException {

        // new string arraylist to hold the nodes to be interrupted at next step
        ArrayList<String> nextNodes = new ArrayList<String>();
        // new string arraylist to hold the connecting edges to the next step
        ArrayList<String> nextEdges = new ArrayList<String>();

        // Highlight the edges from the previous step
        highlightEdges(graph, connectingEdges);
        // then de-highlight the edges
        dehighlightEdges(graph, connectingEdges);

        // Highlight the nodes in this step
        highlightNodes(graph, nodes);

        for (String nodeId : nodes) {

            Node currentNode = graph.getNode(nodeId);

            // Add neighbors to the nextNodes list
            currentNode.neighborNodes().forEach(neighbor -> {
                nextNodes.add(neighbor.getId());
            });

            // Add connecting edges to the nextEdges list
            currentNode.edges().forEach(edge -> {
                nextEdges.add(edge.getId());
            });


            //TODO: Implement the logic to interrupt the nodes
            
        }

        // dehighlight the nodes after calculations
        dehighlightNodes(graph, nodes);

        // Call the interruptNodes method recursively
        interruptNodes(graph, nextNodes, nextEdges);
    }

    // Method to highlight the edges concurrently
    public static void highlightEdges(Graph graph, ArrayList<String> edges) throws InterruptedException {
        for (String edgeId : edges) {
            Edge currentEdge = graph.getEdge(edgeId);
            currentEdge.setAttribute("ui.class", "highlighted");
        }
        // Sleep for some seconds to see the highlighted edges
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to de-highlight the edges concurrently
    public static void dehighlightEdges(Graph graph, ArrayList<String> edges) throws InterruptedException {
        for (String edgeId : edges) {
            Edge currentEdge = graph.getEdge(edgeId);
            currentEdge.setAttribute("ui.class", "idle");
        }
        // Sleep for some seconds to see the de-highlighted edges
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to highlight the interrupted nodes concurrently
    public static void highlightNodes(Graph graph, ArrayList<String> nodes) throws InterruptedException {
        for (String nodeId : nodes) {
            Node currentNode = graph.getNode(nodeId);
            currentNode.setAttribute("ui.class", "highlighted");
        }
        // Sleep for some seconds to see the highlighted nodes
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Method to de-highlight the interrupted nodes concurrently
    public static void dehighlightNodes(Graph graph, ArrayList<String> nodes) throws InterruptedException {
        for (String nodeId : nodes) {
            Node currentNode = graph.getNode(nodeId);
            currentNode.setAttribute("ui.class", "idle");
        }
        // Sleep for some seconds to see the de-highlighted nodes
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
