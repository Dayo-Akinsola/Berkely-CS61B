package deque;

public class ArrayDeque<T> implements Deque<T> {

    private T arr[];
    private int size;
    private int nextFirst;
    private int nextLast;
    private int RFACTOR;

    public ArrayDeque() {
        arr = (T []) new Object[8];
        size = 0;
        nextFirst = 4;
        nextLast = 5;
        RFACTOR = 2;
    }


    private void resize(int capacity) {
        T newArr[] = (T []) new Object[capacity];
        int count = 0;
        int index = (((nextFirst + 1) % arr.length) + arr.length) % arr.length;
        while (count < size) {
            newArr[count] = arr[index];
            index = (index + 1) % arr.length;
            count++;
        }
        nextFirst = newArr.length - 1;
        nextLast = size;
        arr = newArr;
    }

    @Override
    public void addFirst(T item) {
        if (size == arr.length) {
            resize(size * RFACTOR);
        }
        arr[nextFirst] = item;
        size = size + 1;
        nextFirst = (((nextFirst - 1) % arr.length) + arr.length) % arr.length;
    }

    @Override
    public void addLast(T item) {
        if (size == arr.length) {
            resize(size * RFACTOR);
        }
        arr[nextLast] = item;
        size = size + 1;
        nextLast = (((nextLast + 1) % arr.length) + arr.length) % arr.length;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int arrLength = arr.length;
        int count = 0;
        int index = (((nextFirst + 1) % arr.length) + arr.length) % arr.length;
        while (count < size) {
            System.out.print(arr[index] + " ");
            index = (index + 1) % arrLength;
            count++;
        }
        System.out.println("");
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if (size <= arr.length * 0.25 && arr.length >= 16) {
            resize(arr.length / RFACTOR);
            System.out.println(arr.length);
        }
        nextFirst = (nextFirst + 1) % arr.length;
        T nextFirstItem = arr[nextFirst];
        arr[nextFirst] = null;
        size--;
        return nextFirstItem;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if (size <= arr.length * 0.25 && arr.length >= 16) {
            resize(arr.length / RFACTOR);
        }
        nextLast = (nextLast - 1) % arr.length;
        T lastItem = arr[nextLast];
        arr[nextLast] = null;
        size--;
        return lastItem;
    }

    @Override
    public T get(int index) {
        int first = (nextFirst + 1) % arr.length;
        int itemIndex = (first + index) % arr.length;
        return arr[itemIndex];
    }
}
