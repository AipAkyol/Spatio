import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.view.Viewer;

import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.print.DocFlavor.STRING;

import org.graphstream.algorithm.generator.*;;

public class App {
    public static final int CLOCK_CYCLE = 5;
    public static int clock = CLOCK_CYCLE; // The clock value for new interrupts
    public static ArrayList<String> deletedEdges = new ArrayList<String>();
    
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

        int graphSize = 20; // Change this value for the number of nodes you want (graphSize*graphSize)
        for(int i=0; i+1 < graphSize; i++) {
	        gen.nextEvents();
        }

        gen.end();

        graph.edges().forEach(e -> {
            e.setAttribute("power", 4);
            e.setAttribute("ui.label", e.getAttribute("power"));
        }); 

        graph.nodes().forEach(node -> {
            // randomly assign a type for each node, 30% chance of being a inhibitor, 70% chance of being a exhibitor
            int random = (int) (Math.random() * 100);
            if (random < 20) {
                node.setAttribute("type", "inhibitor");
                node.setAttribute("ui.class", "inhibitor");
            } else {
                node.setAttribute("type", "exhibitor");
                node.setAttribute("ui.class", "exhibitor");
            }
            node.setAttribute("pastMiliVolts", 0);
            node.setAttribute("currentMiliVolts", 0);
            node.setAttribute("threshold", 3);
            node.setAttribute("totalEdgePower", 0);
            node.edges().forEach(e -> {
                int newEdgePower = (int) node.getAttribute("totalEdgePower") + (int) e.getAttribute("power");
                node.setAttribute("totalEdgePower", newEdgePower);
            });
            node.setAttribute("lastActiveTime", 0);
            node.setAttribute("ui.label", node.getAttribute("currentMiliVolts"));
        });

        // delete edges between inhibitors
        ArrayList<Edge> edgesToDelete = new ArrayList<Edge>();
        graph.edges().forEach(e -> {
            String sourceType = (String) e.getSourceNode().getAttribute("type");
            String targetType = (String) e.getTargetNode().getAttribute("type");
            if (sourceType.equals("inhibitor") && targetType.equals("inhibitor")) {
                edgesToDelete.add(e);
            }
        });
        edgesToDelete.forEach(e -> {
            graph.removeEdge(e);
        });

        // delete inhibitors with no edges
        ArrayList<Node> nodesToDelete = new ArrayList<Node>();
        graph.nodes().forEach(node -> {
            if (node.edges().count() == 0) {
                nodesToDelete.add(node);
            }
        });
        nodesToDelete.forEach(node -> {
            graph.removeNode(node);
        });
        

        //TODO: false or not
        Viewer viewer = graph.display();
        Thread.sleep(2000); // Wait for 2 seconds to see the initial graph

        //select two random nodes to interrupt from left side ids between 0_0 and (GraphSize-1)_0
        int random1 = (int) (Math.random() * graphSize);
        int random2 = (int) (Math.random() * graphSize);
        while (random1 == random2) {
            random2 = (int) (Math.random() * graphSize);
        }
        String node1 = "0_" + random1;
        String node2 = "0_" + random2;
        graph.getNode(node1).setAttribute("currentMiliVolts", 10);
        graph.getNode(node2).setAttribute("currentMiliVolts", 10);
        graph.getNode(node1).setAttribute("ui.label", graph.getNode(node1).getAttribute("currentMiliVolts"));
        graph.getNode(node2).setAttribute("ui.label", graph.getNode(node2).getAttribute("currentMiliVolts"));
        Thread.sleep(2000); // Wait for 2 seconds to see the initial interrupts

