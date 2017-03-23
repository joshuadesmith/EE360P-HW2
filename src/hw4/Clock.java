package hw4;

public class Clock {
    int clk;

    public Clock(){
        this.clk = 1;
    }

    public int getClock(){
        return clk;
    }

    public void tick(){
        this.clk ++;
    }

    public void compareAndUpdate(Clock CLK){
        this.clk = Math.max(this.clk, CLK.clk) + 1;
    }
    public void compareAndUpdate(int CLK){
        this.clk = Math.max(this.clk, CLK) + 1;
    }
}
