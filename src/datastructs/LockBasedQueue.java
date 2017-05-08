package datastructs;

import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by joshuasmith on 5/8/17.
 */
public class LockBasedQueue<T> {
    ReentrantLock enqLock, deqLock;
    Node<T> head;
    Node<T> tail;
    int size;

    public LockBasedQueue() {
        this.enqLock = new ReentrantLock();
        this.deqLock = new ReentrantLock();
        this.head = new Node<T>(null);
        this.tail = this.head;
        this.size = 0;
    }

    public T dequeue() throws NoSuchElementException {
        T result;
        deqLock.lock();

        try {
            if (head.next == null) throw new NoSuchElementException();
            result = (T) head.next.data;
            head = head.next;
        } finally {
            deqLock.unlock();
        }

        return result;
    }

    public void enqueue(T data) throws NullPointerException {
        if (data == null) throw new NullPointerException();
        enqLock.lock();

        try {
            Node<T> node = new Node<>(data);
            tail.next = node;
            tail = node;
        } finally {
            enqLock.unlock();
        }
    }
}
