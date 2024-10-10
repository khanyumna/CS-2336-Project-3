//Name: Yumna Khan, netID: YXK220040
import java.io.*;
import java.util.*;

public class Main {

    //graph class for Galaxy Map
    public static class Graph {
        private Map<String, List<Edge>> aMap; //create adjacency map
        
        //constructor for adjacency map
        public Graph() {
            this.aMap = new HashMap<>();
        }
        
        //method to insert vertex into the graph
        public void insertVertex(String vertex) {
            aMap.put(vertex, new ArrayList<>());
        }
        
        //method to insert an edge between two vertices with given weight
        public void insertEdge(String source, String destination, int weight) {
            // Ensure adjacency list for the source vertex 
            if (!aMap.containsKey(source)) {
                insertVertex(source);
            }
    
            // Ensure adjacency list for the destination vertex
            if (!aMap.containsKey(destination)) {
                insertVertex(destination);
            }
    
            // add vertex
            aMap.get(source).add(new Edge(destination, weight));
            // undirected graph, add reverse edge
            aMap.get(destination).add(new Edge(source, weight));
        }
        
        //method for getting edges
        public List<Edge> getEdges(String vertex) {
            return aMap.get(vertex);
        }
        
        // class for edge (destination and weight)
        private static class Edge {
            String destination;
            int weight;
    
            Edge(String destination, int weight) {
                this.destination = destination;
                this.weight = weight;
            }
        }
    }
        
    // main method
    public static void main(String[] args) {
        try {
            // read the galaxy map and create the graph
            Graph graph = readGalaxyMap("galaxy_map.txt");

            // analyze patrols and write results to the output file
            analyzePatrols(graph, "pilot_routes.txt", "patrols.txt");

            System.out.println("Analysis completed. Results written to " + "patrols.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method to read galaxy map file + create graph
    private static Graph readGalaxyMap(String galaxyMap) throws IOException {
        Graph graph = new Graph();

        try (BufferedReader reader = new BufferedReader(new FileReader(galaxyMap))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" "); //split line into parts
                String vertex = parts[0];  // get first part as current vertex
                graph.insertVertex(vertex); //insert vertex into the graph

                for (int i = 1; i < parts.length; i++) { //process remaining edges
                    String[] edgeParts = parts[i].split(","); //split each edge into destination and weight
                    String destination = edgeParts[0];
                    int weight = Integer.parseInt(edgeParts[1]);
                    graph.insertEdge(vertex, destination, weight); // insert edge into graph
                }
            }
        }

        return graph;
    }

    // method to analyze patrols and write output file
    private static void analyzePatrols(Graph graph, String galaxyMap, String output) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(galaxyMap));
             BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String pilotName = parts[0]; //get pilot name and starting vertex
                String startVertex = parts[1];

                List<String> patrolPath = Arrays.asList(parts).subList(2, parts.length); //get patrol path as list

                int pathWeight = calculateWeight(graph, startVertex, patrolPath); //calculate weight
                boolean isValid = pathWeight > 0;

                writer.write(pilotName + "\t" + pathWeight + "\t" + (isValid ? "valid" : "invalid")); //check if path is valid
                writer.newLine();
            }
        }
    }

    // method to calculate weight of patrol paths
    private static int calculateWeight(Graph graph, String vertex, List<String> patrolPath) {
        int weight = 0;
        String currentVertex = vertex;

        for (String nextVertex : patrolPath) {
            List<Graph.Edge> edges = graph.getEdges(currentVertex); // get edges of current vertex
            for (Graph.Edge edge : edges) { // iterate through edges to find one with matching destination
                if (edge.destination.equals(nextVertex)) {
                    weight += edge.weight; //add weight of matching edge to total
                    break;
                }
            }

            // if the edge doesn't exist, the path is invalid
            boolean edgeExists = false;
            for (Graph.Edge edge : graph.getEdges(currentVertex)) {
                if (edge.destination.equals(nextVertex)) {
                    edgeExists = true;
                    break;
                    }
                }

                if (!edgeExists) { //return 0 if edge doesn't exist
                    return 0;
                }
            currentVertex = nextVertex;
        }
        return weight; //return total weight of the path
    }
}

