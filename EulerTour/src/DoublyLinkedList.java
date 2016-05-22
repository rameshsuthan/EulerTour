

/**
 * DoublyLinkedList(DLL) - Inplementation of a doubly linked list
 * @author rameshsuthan
 *
 * @param <T>
 */
public class DoublyLinkedList<T> {
	
	/**
	 * Entry - Implementation of the node in the linked list
	 * @author rameshsuthan
	 *
	 * @param <T>
	 */
	public class Entry<T>{
		T element;//data
		Entry<T> next;//next pointer
		Entry<T> prev;//previous pointer
		public Entry(T element,Entry<T> prev,Entry<T> next){
			this.element=element;
			this.next=next;
			this.prev=prev;
		}
	}
	Entry<T> head,tail;//head and tail for the doubly linked list
	int size;//size
	
	public DoublyLinkedList() {
		head=new Entry<>(null, null, null);
		tail=null;
		size=0;
	}
	
	/**
	 * Method to add a node in the doubly linked list
	 * @param x :data to be added to the linked list 
	 */
	public void add(T x){
		if(tail==null){
			head.next=new Entry<>(x, null,head.next);
			tail=head.next;
		}else{
			tail.next=new Entry<>(x, tail, null);
			tail=tail.next;
			head.next.prev=tail;
		}
		size++;
	}
	
	/**
	 * Method to add a node and get the reference in the linked list
	 * @param x
	 * @return
	 */
	public DoublyLinkedList<T>.Entry<T> addAndGetIndex(T x){
		add(x);
		return tail;
	}
	
	/**
	 * Method to add all the element from the secList to the current list
	 * @param secList - second Doubly Linked List
	 */
	public void addAll(DoublyLinkedList<T> secList){
		Entry<T> x=secList.head.next;
		while(x!=null){
			add(x.element);
			x=x.next;
		}
		size+=secList.size;			
	}
	
	/**
	 * Method to merge the second input list into the current list before the given index position
	 * @param index : index postion(reference) 
	 * @param secList :second DLL
	 */
	public void mergeListBefore(DoublyLinkedList<T>.Entry<T> index,DoublyLinkedList<T> secList){
		
		DoublyLinkedList<T>.Entry<T> secHead=secList.head;
		DoublyLinkedList<T>.Entry<T> secTail= secList.tail;
		
		//if the current list is empty just update the head and tail
		if(head.next==null){
			head.next=secHead.next;
			tail=secTail;
		}
		//if need to insert at the first position no need to update the tail
		else if(index==head.next){
			secHead.next.prev=tail;
			secTail.next=head.next;
			head.next=secHead.next;	
		}
		else{
			DoublyLinkedList<T>.Entry<T> prevNode = index.prev;
			index.prev=secTail;
			secHead.next.prev=prevNode;			
			secTail.next=index;
			prevNode.next=secHead.next;
		}
		
		size+=secList.size;
		//clear the second list
		secList.clear();
		
		
	}
	
	/**
	 * Method to clear the linked list 
	 */
	public void clear(){
		head.next=null;
		tail=null;
		size=0;
	}
	
	

	/**
	 * Method to print the element in the linked list 
	 */
	public void printList(){
		Entry<T> x=head.next;
		while(x!=null){
			//System.out.println( "["+x.prev+ "<-"+ x.element+"->"+x.next+"] ");
			System.out.println(x.element);
			x=x.next;
		}
		System.out.println();
	}
	
	
	public static void main(String[] args) {
		
		DoublyLinkedList<Integer> list= new DoublyLinkedList<Integer>();
		DoublyLinkedList<Integer> list2= new DoublyLinkedList<Integer>();
		DoublyLinkedList<Integer> list3= new DoublyLinkedList<Integer>();
		DoublyLinkedList<Integer>.Entry<Integer> index,index2;
		index= list.addAndGetIndex(1);
		
		index2=list.addAndGetIndex(2);
		System.out.println("List1");
		list.add(3);
		
		
		list.printList();
		list2.add(4);
		list2.add(5);
		list2.add(6);
		
		System.out.println("List2");
		list2.printList();
		
		list3.add(7);
		list3.add(8);
		list3.add(9);
		System.out.println("List3");
		list3.printList();
		
		//list2.addAll(list3);
		//list2.printList();
		//list3.printList();
		
		list.mergeListBefore(index2,list2);
		list.printList();
		
		list.mergeListBefore(index,list3);
		list.printList();
		System.out.println(list.tail.element);
				
		
		
		
	}

}
