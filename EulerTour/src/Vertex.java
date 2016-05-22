
import java.util.*;

public class Vertex {
	public int name; // name of the vertex
	public boolean seen; // flag to check if the vertex has already been visited
	public Vertex parent; // parent of the vertex
	public int distance; // distance to the vertex from the source vertex
	public List<Edge> Adj, revAdj; // adjacency list; use
												// LinkedList or ArrayList
	// index of First edge containing this vertex in the tour.
	// to achieve O(1) Run Time for Merging the tours
	public DoublyLinkedList<Edge>.Entry<Edge> index;
	public List<Edge>  unUsedEdges; // storing the unused edges during euler tour

	/**
	 * Constructor for the vertex
	 * 
	 * @param n
	 *            : int - name of the vertex
	 */
	Vertex(int n) {
		name = n;
		seen = false;
		parent = null;
		Adj = new ArrayList<Edge>();
		revAdj = new ArrayList<Edge>(); /* only for directed graphs */
		unUsedEdges = new ArrayList<Edge>();
	}

	/**
	 * Method to represent a vertex by its name
	 */
	public String toString() {
		return Integer.toString(name);
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Vertex) {
			Vertex otherVertex = (Vertex) obj;
			if (this.name == otherVertex.name) {
				return true;
			}
		}
		return false;
	}
}
