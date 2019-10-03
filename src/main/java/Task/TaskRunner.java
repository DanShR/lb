package Task;

import Interface.Task;
import org.apache.log4j.Logger;

public class TaskRunner implements Runnable {

    private static final Logger log = Logger.getLogger(TaskRunner.class);
    private Task task;

    public TaskRunner(Task task) {
        this.task = task;
    }

    public void run() {
        log.info("####Task started - " + task.getClass());
        task.runTask();
        log.info("####Task finished"+ task.getClass());
    }
}
