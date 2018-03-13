package nl.dutchman.spacetrimmer.utils;

public class ProcessInformation
{
    private long startTime, endTime;

    public void start()
    {
        this.startTime = System.currentTimeMillis();
    }

    public void end()
    {
        this.endTime = System.currentTimeMillis();
    }

    public long getTime()
    {
        return this.endTime - this.startTime;
    }
}
