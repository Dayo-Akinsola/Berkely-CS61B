package deque;
import org.junit.Test;

import java.lang.reflect.Array;

import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void addFirstTest() {
        ArrayDeque<Integer> arrD = new ArrayDeque();
        assertEquals(arrD.isEmpty(), true);
        arrD.addFirst(4);
        arrD.addFirst(5);
        arrD.addFirst(10);
        arrD.addFirst(22);

        assertEquals(4, arrD.size());

        int value = arrD.get(2);
        assertEquals(5, value);
        arrD.printDeque();
    }

    @Test
    public void addLastTest() {
        ArrayDeque<Integer> arrD = new ArrayDeque();
        arrD.addLast(10);
        arrD.addLast(22);
        arrD.addLast(77);
        arrD.addLast(3);

        assertEquals(4, arrD.size());

        int value = arrD.get(1);
        assertEquals(22, value);
        arrD.printDeque();
    }

    @Test
    public void addMixTest() {
        ArrayDeque<Integer> arrD = new ArrayDeque();
        arrD.addFirst(44);
        arrD.addLast(5);
        arrD.addFirst(2);
        arrD.addFirst(30);

        int value = arrD.get(3);
        assertEquals(5, value);
        arrD.printDeque();
    }

    @Test
    public void removeFirstTest() {
        ArrayDeque<Integer> arrD = new ArrayDeque();

        arrD.addFirst(20);
        arrD.addFirst(10);
        arrD.addLast(3);
        arrD.addLast(8);

        arrD.removeFirst();
        arrD.removeFirst();
        int value = arrD.get(0);
        assertEquals(2, arrD.size());
        assertEquals(3, value);

        arrD.addFirst(30);
        int valueTwo = arrD.get(0);
        assertEquals(30, valueTwo);

        arrD.printDeque();
    }

    @Test
    public void removeLastTest(){
        ArrayDeque<Integer> arrD = new ArrayDeque();

        arrD.addFirst(20);
        arrD.addFirst(10);
        arrD.addLast(3);
        arrD.addLast(8);

        arrD.removeLast();
        arrD.removeLast();

        assertEquals(2, arrD.size());
        int value = arrD.get(1);
        assertEquals(20, value);

        arrD.addLast(40);
        arrD.addLast(22);
        int valueTwo = arrD.get(3);
        assertEquals(22, valueTwo);
        arrD.printDeque();
    }

    @Test
    public void addFirstResizeTest() {
        ArrayDeque<Integer> arrD = new ArrayDeque();

        for (int i = 0; i < 20; i++) {
            arrD.addFirst(i);
        }

        assertEquals(20, arrD.size());
        int firstValue = arrD.get(0);
        assertEquals(19, firstValue);
        arrD.printDeque();
    }

    @Test
    public void addLastResizeTest() {
        ArrayDeque<Integer> arrD = new ArrayDeque();

        for (int i = 0; i < 20; i++) {
            arrD.addLast(i);
        }
        assertEquals(20, arrD.size());
        int firstValue = arrD.get(0);
        int lastValue = arrD.get(19);
        assertEquals(0, firstValue);
        assertEquals(19, lastValue);
        arrD.printDeque();
    }

    @Test
    public void addFirstLastResizeTest() {
        ArrayDeque<Integer> arrD = new ArrayDeque();

        for (int i = 0; i < 20; i++) {
            arrD.addLast(i);
        }

        for (int i = 20; i < 40; i++) {
            arrD.addFirst(i);
        }

        assertEquals(40, arrD.size());
        int value = arrD.get(2);
        assertEquals(37, value);

        arrD.printDeque();
    }

    @Test
    public void removeFirstResizeTest() {
        ArrayDeque<Integer> arrD = new ArrayDeque();

        for (int i = 0; i < 100; i++) {
            arrD.addFirst(i);
        }

        for (int i = 0; i < 80; i++) {
            arrD.removeFirst();
        }

        assertEquals(20, arrD.size());
        arrD.printDeque();
    }
}

