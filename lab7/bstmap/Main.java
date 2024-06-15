package bstmap;

public class Main {

    public static void main(String[] args) {
        final BSTMap<Integer, String> bstMap = new BSTMap<>();
        bstMap.put(1, "First");
        bstMap.put(14, "Second");
        bstMap.put(4, "Third");
        System.out.println(bstMap.containsKey(13));
        System.out.println(bstMap.containsKey(14));
        System.out.println(bstMap.get(13));
        System.out.println(bstMap.size());
        System.out.println(bstMap.get(14));
        bstMap.printInOrder();
    }
}