        while (true) {
            clock--;
            // if clock is 0, interrupt new nodes randomly
            if (clock == 0) {
                int random3 = (int) (Math.random() * graphSize);
                int random4 = (int) (Math.random() * graphSize);
                while (random3 == random4) {
                    random4 = (int) (Math.random() * graphSize);
                }
                String node3 = "0_" + random3;
                String node4 = "0_" + random4;
                // increase the voltage of the nodes
                int node3Voltage = (int) graph.getNode(node3).getAttribute("currentMiliVolts") + 10;
                int node4Voltage = (int) graph.getNode(node4).getAttribute("currentMiliVolts") + 10;
                graph.getNode(node3).setAttribute("currentMiliVolts", node3Voltage);
                graph.getNode(node4).setAttribute("currentMiliVolts", node4Voltage);
                graph.getNode(node3).setAttribute("ui.label", graph.getNode(node3).getAttribute("currentMiliVolts"));
                graph.getNode(node4).setAttribute("ui.label", graph.getNode(node4).getAttribute("currentMiliVolts"));
                clock = CLOCK_CYCLE;
            }

            // for 10% chance, resurrect one of the deleted edges
            int random5 = (int) (Math.random() * 100);
            if (random5 < 10) {
                if (deletedEdges.size() > 0) {
                    //randomly shuffle the deleted edges
                    deletedEdges.sort((a, b) -> 0.5 < Math.random() ? -1 : 1);
                    String edgeId = deletedEdges.get(0);
                    String[] edgeNodes = edgeId.split(" ");
                    Node source = graph.getNode(edgeNodes[0]);
                    Node dest = graph.getNode(edgeNodes[1]);
                    Edge edge = graph.addEdge(edgeId, source, dest);
                    edge.setAttribute("power", 4);
                    edge.setAttribute("ui.label", edge.getAttribute("power"));
                    deletedEdges.remove(0);
                    System.out.println("Resurrected edge: " + edgeId);
                }
            }


            Thread.sleep(10); // Wait for 1 second to see the new voltage of the nodes
            // Calculate the new voltage of the nodes
            calculateVoltage(graph);
            
        }
            
        

        
    }

    // Method to calculate the new voltage of the nodes
    public static void calculateVoltage(Graph graph)  throws InterruptedException {

        graph.nodes().forEach(node -> {

            node.setAttribute("pastMiliVolts", node.getAttribute("currentMiliVolts"));
            node.setAttribute("currentMiliVolts", 0);

        });

        // Calculate whether node will be interrupted or not
        graph.nodes().forEach(node -> {

            int[] totalInterruptedVoltage = {0};
            ArrayList<String> activeNeighbours = new ArrayList<String>();
            node.neighborNodes().forEach(neighbor -> {
                int edgePower = (int) neighbor.getEdgeBetween(node).getAttribute("power");
                int totalEdgePower = (int) neighbor.getAttribute("totalEdgePower");
                float edgeProportion = (float) edgePower / totalEdgePower;

                int pastMiliVolts = (int) neighbor.getAttribute("pastMiliVolts");
                float divisionResult = pastMiliVolts * edgeProportion;

                int neighborVoltage = Math.round(divisionResult);
                if (neighborVoltage == 0) {
                    return;
                }
                activeNeighbours.add(neighbor.getId());
                if (neighbor.getAttribute("type").equals("inhibitor")) {
                    neighborVoltage *= -1;
                } 
                totalInterruptedVoltage[0] += neighborVoltage;   
            });

            if (totalInterruptedVoltage[0] >= (int) node.getAttribute("threshold")) { // exhibitors won so increase bond power between and decrease bond power between inhibitors
                node.setAttribute("currentMiliVolts", totalInterruptedVoltage[0]);
                activeNeighbours.forEach(neighborId -> {
                    Node neighbor = graph.getNode(neighborId);
                    Edge edge = neighbor.getEdgeBetween(node);
                    if (neighbor.getAttribute("type").equals("inhibitor")) {
                        int newEdgePower = (int) edge.getAttribute("power") - 1;
                        edge.setAttribute("power", newEdgePower);
                        edge.setAttribute("ui.label", edge.getAttribute("power"));
                        if (newEdgePower == 1) {
                            //store the edge as deleted to create it later
                            String edgeId = node.getId() + " " + neighbor.getId();
                            deletedEdges.add(edgeId);
                            graph.removeEdge(edge);
                        }
                    } else {
                        int newEdgePower = (int) edge.getAttribute("power") + 1;
                        edge.setAttribute("power", newEdgePower);
                        edge.setAttribute("ui.label", edge.getAttribute("power"));
                    }
                });
                if ((int)node.getAttribute("lastActiveTime") < 5) {
                    node.setAttribute("threshold", (int) node.getAttribute("threshold") + 1);   
                }
                node.setAttribute("lastActiveTime", 0);
            
            } else { // inhibitors won so decrease bond power between and increase bond power between inhibitors
                
                node.setAttribute("currentMiliVolts", 0);
                activeNeighbours.forEach(neighborId -> {
                    Node neighbor = graph.getNode(neighborId);
                    Edge edge = neighbor.getEdgeBetween(node);
                    if (neighbor.getAttribute("type").equals("inhibitor")) {
                        int newEdgePower = (int) edge.getAttribute("power") + 1;
                        edge.setAttribute("power", newEdgePower);
                        edge.setAttribute("ui.label", edge.getAttribute("power"));
                    } else {
                        int newEdgePower = (int) edge.getAttribute("power") - 1;
                        edge.setAttribute("power", newEdgePower);
                        edge.setAttribute("ui.label", edge.getAttribute("power"));
                        if (newEdgePower == 1) {
                            //store the edge as deleted to create it later
                            String edgeId = node.getId() + " " + neighbor.getId();
                            deletedEdges.add(edgeId);
                            graph.removeEdge(edge);
                        }
                    }
                });
                if ((int)node.getAttribute("lastActiveTime") >= 5) {
                    if ((int)node.getAttribute("threshold") > 1) {
                        node.setAttribute("threshold", (int) node.getAttribute("threshold") - 1);
                        node.setAttribute("lastActiveTime", 0);  
                    } 
                }

            }
            
        node.setAttribute("ui.label", node.getAttribute("currentMiliVolts"));
        });
        Thread.sleep(1000);
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
