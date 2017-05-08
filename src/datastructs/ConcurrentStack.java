package datastructs;

import java.util.NoSuchElementException;

/**
 * Created by joshuasmith on 5/8/17.
 */
public class ConcurrentStack<T> {
    private Node<T> top = null;

    public synchronized void push(T data) {
        Node<T> node = new Node<>(data);
        node.next = top;
        top = node;
    }

    public synchronized T pop() throws NoSuchElementException {
        if (top == null) {
            throw new NoSuchElementException();
        } else {
            Node<T> oldTop = top;
            top = top.next;
            return oldTop.data;
        }
    }
}
