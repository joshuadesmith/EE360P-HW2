package lockTest;

/*
 * EID's of group members:
 * TK8375
 * JDS5228
 */
public class FairReadWriteLock {
    private int current = 0;
    private int ticketNum = 0;
    private int readerCount = 0;
    private int waitingReaderCount = 0;

    public synchronized void beginRead() throws InterruptedException {
        int ticketNum = this.ticketNum;
        this.waitingReaderCount++;
        while(ticketNum > this.current){this.wait();}
        this.readerCount++;
        this.waitingReaderCount--;
    }

    public synchronized void endRead() {
        this.readerCount--;
        if(this.readerCount == 0){
            this.current++;
            if(this.ticketNum < current)
            {this.ticketNum = current;
            }
        }
        notify();
    }

    public synchronized void beginWrite() throws InterruptedException {
        int ticket;
        if(this.readerCount == 0 && this.waitingReaderCount == 0){ticket = this.ticketNum;}
        else{
            ticket = this.ticketNum+1;
        }
        this.ticketNum = ticket + 1;
        while(ticket >this.current){this.wait();}

    }
    public synchronized void endWrite() {
        this.current++;
        notifyAll();

    }
}