package hw4;


public class TimeStamp implements Comparable<TimeStamp>{
    int pid;
    int clk;
    String message;

    public TimeStamp(int ID, int CLK, String MESSAGE){
        this.pid = ID;
        this.clk = CLK;
        this.message = MESSAGE;
    }
    @Override
    public int compareTo(TimeStamp o) {
        if(this.clk < o.clk)return -1;
        else if(this.clk > o.clk) return 1;
        else{
            if(this.pid < o.pid) return -1;
            else if(this.pid>o.pid) return 1;
        }
        return 0;
    }

}
