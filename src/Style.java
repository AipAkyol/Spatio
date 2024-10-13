import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

public class Style {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("org.graphstream.ui", "swing");

        // Create the graph
        Graph graph = new SingleGraph("Graph with External CSS");

        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        // Add nodes and edges
        Node nodeA = graph.addNode("A");
        Node nodeB = graph.addNode("B");
        Edge edgeAB = graph.addEdge("AB", "A", "B");

        // Set class attributes for custom styles
        nodeA.setAttribute("ui.class", "A");
        edgeAB.setAttribute("ui.class", "AB");

        // Load the external CSS file
        String cssFilePath = ".\\resources/temp.css"; // Replace with the absolute path to your CSS file
        graph.setAttribute("ui.stylesheet", "url('" + cssFilePath + "')");

        // Display the graph
        graph.display();
        Thread.sleep(5000);

        nodeA.removeAttribute("ui.class");
        edgeAB.removeAttribute("ui.class");

    }
}