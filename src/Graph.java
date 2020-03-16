import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

//increaseDegree and lowerDegree has some funkyness.

public class Graph {
    int V;

    LinkedList<Vertex> vertices;
    LinkedList<Vertex> degree0;
    LinkedList<Vertex> degree1;
    LinkedList<Vertex> degree2;
    LinkedList<Vertex> degree3;

    Graph(int V) {
        this.V = V;

        vertices = new LinkedList<>();

        // degree sorting
        degree0 = new LinkedList<>();
        degree1 = new LinkedList<>();
        degree2 = new LinkedList<>();

        // degree 3 or larger
        degree3 = new LinkedList<>();


        // initialize vertices
        for (int i = 0; i < this.V; i++) {
            vertices.add(i, new Vertex(i));
        }

    }
    static void addEdge(Graph graph, int src, int dest) {
        graph.vertices.get(src).neighbors.add(graph.vertices.get(dest));
        graph.vertices.get(dest).neighbors.add(graph.vertices.get(src));
    }

    static void sortByDegree(Graph graph) {
        graph.degree0.clear();
        graph.degree1.clear();
        graph.degree2.clear();
        graph.degree3.clear();
        for (Vertex vertex : graph.vertices) {
            if(!vertex.removed) {
                int degree = vertex.getDegree();
                if(degree == 0) {
                    graph.degree0.add(vertex);
                }
                else if(degree == 1) {
                    graph.degree1.add(vertex);
                }
                else if(degree == 2) {
                    graph.degree2.add(vertex);
                }
                else {
                    graph.degree3.add(vertex);
                }
            }
        }
    }

    static Graph readGraph(File file) throws FileNotFoundException {
        Scanner filesc = new Scanner(file);
        Scanner firstsc = new Scanner(filesc.nextLine());

        firstsc.skip("p edge");
        int vertices = firstsc.nextInt();
        Graph graph = new Graph(vertices);

        while(filesc.hasNextLine()) {
            Scanner linesc = new Scanner(filesc.nextLine());
            linesc.next("e");
            int srcEdge = linesc.nextInt()-1; //benchmark files are 1 indexed.
            int destEdge = linesc.nextInt()-1;

            addEdge(graph, srcEdge, destEdge);
        }

        sortByDegree(graph);
        return graph;
    }

    static Graph randomGraph(int maxdegree, int maxedgecount, int size) {
        Graph graph = new Graph(size);
        Random random = new Random();
        int edgecount = random.nextInt(maxedgecount);
        for(int i = 0; i <= edgecount; i++) {
            Vertex v1 = graph.vertices.get(random.nextInt(size));
            Vertex v2 = graph.vertices.get(random.nextInt(size));
            if(!v1.neighbors.contains(v2)) {
                if(v1.getDegree() < maxdegree && v2.getDegree() < maxdegree) {
                    if(v1 != v2) {
                        addEdge(graph, v1.id, v2.id);
                    }
                }
            }
        }
        sortByDegree(graph);
        return graph;
    }

    static void printGraph(Graph graph) {
        for(int v = 0; v < graph.V; v++) {
            if(graph.vertices.get(v).removed) {
                System.out.println("Vertex " + v + " removed");
            }
            else {
                System.out.println("Adjacency list of vertex "+ graph.vertices.get(v).id);
                System.out.print("head");
                for(Vertex neighbor : graph.vertices.get(v).neighbors){
                    if(!neighbor.removed) {
                        System.out.print(" -> " + neighbor.id);
                    }
                }
            }
            System.out.println("\n");
        }
    }

    static void lowerDegrees(Graph graph, Vertex vertex) {
        for(Vertex neighbor : vertex.neighbors) {
            if(!neighbor.removed){
                int degree = neighbor.getDegree();
                if(degree == 0) {
                    graph.degree0.add(neighbor);
                    graph.degree1.remove(neighbor);
                }
                else if(degree == 1) {
                    graph.degree1.add(neighbor);
                    graph.degree2.remove(neighbor);
                }
                else if(degree == 2) {
                    graph.degree2.add(neighbor);
                    graph.degree3.remove(neighbor);
                }
            }
        }
    }

