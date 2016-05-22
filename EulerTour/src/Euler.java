import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Euler - Class contains Methods to find the Euler Tour or Euler Path in the
 * Given Graph
 * @author rameshsuthan
 *
 */
public class Euler {

	/**
	 * Method to the get the next unused Edges in the Euler Tour
	 * 
	 * @param vertexWithUnusedEdgeList
	 * @return :vertex with unused Edges
	 */
	public static Vertex getNextVertexWithUnusedEdges(
			LinkedList<Vertex> vertexWithUnusedEdgeList) {
		Vertex v = null;

		if (vertexWithUnusedEdgeList.size() == 0) {
			return v;
		}

		while ((v = vertexWithUnusedEdgeList.pollLast()) != null) {
			if (v.unUsedEdges.size() > 0) {
				return v;
			}
		}
		return v;
	}

	/**
	 * Method to Join the full tour with the current tour before the joinVertex
	 * 
	 * @param fullTour
	 *            - List of edges in Full Tour
	 * @param currentTour
	 *            - List of edges in the current Tour
	 * @param joinVertex
	 *            - current Tour will be joined before the first edge containing
	 *            join Vertex
	 */
	public static void joinTour(DoublyLinkedList<Edge> fullTour,
			DoublyLinkedList<Edge> currentTour, Vertex joinVertex) {
		// For first join the full tour is emtpy we can add as it is
		if (fullTour.size == 0) {
			fullTour.mergeListBefore(fullTour.head.next, currentTour);
			return;
		}
		fullTour.mergeListBefore(joinVertex.index, currentTour);
	}

	/**
	 * Method to find the euler tour in the given graph g and starting at the
	 * vertex sVertex
	 * 
	 * @param g
	 *            - Input Graph(Must be a Euler Graph)
	 * @param sVertex
	 *            - Source Vertex from where the tour starts
	 * @return DoublyLinkedList<Edge>: containing the list of edges in the euler
	 *         tour.
	 */
	public static DoublyLinkedList<Edge> hierholzerAlgorithm(Graph g,
			Vertex sVertex) {
		Vertex currVertex = sVertex;

		DoublyLinkedList<Edge> currentTour = new DoublyLinkedList<Edge>();
		DoublyLinkedList<Edge> fullTour = new DoublyLinkedList<Edge>();

		LinkedList<Vertex> vertexWithUnusedEdgeList = new LinkedList<Vertex>();
		Vertex nextVertex = null;

		while (currVertex != null) {

			Edge edge = currVertex.unUsedEdges.remove(0);

			DoublyLinkedList<Edge>.Entry<Edge> indexInTour = currentTour
					.addAndGetIndex(edge);
			if (currVertex.unUsedEdges.size() > 0 && currVertex.index == null) {
				currVertex.index = indexInTour;
				vertexWithUnusedEdgeList.add(currVertex);
			}

			nextVertex = edge.otherEnd(currVertex);
			nextVertex.unUsedEdges.remove(edge);
			currVertex = nextVertex;

			if (nextVertex == sVertex) {
				// System.out.println("Found circuit Starting at:"+sVertex);
				joinTour(fullTour, currentTour, sVertex);
				sVertex = getNextVertexWithUnusedEdges(vertexWithUnusedEdgeList);
				currVertex = sVertex;

				// fullTour.addAll(currentTour);

			}
		}

		return fullTour;

	}

	/**
	 * Method to find Euler tour or Euler path in the given graph if it is
	 * Eulerian Graph
	 * 
	 * @param g
	 *            - input graph
	 * @return DoublyLinkedList<Edge>: List consisting of edges in Euler Tour or
	 *         Euler Path
	 */
	public static DoublyLinkedList<Edge> findEulerTour(Graph g) {
		// check the connectedness and eulerian or not.
		DoublyLinkedList<Edge> eulerTour = null;

		Vertex[] eulerPathVertices = new Vertex[2];
		long startTime = System.currentTimeMillis();
		int eulerType;
		
		if(g.numNodes==0||g.numNodes==1||g.numEdges==0){
			//System.out.println("Null graph");
			return null;
		}
		
		if (!isConnected(g)
				|| (eulerType = isEuler(g, eulerPathVertices)) == -1) {
			System.out.println("Graph is not Eulerian");
			return null;
		}
		long endTime = System.currentTimeMillis();
		// System.out.println("Time to find the connectedness of the graph and Eulerian graph:"+
		// (endTime - startTime) + " ms");

		startTime = System.currentTimeMillis();
		if (eulerType == 0) {
			eulerTour = hierholzerAlgorithm(g, g.verts.get(1));
		} else {
			// There is a Euler path
			int startVertex, endVertex;

			// following condition is added to make sure that we start with
			// smaller numbered node of odd degree
			if (eulerPathVertices[0].name < eulerPathVertices[1].name) {
				startVertex = eulerPathVertices[0].name;
				endVertex = eulerPathVertices[1].name;
			} else {
				startVertex = eulerPathVertices[1].name;
				endVertex = eulerPathVertices[0].name;
			}
			// Adding a fake edge between the Odd Degree Vertices to convert to
			// create a Euler ciruit in the graph
			// System.out.println("Adding edge for euler path:" + startVertex
			// "<->" + endVertex);
			Edge fakeEdge = g.addEdge(startVertex, endVertex, 1);
			Vertex sVertex = g.verts.get(startVertex);
			Vertex eVertex = g.verts.get(endVertex);
			// Call the hierholzerAlgorithm after adding the fake edge.
			eulerTour = hierholzerAlgorithm(g, sVertex);

			// After getting the euler Tour, create a euler path by removing the
			// fake edge
			// and Folding the paths of the ciruits
			eulerTour = getEulerPath(eulerTour, fakeEdge,
					g.verts.get(startVertex));

			// Remove the fake edge from the graph
			sVertex.Adj.remove(fakeEdge);
			eVertex.Adj.remove(fakeEdge);

		}
		endTime = System.currentTimeMillis();
		// System.out.println("Total Time to find the Euler Tour alone:"+
		// (endTime - startTime) + " ms");
		// System.out.println(eulerTour.tail);
		return eulerTour;
	}

