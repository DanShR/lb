package Task.Param;

import java.util.concurrent.TimeUnit;

public class TaskParam {
    private long initialDelay;
    private long delay;
    private TimeUnit timeUnit;
    private boolean active;
    private int executionParam;

    public TaskParam(long initialDelay, long delay, TimeUnit timeUnit, int executionParam, boolean active) {
        this.initialDelay = initialDelay;
        this.delay = delay;
        this.timeUnit = timeUnit;
        this.executionParam = executionParam;
        this.active = active;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getExecutionParam() {
        return executionParam;
    }

    public boolean isActive() {
        return active;
    }
}
