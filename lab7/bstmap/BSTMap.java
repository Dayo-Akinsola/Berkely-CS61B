package bstmap;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private BSTNode root;
    private int size;

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return getNode(root, key) != null;
    }

    @Override
    public V get(K key) {
        final BSTNode node = getNode(root, key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (root == null) {
            root = new BSTNode(key, value);
            size++;
        } else {
            insertNode(root, key, value);
        }
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(BSTNode currentNode) {
        if (currentNode == null) {
            return;
        }

        printInOrder(currentNode.leftChild);
        System.out.println(currentNode.key + " " + currentNode.value);
        printInOrder(currentNode.rightChild);
    }

    private BSTNode insertNode(final BSTNode currentNode, K key, V value) {
        if (currentNode == null) {
            size++;
            return new BSTNode(key, value);
        }
        final int nodeKeyComparison = key.compareTo(currentNode.key);
        if (nodeKeyComparison < 0) {
           currentNode.leftChild = insertNode(currentNode.leftChild, key, value);
        } else if (nodeKeyComparison > 0) {
            currentNode.rightChild = insertNode(currentNode.rightChild, key, value);
        }

        return currentNode;
    }

    private BSTNode getNode(final BSTNode currentNode, K key) {
        if (currentNode == null) {
            return null;
        }

        final int nodeKeyComparison = key.compareTo(currentNode.key);
        if (nodeKeyComparison < 0) {
            return getNode(currentNode.leftChild, key);
        } else if (nodeKeyComparison > 0) {
            return getNode(currentNode.rightChild, key);
        }
        return currentNode;
    }

    private class BSTNode {
        private final K key;
        private V value;
        private BSTNode leftChild;
        private BSTNode rightChild;

        public BSTNode(K key, V value, BSTNode leftChild, BSTNode rightChild) {
            this.key = key;
            this.value = value;
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.leftChild = null;
            this.rightChild = null;
        }

    }
}