	public static DoublyLinkedList<Edge> getEulerPath(
			DoublyLinkedList<Edge> eulerTour, Edge fakeEdge, Vertex startVertex) {

		// Split the eulerTour into list1 -> fakeEdge -> list2
		DoublyLinkedList<Edge> list1 = new DoublyLinkedList<Edge>();
		DoublyLinkedList<Edge> list2 = new DoublyLinkedList<Edge>();
		DoublyLinkedList<Edge>.Entry<Edge> edgeNode;

		// System.out.println("Actual Euler Tour" + eulerTour.size);
		// eulerTour.printList();
		edgeNode = eulerTour.head.next;

		// List 1 - add all the edges until the fake edge
		while (edgeNode != null) {
			if (edgeNode.element == fakeEdge) {
				break;
			}
			list1.add(edgeNode.element);
			edgeNode = edgeNode.next;
		}

		edgeNode = edgeNode.next;

		// Case 1: (A and B odd degree vertex)
		// List 1 starts with A and ends with A
		// List 2 starts with B and ends with A
		// List1(A1->....->A1)->fakeEdge(A1,B1)-> List2(B2->....->A2)
		// List1 + Reversed (List2)
		// A1->....->A1 + A2->........->B2
		if (edgeNode != null && !edgeNode.element.isEdgeContains(startVertex)) {
			// add all the remaining edge in the reverse order from the tail
			// until the fake edge
			edgeNode = eulerTour.tail;
			while (edgeNode != null) {
				if (edgeNode.element == fakeEdge) {
					break;
				}
				list2.add(edgeNode.element);
				edgeNode = edgeNode.prev;
			}
			// Merge List 1 with reveresed List 2
			list2.mergeListBefore(list2.head.next, list1);
			return list2;
		}

		// Case 2: (A and B odd degree vertex)
		// List 1 starts with A and ends with B
		// List 2 starts with A and ends with A
		// List1(A1->....->B1)->fakeEdge(A,B)-> List2(A2->....->A2)
		// List2 + List11
		// A2->....->A2 + A1->........->B1
		if (edgeNode != null && edgeNode.element.isEdgeContains(startVertex)) {
			while (edgeNode != null) {
				if (edgeNode.element == fakeEdge) {
					break;
				}
				list2.add(edgeNode.element);
				edgeNode = edgeNode.next;
			}
			// Merge List 2 before List1
			list1.mergeListBefore(list1.head.next, list2);
			return list1;

		}

		return list1;
	}

	/**
	 * Method to determine whether the given tour is Euler Tour given a graph
	 * and start vertex for the traversal
	 * 
	 * @param g
	 *            - input graph
	 * @param tour
	 *            - tour
	 * @param start
	 *            - startVertex for the Euler Tour
	 * @return true - if it is a euler tour else returns false
	 */
	static boolean verifyTour(Graph g, DoublyLinkedList<Edge> tour, Vertex start) {
		DoublyLinkedList<Edge>.Entry<Edge> node = tour.head.next;
		int numOfEdgesTraversed = 0;
		Vertex unMatchedVertex = null;
		while (node != null) {
			Edge e = node.element;

			if (e.traversed) {
				// System.out.println("Edge already visited" + e);
				return false;
			}

			// for first edge
			if (numOfEdgesTraversed == 0) {
				unMatchedVertex = e.otherEnd(start);
			}// for rest of the edges
			else {
				// the current edge should contain one vertex matching with
				// previous edge
				// and other vertex matching with the next edge
				// unMatchedVertex stores the Vertex which is not matched witht
				// the previous edge
				if (node.element.From == unMatchedVertex) {
					unMatchedVertex = node.element.To;
				} else if (node.element.To == unMatchedVertex) {
					unMatchedVertex = node.element.From;
				} else {
					// System.out.println("Edges not adjacent");
					return false;
				}
			}
			e.traversed = true;
			node = node.next;
			numOfEdgesTraversed++;
		}

		// Traverse all the edge in the graph to verify whether it has been
		// visited or not
		Iterator<Vertex> it = g.iterator();
		// System.out.println(g.verts.size());
		while (it.hasNext()) {
			Vertex v = it.next();
			// System.out.println(v);
			for (Edge e : v.Adj) {
				if (!e.traversed) {
					// System.out.println("Edge not Traversed:" + e);
					return false;
				}
			}
		}

		/*
		 * if (start != unMatchedVertex) {
		 * System.out.println("It is a Euler Path"); }
		 */

		// System.out.println("Number of Edges Traversed for Verify Tour:"+
		// numOfEdgesTraversed);
		return true;
	}

