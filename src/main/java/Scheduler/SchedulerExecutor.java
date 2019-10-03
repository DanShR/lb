package Scheduler;

import Interface.Task;
import Task.*;
import Task.Param.TaskParam;
import Task.Param.TaskParamInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class SchedulerExecutor {
    private static TaskParamInitializer taskParamInitializer;
    private static Task endedGames;
    private static Task upcomingGames;
    private static Task eventOdds;
    private static Task bets;
    private static Task betsResult;

    @Autowired
    public SchedulerExecutor(TaskParamInitializer taskParamInitializer,
                             @Qualifier("endedGames") EndedGames endedGames,
                             @Qualifier("upcomingGames") UpcomingGames upcomingGames,
                             @Qualifier("eventOdds") EventOdds eventOdds,
                             @Qualifier("bets") Bets bets,
                             @Qualifier("betsResult") BetsResult betsResult) {
        this.taskParamInitializer = taskParamInitializer;
        this.endedGames = endedGames;
        this.upcomingGames = upcomingGames;
        this.eventOdds = eventOdds;
        this.bets = bets;
        this.betsResult = betsResult;
    }

    public static void start() {

        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(taskParamInitializer.getCorePoolSize());
        startTask(exec, endedGames);
        startTask(exec, upcomingGames);
        startTask(exec, eventOdds);
        startTask(exec, bets);
        startTask(exec, betsResult);

        ReentrantLock locker = new ReentrantLock();
        locker.lock();

        try {
            locker.newCondition().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void startTask(ScheduledThreadPoolExecutor exec, Task task) {
        TaskParam taskParam = task.getTaskParam();
        if (taskParam.isActive()) {
            TaskRunner taskRunner = new TaskRunner(task);
            exec.scheduleWithFixedDelay(taskRunner, taskParam.getInitialDelay(), taskParam.getDelay(), taskParam.getTimeUnit());
        }
    }
}
