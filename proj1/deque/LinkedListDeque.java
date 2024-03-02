package deque;

public class LinkedListDeque<T> implements Deque<T> {

    private class Node {
        private T item;
        private Node next;
        private Node prev;

        public Node(T i, Node p, Node n) {
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

    public LinkedListDeque(T x) {
        sentinel =  new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        addFirst(x);
        size = 1;
    }

    @Override
    public void addFirst(T x) {
        Node newNode = new Node(x, sentinel, sentinel.next);
        Node oldFirst = sentinel.next;
        oldFirst.prev = newNode;
        sentinel.next = newNode;
        if (sentinel.prev == sentinel) {
            sentinel.prev = newNode;
        }
        size++;
    }

    @Override
    public void addLast(T x) {
        Node newNode = new Node(x, sentinel.prev, sentinel);
        Node oldLast = sentinel.prev;
        oldLast.next = newNode;
        sentinel.prev = newNode;
        size++;
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        Node nodeToRemove = sentinel.next;
        T nodeItem = nodeToRemove.item;
        sentinel.next.next.prev = sentinel;
        sentinel.next = sentinel.next.next;
        nodeToRemove.prev = null;
        nodeToRemove.next = null;
        size--;
        return nodeItem;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        Node nodeToRemove = sentinel.prev;
        T nodeItem = nodeToRemove.item;
        sentinel.prev.prev.next = sentinel;
        sentinel.prev = sentinel.prev.prev;
        nodeToRemove.next = null;
        nodeToRemove.prev = null;
        size--;
        return nodeItem;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node currentNode = sentinel;
        for (int i = 0; i < size; i++) {
            currentNode = currentNode.next;
            System.out.print(currentNode.item + " ");
        }
        System.out.println("");
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        Node currentNode = sentinel;
        for (int i = 0; i <= index; i++) {
            currentNode = currentNode.next;
        }
        return currentNode.item;
    }

    private T getRecursive(int index, Node currentNode) {
        if (index < 0) {
            return currentNode.item;
        }
        return getRecursive(index - 1, currentNode.next);
    }

    public T getRecursive(int index) {
        T nodeItem = getRecursive(index, sentinel);
        return nodeItem;
    }
}