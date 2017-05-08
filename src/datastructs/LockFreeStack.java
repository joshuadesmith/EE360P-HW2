package datastructs;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by joshuasmith on 5/8/17.
 */
public class LockFreeStack<T> {
    AtomicReference<Node<T>> top = new AtomicReference<>(null);

    public void push(T data) {
        Node<T> node = new Node<T>(data);

        while (true) {
            Node<T> oldTop = top.get();
            node.next = oldTop;
            if (top.compareAndSet(oldTop, node)) return;
            else Thread.yield();
        }
    }

    public T pop() throws NoSuchElementException {
        while (true) {
            Node<T> oldTop = top.get();
            if (oldTop == null) throw new NoSuchElementException();
            T data = oldTop.data;
            Node<T> newTop = oldTop.next;
            if (top.compareAndSet(oldTop, newTop)) return data;
            else Thread.yield();
        }
    }
}
