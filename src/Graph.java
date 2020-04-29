import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class Graph {
    int V;
    int stnodes; //Use to count each node in a search tree, i.e. increment for each time we branch.
    int ops; //Consider what operations are characteristic of the algorithm and count them. Degree correction of neighbors is probably it.

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

        while (!uncliqued.isEmpty()) {
            LinkedList<Vertex> clique = new LinkedList<>();
            for (int i = 0; i < cliquesize; i++) {
                if (!uncliqued.isEmpty()) {
                    Vertex vertex = uncliqued.remove(random.nextInt(uncliqued.size()));
                    clique.add(vertex);
                }
            }

            cliqueleaders.add(clique.getFirst());

            for (Vertex v1 : clique) {
                for (Vertex v2 : clique) {
                    if (v1 != v2 && !v1.neighbors.contains(v2) && !v2.neighbors.contains(v1)) {
                        addEdge(graph, v1.id, v2.id);
                    }
                }
            }
        }
        while (!cliqueleaders.isEmpty()) {
            Vertex v1 = cliqueleaders.remove(random.nextInt(cliqueleaders.size()));
            if (!cliqueleaders.isEmpty()) {
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
            graph.ops += 1; //Count significant operation
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
            graph.ops += 1; //Count significant operation
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
        if (graph.degreeUp.size() > 0) {
            maxDegreeVertex = graph.degreeUp.getFirst();
        } else {
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

    static int reduce(Graph graph) {
        int misCount = 0;
        LinkedList<Vertex> reducedVertices = new LinkedList<>();
        while (graph.degree1.size() > 0) {
            Vertex v = graph.degree1.getFirst();
            LinkedList<Vertex> reduced = removeNeighborhood(graph, v);
            misCount++;
            reducedVertices.addAll(reduced);

        }
        while (graph.degree0.size() > 0) {
            Vertex v = graph.degree0.getFirst();
            Vertex reduced = removeVertex(graph, v);
            misCount++;
            reducedVertices.add(reduced);

        }
        misCount += mis3(graph);
        restoreNeighborhood(graph, reducedVertices);
        return misCount;
    }

    static int mis3(Graph graph) {
        if (graph.degree0.size() > 0 || graph.degree1.size() > 0) {
            return reduce(graph);
        }
        if (graph.degree3.size() > 0 || graph.degreeUp.size() > 0) {
            Vertex v = maxDegreeVertex(graph);

            LinkedList<Vertex> neighbors = removeNeighborhood(graph, v);
            graph.stnodes += 1; //Count the branch
            int misCountAlt = 1 + mis3(graph);
            restoreNeighborhood(graph, neighbors);

            removeVertex(graph, v);
            graph.stnodes += 1; //Count the branch
            int misCount = mis3(graph);
            restoreVertex(graph, v);

            return Math.max(misCountAlt, misCount);
        }
        if (graph.degree2.size() > 0) {
            return polyAlg(graph);
        }
        return 0;
    }

    static LinkedList secondNeighbors(Vertex vertex) {
        LinkedList<Vertex> secondNeighbors = new LinkedList<>();
        for (Vertex neighbor : vertex.neighbors) {
            secondNeighbors.addAll(neighbor.neighbors);
        }
        return secondNeighbors;
    }

    static int mis2(Graph graph) {
        if (graph.degree0.size() > 0 || graph.degree1.size() > 0) {
            return reduce(graph);
        }
        if (graph.degree2.size() > 0) {
            Vertex v = graph.degree2.getFirst();
            Vertex u1 = v.neighbors.getFirst();
            Vertex u2 = v.neighbors.getFirst();

            if (u1.neighbors.contains(u2)) {
                removeNeighborhood(graph, v);
                return 1 + mis2(graph);
            } else {
                LinkedList<Vertex> secondNeighbors = secondNeighbors(v);
                if (secondNeighbors.size() == 1) {
                    Vertex w = secondNeighbors.getFirst();

                    LinkedList<Vertex> removed = removeNeighborhood(graph, w);
                    int misCount = 2 + mis2(graph);
                    restoreNeighborhood(graph, removed);

                    removeVertex(graph, w);
                    int misCountAlt = 2 + mis2(graph);
                    restoreVertex(graph, w);
                }
                if (secondNeighbors.size() > 1) {
                    LinkedList<Vertex> removed = removeNeighborhood(graph, v);
                    int misCount = mis2(graph);
                    restoreNeighborhood(graph, removed);

                    //TODO Recursive after removing v and its mirror. Make mirror function
                }
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        Graph testgraph2 = randomGraph(5, 1000, 100);
        Graph testgraph3 = randomClique(4, 24);
        printGraph(testgraph3);
        System.out.println(mis3(testgraph3));
    }
}