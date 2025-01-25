import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//Class my entry
class MyEntry {
    private Integer key;
    private String value;

    public MyEntry(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + " " + value;
    }
}

//Class Node
class Node{
    private MyEntry entry;
    private Node[] next;

    public Node(int level, MyEntry entry) {
        this.entry = entry;
        this.next = new Node[level + 1];
    }

    public MyEntry getEntry() {
        return entry;
    }

    public Node getNext(int level) {
        return next[level];
    }

    public void setNext(int level, Node node) {
        next[level] = node;
    }

    public Node[] getAllNext() {
        return next;
    }

    public void setAllNext(Node[] newNext) {
        this.next = newNext;
    }

    public int getLevelCount() {
        return next.length;
    }
}

//Class SkipListPQ
class SkipListPQ {

    private double alpha;
    private Random rand;
    private Node head;
    private int maxLevel;

    public SkipListPQ(double alpha) {
        this.alpha = alpha;
        this.rand = new Random();
        this.maxLevel = 0;

	    this.head = new Node(0, new MyEntry(Integer.MIN_VALUE, "head"));    
        head.setNext(0, new Node(0, new MyEntry(Integer.MAX_VALUE, "tail")));
    }

    public int size() {
	    int size = 0;
        Node tmp = head.getNext(0);

        while(tmp != null && tmp.getEntry().getKey() != Integer.MAX_VALUE){
            size++;
            tmp = tmp.getNext(0);
        }

        return size;
    }

    public MyEntry min() {
	    if(size() == 0) return null;

        MyEntry minEntry = head.getNext(0).getEntry();
        System.out.println(minEntry.getKey() + " " + minEntry.getValue());

        return minEntry;
    }

    public int insert(int key, String value) {
    int newLevel = generateEll(alpha, key);
    Node newNode = new Node(newLevel, new MyEntry(key, value));
    Node tmp = head;

    if (newLevel > maxLevel) {
        maxLevel = newLevel;

        Node[] newNext = new Node[maxLevel + 1];
        System.arraycopy(head.getAllNext(), 0, newNext, 0, head.getAllNext().length);
        head.setAllNext(newNext);
    }

    for (int i = maxLevel; i >= 0; i--) {
        while (tmp.getNext(i) != null && tmp.getNext(i).getEntry().getKey() < key) {
            tmp = tmp.getNext(i);
        }

        if (i <= newLevel) {
            newNode.setNext(i, tmp.getNext(i));
            tmp.setNext(i, newNode);
        }
    }

    return newLevel;
}


    private int generateEll(double alpha_ , int key) {
        int level = 0;
        if (alpha_ >= 0. && alpha_< 1) {
          while (rand.nextDouble() < alpha_) {
              level += 1;
          }
        }
        else{
          while (key != 0 && key % 2 == 0){
            key = key / 2;
            level += 1;
          }
        }
        return level;
    }

    public MyEntry removeMin() {
	    if(size() == 0) return null;
        
        Node minNode = head.getNext(0);
        for(int i=0; i<=maxLevel; i++){
            if(head.getNext(i) == minNode){
                head.setNext(i, minNode.getNext(i));
            }
        }

        while(maxLevel > 0 && head.getNext(maxLevel) == null){
            maxLevel--;
        }

        return minNode.getEntry();
    }

    public void print() {
        Node tmp = head.getNext(0); // Partiamo dal primo elemento dopo il nodo head

        while (tmp != null && tmp.getEntry().getKey() != Integer.MAX_VALUE) { // Assicurati di non includere il nodo tail
            int actualLevel = tmp.getLevelCount(); // Ottieni il numero di livelli del nodo
            System.out.print(tmp.getEntry().getKey() + " " + tmp.getEntry().getValue() + " " + actualLevel);

            tmp = tmp.getNext(0); // Vai al prossimo nodo
            if (tmp != null && tmp.getEntry().getKey() != Integer.MAX_VALUE) {
                System.out.print(", "); // Stampa la virgola solo se ci sono altri elementi
            }
        }
        System.out.println(); // Aggiungi una nuova riga alla fine
    }


}

//TestProgram

public class TestProgram {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TestProgram <file_path>");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String[] firstLine = br.readLine().split(" ");
            int N = Integer.parseInt(firstLine[0]);
            double alpha = Double.parseDouble(firstLine[1]);
            System.out.println(N + " " + alpha);

            SkipListPQ skipList = new SkipListPQ(alpha);

            for (int i = 0; i < N; i++) {
                String[] line = br.readLine().split(" ");
                int operation = Integer.parseInt(line[0]);

                switch (operation) {
                    case 0:
                        skipList.min();
                        break;
                    case 1:
			            skipList.removeMin(); 
                        break;
                    case 2:
                        int key = Integer.parseInt(line[1]);
                        String value = line[2];
                        skipList.insert(key, value);
                        break;
                    case 3:
			            skipList.print();
                        break;
                    default:
                        System.out.println("Invalid operation code");
                        return;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}