package Task.Param;

import Enums.SchedulerTask;
import ParamFile.ParamFileReader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TaskParamInitializer {

    private TaskParam upcomingGames;
    private TaskParam endedGames;
    private TaskParam eventOdds;
    private TaskParam bets;
    private TaskParam betsResult;
    private int corePoolSize;
    private String token;

    @PostConstruct
    public void InitializeParameters() {
        ParamFileReader paramFileReader = new ParamFileReader();
        paramFileReader.initParamFile();
        HashMap<String, String> commonParams = paramFileReader.readCommonParams();
        generateCommonParams(commonParams);
        EnumMap<SchedulerTask, HashMap<String,String>> schedulerTasksParams = paramFileReader.readSchedulerTasksParams();
        generateSchedulerTasksParams(schedulerTasksParams);
    }

    private void generateCommonParams(HashMap<String, String> commonParams) {
        corePoolSize = Integer.parseInt(commonParams.get(ParamFileReader.PARAM_CORE_POOL_SIZE));
        token = commonParams.get(ParamFileReader.PARAM_TOKEN);
    }

    private void generateSchedulerTasksParams(EnumMap<SchedulerTask, HashMap<String,String>> params) {

        for(SchedulerTask schedulerTask : SchedulerTask.values()) {
            HashMap<String, String> currentTaskParams = params.get(schedulerTask);
            long initialDelay = Long.parseLong(currentTaskParams.get(ParamFileReader.TASK_PARAM_INITIAL_DELAY));
            long delay = Long.parseLong(currentTaskParams.get(ParamFileReader.TASK_PARAM_DELAY));
            boolean active = Integer.parseInt(currentTaskParams.get(ParamFileReader.TASK_PARAM_ACTIVE)) == 1;
            TimeUnit timeUnit = null;
            switch (currentTaskParams.get(ParamFileReader.TASK_PARAM_TIME_UNIT)) {
                case (ParamFileReader.TIME_UNIT_NAME_HOURS):
                    timeUnit = TimeUnit.HOURS;
                    break;
                case (ParamFileReader.TIME_UNIT_NAME_MINUTES):
                    timeUnit = TimeUnit.MINUTES;
                    break;
            }
            int executionParam = Integer.parseInt(currentTaskParams.get(ParamFileReader.TASK_PARAM_EXECUTION_PARAM));
            TaskParam currentTaskParam = new TaskParam(initialDelay, delay, timeUnit, executionParam, active);

            switch (schedulerTask) {
                case UPCOMING_GAMES:
                    upcomingGames = currentTaskParam;
                    break;
                case ENDED_GAMES:
                    endedGames = currentTaskParam;
                    break;
                case EVENTS_ODDS:
                    eventOdds = currentTaskParam;;
                    break;
                case BETS:
                    bets = currentTaskParam;
                    break;
                case BETS_RESULT:
                    betsResult = currentTaskParam;
                    break;

            }
        }
    }

    public TaskParam getUpcomingGames() {
        return upcomingGames;
    }

    public TaskParam getEndedGames() {
        return endedGames;
    }

    public TaskParam getEventOdds() {
        return eventOdds;
    }

    public TaskParam getBetsResult() {
        return betsResult;
    }

    public TaskParam getBets() {
        return bets;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public List<TaskParam> sportLoaderList(){
        List<TaskParam> loaderList = new ArrayList<>();
        loaderList.add(upcomingGames);
        loaderList.add(endedGames);
        loaderList.add(eventOdds);
        return loaderList;
    }

    public String getToken() {
        return token;
    }


}
