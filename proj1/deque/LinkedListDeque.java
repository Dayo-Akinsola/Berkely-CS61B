package deque;

public class LinkedListDeque<ItemType> {

    private class Node {
        private ItemType item;
        private Node next;
        private Node prev;

        public Node(ItemType i, Node p, Node n) {
            item = i;
            next = n;
            prev = p;
        }
    }

    private Node sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel =  new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public LinkedListDeque(ItemType x) {
        sentinel =  new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        addFirst(x);
        size = 1;
    }

    public void addFirst(ItemType x) {
        Node newNode = new Node(x, sentinel, sentinel.next);
        Node oldFirst = sentinel.next;
        oldFirst.prev = newNode;
        sentinel.next = newNode;
        if (sentinel.prev == sentinel) {
            sentinel.prev = newNode;
        }
        size++;
    }

    public void addLast(ItemType x) {
        Node newNode = new Node(x, sentinel.prev, sentinel);
        Node oldLast = sentinel.prev;
        oldLast.next = newNode;
        sentinel.prev = newNode;
        size++;
    }

    public ItemType removeFirst() {
        if (size == 0) {
            return null;
        }
        Node nodeToRemove = sentinel.next;
        ItemType nodeItem = nodeToRemove.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        nodeToRemove.prev = null;
        nodeToRemove.next = null;
        size--;
        return nodeItem;
    }

    public ItemType removeLast() {
        if (size == 0) {
            return null;
        }
        Node nodeToRemove = sentinel.prev;
        ItemType nodeItem = nodeToRemove.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        nodeToRemove.next = null;
        nodeToRemove.prev = null;
        size--;
        return nodeItem;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    public void printDeque() {
        Node currentNode = sentinel;
        for (int i = 0; i < size; i++) {
            currentNode = currentNode.next;
            System.out.print(currentNode.item + " ");
        }
        System.out.println("");
    }

    public ItemType get(int index) {
        if (index >= size) {
            return null;
        }
        Node currentNode = sentinel;
        for (int i = 0; i <= index; i++) {
            currentNode = currentNode.next;
        }
        return currentNode.item;
    }

    private ItemType getRecursive(int index, Node currentNode) {
        if (index < 0) {
            return currentNode.item;
        }
        return getRecursive(index - 1, currentNode.next);
    }

    public ItemType getRecursive(int index) {
        ItemType nodeItem = getRecursive(index, sentinel);
        return nodeItem;
    }
}