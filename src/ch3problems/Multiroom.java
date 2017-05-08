package ch3problems;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by joshuasmith on 5/8/17.
 */
public class Multiroom {
    ReentrantLock lock = new ReentrantLock();
    Condition onlyOneRoom = lock.newCondition();

    Semaphore waitLock = new Semaphore(1);

    boolean[] occupied;
    int numOccupants = 0;
    boolean roomWaiting;

    public Multiroom(int numRooms) {
        this.occupied = new boolean[numRooms];
        for (boolean b : this.occupied) b = false;
    }

    public void joinRoom(int room) {
        if (room < 0 || room >= occupied.length) System.out.println("Invalid room number: " + room);

        else {
            try {
                waitLock.acquire();
                while (!canJoin(room)) { onlyOneRoom.await(); }
                if (!occupied[room]) occupied[room] = true;
                numOccupants++;
                waitLock.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void leaveRoom(int room) {
        if (room < 0 || room >= occupied.length) System.out.println("Invalid room number: " + room);
        if (numOccupants == 0) System.out.println("Can't leave an empty room");
        else {
            if (--numOccupants == 0) {
                occupied[room] = false;
                onlyOneRoom.signalAll();
            }
        }
    }

    private boolean canJoin(int room) {
        for (int i = 0; i < occupied.length; i++) {
            if (i != room) {
                if (occupied[i]) return false;
            } else if (occupied[i]) return true;
        }
        return false;
    }
}