	/**
	 * Method to determine whether given graph is Euler graph or not
	 * 
	 * @param g
	 *            - input graph
	 * @param eulerPathVertices
	 *            - Array of vertices to store the odd degree vertex for the
	 *            Euler Path
	 * @return :0 if it has a Euler Tour, 1 if it has e Euler Path else return
	 *         -1
	 *
	 */
	public static int isEuler(Graph g, Vertex[] eulerPathVertices) {
		Iterator<Vertex> it = g.iterator();
		int noOfOddEdge = 0;
		while (it.hasNext()) {
			Vertex u = it.next();
			if (u.Adj.size() % 2 != 0) {
				noOfOddEdge++;
				// if number of odd Edge is greater than 2. it is not eulerian
				// graph. we can break the loop
				if (noOfOddEdge > 2) {
					break;
				}
				// add the odd vertex to the eulerPathVertices
				eulerPathVertices[noOfOddEdge - 1] = u;
			}
		}

		if (noOfOddEdge == 0) {
			// System.out.println("Graph has a Euler Tour");
			return 0;
		} else if (noOfOddEdge == 2) {
			// System.out.println("Graph has a Euler Path");
			return 1;
		} else {
			// System.out.println("Graph doen't have a Euler Tour or Euler Path");
			return -1;
		}
	}

	/**
	 * Method to determine to find whether the given graph is connected or not
	 * 
	 * @param g
	 *            - input graph
	 * @return :true if the graph is connected,else return false
	 */
	public static boolean isConnected(Graph g) {
		// Do BFS - if the graph is connected all the vertex in the should have
		// seen flag set to true
		BFS(g, g.verts.get(1));
		Iterator<Vertex> it = g.iterator();
		while (it.hasNext()) {
			Vertex u = it.next();
			if (u.seen == false) {
				// atleast one of the node in the graph has seen flag set to
				// false
				// graph is disconnected.
				// System.out.println("Graph is disconnected");
				return false;
			}
		}
		// System.out.println("Graph is connected");
		return true;
	}

	/**
	 * Method to perform BFS search on the graph and sets the seen flag in the
	 * vertex to true
	 * 
	 * @param g
	 *            - input graph
	 * @param src
	 *            - startVertex
	 */
	public static void BFS(Graph g, Vertex src) {

		Queue<Vertex> queue = new LinkedList<Vertex>();
		queue.add(src);
		src.seen = true;

		while (!queue.isEmpty()) {
			Vertex u = queue.remove();
			// System.out.print(u.name+" ");
			for (Edge e : u.Adj) {
				Vertex v = e.otherEnd(u);
				if (!v.seen) {
					v.seen = true;
					v.parent = u;
					queue.add(v);
				}
			}
		}

	}

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in = null;

		if (args.length > 0) {
			File inputFile = new File(args[0]);
			in = new Scanner(inputFile);
		} else {
			in = new Scanner(System.in);
		}

		// long startTime = System.currentTimeMillis();
		Graph g = Graph.readGraph(in, false);
		// long endTime = System.currentTimeMillis();
		// System.out.println("Time to read the graph: " + (endTime -
		// startTime)+ " ms");
		

		// startTime = System.currentTimeMillis();
		DoublyLinkedList<Edge> eulerTour = findEulerTour(g);
		// endTime = System.currentTimeMillis();
		if (eulerTour != null) {
			//print the Euler Tour
			eulerTour.printList();
		}
		
		// System.out.println("Total Time :" + (endTime - startTime) + " ms");

		/*
		 * if (eulerTour != null) { System.out.println("Size of the Tour:" +
		 * eulerTour.size); //eulerTour.printList();
		 * //System.out.println("Total Time :" + (endTime - startTime) + " ms");
		 * 
		 * startTime = System.currentTimeMillis();
		 * System.out.println("isEulerTour:"+ verifyTour(g, eulerTour,
		 * g.verts.get(1))); endTime = System.currentTimeMillis();
		 * System.out.println("Total Time to Verify Euler Tour :"+ (endTime -
		 * startTime) + " ms"); }
		 */

	}
	
	/*
	 * Sample Input:
	 * 6 10
		1 2 1
		1 3 1
		1 4 1
		1 6 1
		2 3 1
		3 6 1
		3 4 1
		4 5 1
		4 6 1
		5 6 1
		
		Sample Output:
		(1,2)
		(2,3)
		(3,6)
		(1,6)
		(1,4)
		(4,5)
		(5,6)
		(4,6)
		(3,4)
		(1,3)
	 */

}
