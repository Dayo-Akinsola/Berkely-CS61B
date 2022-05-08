package deque;

public class ArrayDeque<ItemType> {

    private ItemType arr[];
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        arr = (ItemType []) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
    }

    public void addFirst(ItemType item) {
        arr[nextFirst] = item;
        size = size + 1;
        nextFirst = (nextFirst - 1) % arr.length;
    }

    public void addLast(ItemType item) {
        arr[nextLast] = item;
        size = size + 1;
        nextLast = (nextLast + 1) % arr.length;
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int arrLength = arr.length;
        int count = 0;
        int index = nextFirst + 1;
        while (count < size) {
            System.out.print(arr[index]);
            index = (index + 1) % arrLength;
            count++;
        }
    }

    public ItemType removeFirst() {
        nextFirst = (nextFirst + 1) % arr.length;
        ItemType nextFirstItem = arr[nextFirst];
        arr[nextFirst] = null;
        size--;
        return nextFirstItem;
    }

    public ItemType removeLast() {
        nextLast = (nextLast - 1) % arr.length;
        ItemType lastItem = arr[nextLast];
        arr[nextLast] = null;
        size--;
        return lastItem;
    }

    public ItemType get(int index) {
        int first = (nextFirst + 1) % arr.length;
        int itemIndex = (first + index) % arr.length;
        return arr[itemIndex];
    }
}