    static void increaseDegrees(Graph graph, Vertex vertex) {
        for(Vertex neighbor : vertex.neighbors) {
            if(!neighbor.removed) {
                int degree = neighbor.getDegree();
                if(degree == 1) {
                    graph.degree1.add(neighbor);
                    graph.degree0.remove(neighbor);
                }
                else if(degree == 2) {
                    graph.degree2.add(neighbor);
                    graph.degree1.remove(neighbor);
                }
                else if(degree >= 3) {
                    graph.degree3.add(neighbor);
                    graph.degree2.remove(neighbor);
                }
            }
        }
    }

    static Vertex removeVertex(Graph graph, Vertex vertex) {
        if(!vertex.removed) {
            vertex.removed = true;

            switch(vertex.getDegree()) {
                case 0:
                    graph.degree0.remove(vertex);
                    break;
                case 1:
                    graph.degree1.remove(vertex);
                    break;
                case 2:
                    graph.degree2.remove(vertex);
                    break;
                default:
                    graph.degree3.remove(vertex);
            }
            lowerDegrees(graph, vertex);
            return vertex;
        }
        return null;
    }

    static LinkedList<Vertex> removeNeighborhood(Graph graph, Vertex vertex) {
        LinkedList<Vertex> removedVertices = new LinkedList<Vertex>();
        for(Vertex neighbor : vertex.neighbors) {
            Vertex removedVertex = removeVertex(graph, neighbor);
            removedVertices.add(removedVertex);
        }
        removedVertices.add(vertex);
        removeVertex(graph, vertex);
        return removedVertices;
    }

    static void restoreVertex(Graph graph, Vertex vertex) {
        vertex.removed = false;
        switch(vertex.getDegree()){
            case 0:
                graph.degree0.add(vertex);
                break;
            case 1:
                graph.degree1.add(vertex);
                break;
            case 2:
                graph.degree2.add(vertex);
                break;
            default:
                graph.degree3.add(vertex);
        }
        increaseDegrees(graph, vertex);
    }

    static void restoreNeighborhood(Graph graph, LinkedList<Vertex> vertices) {
        for(Vertex vertex : vertices) {
            restoreVertex(graph, vertex);
        }
    }

    static Vertex maxDegreeVertex(Graph graph)
    {
        Vertex maxDegreeVertex = graph.degree3.getFirst();
        for(Vertex vertex : graph.degree3) {
            if(vertex.getDegree() >= maxDegreeVertex.getDegree() && !vertex.removed) {
                maxDegreeVertex = vertex;
            }
        }
        return maxDegreeVertex;
    }

    static int polyAlg(Graph graph) {
        int is = 0;
        while(graph.degree2.size() > 0) {
            Vertex vertex = graph.degree2.getFirst();
            removeNeighborhood(graph, vertex);
            is++;
        }
        return is;
    }

    static int mis3(Graph graph) {
        if(graph.degree0.size() > 0) {
            Vertex v = graph.degree0.getFirst();
            removeVertex(graph, v);
            int misCount = mis3(graph);
            restoreVertex(graph, v);
            return 1 + misCount;
        }
        if(graph.degree1.size() > 0) {
            Vertex v = graph.degree1.getFirst();
            removeNeighborhood(graph, v);
            int misCount = mis3(graph);
            restoreVertex(graph, v);
            return 1 + misCount;
        }
        if(graph.degree3.size() > 0) {
            Vertex v = maxDegreeVertex(graph);

            LinkedList<Vertex> neighbors = removeNeighborhood(graph, v);
            int misCountAlt = 1 + mis3(graph);
            restoreNeighborhood(graph, neighbors);

            removeVertex(graph, v);
            int misCount = mis3(graph);
            restoreVertex(graph, v);

            return Math.max(misCountAlt, misCount);
        }
        if(graph.degree2.size() > 0) {
            return polyAlg(graph);
        }
        return 0;
    }

    public static void main(String[] args) {
        File file = new File("frb30-15-mis/frb30-15-5.mis");
        try {
            Graph testgraph1 = readGraph(file);
            Graph testgraph2 = randomGraph(4, 9999, 100);
            //printGraph(testgraph2);
            System.out.println("MIS: " + mis3(testgraph2));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}