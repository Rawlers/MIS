import java.util.LinkedList;

public class Vertex {
    LinkedList<Vertex> neighbors;
    int id;
    boolean removed;
    int degree;

    Vertex(int id) {
        this.neighbors = new LinkedList<>();
        this.id = id;
        this.removed = false;
    }

    int getDegree() {
        int degree = 0;
        for(Vertex neighbor : neighbors) {
            if(!neighbor.removed) {
                degree++;
            }
        }
        return degree;
    }

}
