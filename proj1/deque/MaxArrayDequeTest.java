package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;


public class MaxArrayDequeTest<T> {

    private static class DequeComparator implements Comparator<Integer> {
        public int compare(Integer a, Integer b) {
            return a.compareTo(b);
        }
    }
    @Test
    public void maxArrayTest() {
        DequeComparator integerDequeComparator = new DequeComparator();
        MaxArrayDeque<Integer> myArrDeque = new MaxArrayDeque<>(integerDequeComparator);
        for (int i = 0; i < 8; i++) {
            myArrDeque.addFirst(i + 1);
        }
        int maxNum = myArrDeque.max(integerDequeComparator);
        assertEquals(8, maxNum);

    }
}
