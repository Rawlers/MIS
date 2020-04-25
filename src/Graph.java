import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Graph {
    int V;

    LinkedList<Vertex> vertices;
    LinkedList<Vertex> degree0;
    LinkedList<Vertex> degree1;
    LinkedList<Vertex> degree2;
    LinkedList<Vertex> degree3;
    LinkedList<Vertex> degreeUp;
    LinkedList[] degreearray;

    Graph(int V) {
        this.V = V;

        vertices = new LinkedList<>();

        // degree sorting
        degree0 = new LinkedList<>();
        degree1 = new LinkedList<>();
        degree2 = new LinkedList<>();
        degree3 = new LinkedList<>();

        // degree 4 or larger
        degreeUp = new LinkedList<>();

        degreearray = new LinkedList[]{degree0, degree1, degree2, degree3, degreeUp};


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
        for (Vertex vertex : graph.vertices) {
                if (vertex.degree == 0) {
                    graph.degree0.add(vertex);
                } else if (vertex.degree == 1) {
                    graph.degree1.add(vertex);
                } else if (vertex.degree == 2) {
                    graph.degree2.add(vertex);
                } else if (vertex.degree == 3) {
                    graph.degree3.add(vertex);
                } else {
                    graph.degreeUp.add(vertex);
                }
            Collections.sort(graph.degreeUp, Comparator.comparingInt((Vertex v) -> v.degree).reversed());
        }
    }

    static Graph readGraph(File file) throws FileNotFoundException {
        Scanner filesc = new Scanner(file);
        Scanner firstsc = new Scanner(filesc.nextLine());

        firstsc.skip("p edge");
        int vertices = firstsc.nextInt();
        Graph graph = new Graph(vertices);

        while (filesc.hasNextLine()) {
            Scanner linesc = new Scanner(filesc.nextLine());
            linesc.next("e");
            int srcEdge = linesc.nextInt() - 1; //benchmark files are 1 indexed.
            int destEdge = linesc.nextInt() - 1;

            addEdge(graph, srcEdge, destEdge);
        }
        for (Vertex vertex : graph.vertices) {
            vertex.degree = vertex.getDegree();
        }
        sortByDegree(graph);
        return graph;
    }

    static Graph randomGraph(int maxdegree, int maxedgecount, int size) {
        Graph graph = new Graph(size);
        Random random = new Random();
        int edgecount = random.nextInt(maxedgecount);
        for (int i = 0; i <= edgecount; i++) {
            Vertex v1 = graph.vertices.get(random.nextInt(size - 1));
            Vertex v2 = graph.vertices.get(random.nextInt(size - 1));
            if (!v1.neighbors.contains(v2)) {
                if (v1.getDegree() < maxdegree && v2.getDegree() < maxdegree) {
                    if (v1 != v2) {
                        addEdge(graph, v1.id, v2.id);
                    }
                }
            }
        }
        for (Vertex vertex : graph.vertices) {
            vertex.degree = vertex.getDegree();
        }
        sortByDegree(graph);
        return graph;
    }

    static Graph randomClique(int cliquesize, int cliquecount) {
        Graph graph = new Graph(cliquesize * cliquecount);
        Random random = new Random();
        LinkedList<Vertex> uncliqued = new LinkedList<>();
        LinkedList<Vertex> cliqueleaders = new LinkedList<>();

        uncliqued.addAll(graph.vertices);

        while(!uncliqued.isEmpty()) {
            LinkedList<Vertex> clique = new LinkedList<>();
            for (int i = 0; i < cliquesize; i++) {
                if(!uncliqued.isEmpty()) {
                    Vertex vertex = uncliqued.remove(random.nextInt(uncliqued.size()));
                    clique.add(vertex);
                }
            }

            cliqueleaders.add(clique.getFirst());

            for(Vertex v1 : clique) {
                for(Vertex v2: clique) {
                    if(v1 != v2 && !v1.neighbors.contains(v2) && !v2.neighbors.contains(v1)) {
                        addEdge(graph, v1.id, v2.id);
                    }
                }
            }
        }
        while(!cliqueleaders.isEmpty()) {
            Vertex v1 = cliqueleaders.remove(random.nextInt(cliqueleaders.size()));
            if(!cliqueleaders.isEmpty()) {
                Vertex v2 = cliqueleaders.get(random.nextInt(cliqueleaders.size()));
                addEdge(graph, v1.id, v2.id);
            }
        }
        for (Vertex vertex : graph.vertices) {
            vertex.degree = vertex.getDegree();
        }
        sortByDegree(graph);
        return graph;
    }

    static void printGraph(Graph graph) {
        for (int v = 0; v < graph.V; v++) {
            if (graph.vertices.get(v).removed) {
                System.out.println("Vertex " + v + " removed");
            } else {
                System.out.println("Adjacency list of vertex " + graph.vertices.get(v).id);
                System.out.print("head");
                for (Vertex neighbor : graph.vertices.get(v).neighbors) {
                    if (!neighbor.removed) {
                        System.out.print(" -> " + neighbor.id);
                    }
                }
            }
            System.out.println("\n");
        }
    }

    static Vertex removeVertex(Graph graph, Vertex vertex) {
        vertex.removed = true;
        if (vertex.degree <= 3) {
            graph.degreearray[vertex.degree].remove(vertex);
        } else {
            graph.degreeUp.remove(vertex);
        }

        for (Vertex neighbor : vertex.neighbors) {
            if (!neighbor.removed) {
                neighbor.degree = neighbor.degree - 1;
                if (neighbor.degree <= 3) {
                    graph.degreearray[neighbor.degree + 1].remove(neighbor);
                    graph.degreearray[neighbor.degree].add(neighbor);
                }
            }
        }
        return vertex;
    }

    static void restoreVertex(Graph graph, Vertex vertex) {
        vertex.removed = false;
        vertex.degree = vertex.getDegree();
        if (vertex.degree <= 3) {
            graph.degreearray[vertex.degree].add(vertex);
        } else {
            graph.degreeUp.add(vertex);
        }
        for (Vertex neighbor : vertex.neighbors) {
            if (!neighbor.removed) {
                neighbor.degree = neighbor.degree + 1;
                if (neighbor.degree <= 4) {
                    graph.degreearray[neighbor.degree - 1].remove(neighbor);
                    graph.degreearray[neighbor.degree].add(neighbor);
                }
            }
        }
    }

    static LinkedList<Vertex> removeNeighborhood(Graph graph, Vertex vertex) {
        LinkedList<Vertex> removedVertices = new LinkedList<>();
        vertex.removed = true;
        removedVertices.add(vertex);
        for (Vertex neighbor : vertex.neighbors) {
            if (!neighbor.removed) {
                neighbor.removed = true;
                removedVertices.add(neighbor);
            }
        }
        for (Vertex removed : removedVertices) {
            removeVertex(graph, removed);
        }
        return removedVertices;
    }

    static void restoreNeighborhood(Graph graph, LinkedList<Vertex> vertices) {
        for (Vertex vertex : vertices) {
            restoreVertex(graph, vertex);
        }
    }

    static Vertex maxDegreeVertex(Graph graph) {
        Vertex maxDegreeVertex;
        if(graph.degreeUp.size() > 0) {
            maxDegreeVertex = graph.degreeUp.getFirst();
        }
        else{
            maxDegreeVertex = graph.degree3.getFirst();
        }
        return maxDegreeVertex;
    }

    static int polyAlg(Graph graph) {
        int is = 0;
        LinkedList<Vertex> neighbors = new LinkedList<>();
        while (graph.degree2.size() > 0) {
            Vertex vertex = graph.degree2.getFirst();
            neighbors.addAll(removeNeighborhood(graph, vertex));
            is++;
        }
        restoreNeighborhood(graph, neighbors);
        return is;
    }

    static int mis3(Graph graph) {
        if(graph.degree0.size() > 0 || graph.degree1.size() > 0) {
            int misCount = 0;
            LinkedList<Vertex> reducedVertices = new LinkedList<>();
            while(graph.degree1.size() > 0) {
                Vertex v = graph.degree1.getFirst();
                LinkedList<Vertex> reduced = removeNeighborhood(graph, v);
                misCount++;
                reducedVertices.addAll(reduced);

            }
            while(graph.degree0.size() > 0) {
                Vertex v = graph.degree0.getFirst();
                Vertex reduced = removeVertex(graph, v);
                misCount++;
                reducedVertices.add(reduced);

            }
            misCount += mis3(graph);
            restoreNeighborhood(graph, reducedVertices);
            return misCount;
        }
        if (graph.degree3.size() > 0 || graph.degreeUp.size() > 0) {
            Vertex v = maxDegreeVertex(graph);

            LinkedList<Vertex> neighbors = removeNeighborhood(graph, v);
            int misCountAlt = 1 + mis3(graph);
            restoreNeighborhood(graph, neighbors);

            removeVertex(graph, v);
            int misCount = mis3(graph);
            restoreVertex(graph, v);

            return Math.max(misCountAlt, misCount);
        }
        if (graph.degree2.size() > 0) {
            return polyAlg(graph);
        }
        return 0;
    }

    public static void main(String[] args) {
        File file = new File("frb30-15-mis/frb30-15-1.mis");
        try {
            Graph testgraph1 = readGraph(file);
            Graph testgraph2 = randomGraph(4, 110*4, 110);
            Graph testgraph3 = randomClique(4, 24);
            printGraph(testgraph3);
            System.out.println("MIS: " + mis3(testgraph3));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}