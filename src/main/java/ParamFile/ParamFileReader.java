package ParamFile;

import Enums.SchedulerTask;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;

public class ParamFileReader {

    //Param file
    private final String APP_DATA = "APPDATA";
    private final String DIR_NAME = "/sportloader";
    private final String FILE_NAME = "/TaskParam.json";
    private final String PATH_DEFAULT_PARAM_FILE = "src/main/resources/ShedulerParameters.json";

    //Tasks params
    private final String NODE_SCHEDULER_TASKS_PARAMS = "schedulerTasksParams";
    private final String TASK_NAME_UPCOMING_GAMES = "upcomingGames";
    private final String TASK_NAME_ENDED_GAMES = "endedGames";
    private final String TASK_NAME_EVENTS_ODDS = "eventOdds";
    private final String TASK_NAME_BETS = "bets";
    private final String TASK_NAME_BETS_RESULT = "betsResult";
    public static final String TASK_PARAM_INITIAL_DELAY = "initialDelay";
    public static final String TASK_PARAM_DELAY = "delay";
    public static final String TASK_PARAM_TIME_UNIT = "timeUnit";
    public static final String TASK_PARAM_EXECUTION_PARAM = "executionParam";
    public static final String TIME_UNIT_NAME_HOURS = "HOURS";
    public static final String TIME_UNIT_NAME_MINUTES = "MINUTES";
    public static final String TASK_PARAM_ACTIVE = "active";

    //Common params
    private static final String NODE_COMMON_PARAMS = "сommonParams";
    public static final String PARAM_CORE_POOL_SIZE = "corePoolSize";
    public static final String PARAM_TOKEN = "token";

    private JsonNode rootNode;

    public boolean initParamFile() {
        File paramFile = getSchedulerParamFile();
        if (paramFile == null)
            return false;
        rootNode = readParamFileToJsonNode(paramFile);
        if (rootNode == null)
            return false;

        return true;
    }

    public HashMap<String, String> readCommonParams() {
        JsonNode commonParamsNode = rootNode.get(NODE_COMMON_PARAMS);
        HashMap<String, String> commonParams = new HashMap<>();
        commonParams.put(PARAM_CORE_POOL_SIZE, commonParamsNode.get(PARAM_CORE_POOL_SIZE).asText());
        commonParams.put(PARAM_TOKEN, commonParamsNode.get(PARAM_TOKEN).asText());
        return commonParams;
    }

    public EnumMap<SchedulerTask, HashMap<String, String>> readSchedulerTasksParams() {
        EnumMap<SchedulerTask, HashMap<String, String>> paramsFromFile = new EnumMap<>(SchedulerTask.class);
        JsonNode schedulerParams = rootNode.get(NODE_SCHEDULER_TASKS_PARAMS);
        for (Iterator<String> it = schedulerParams.fieldNames(); it.hasNext(); ) {
            SchedulerTask schedulerTask = null;
            String key = it.next();
            switch (key) {
                case (TASK_NAME_UPCOMING_GAMES):
                    schedulerTask = SchedulerTask.UPCOMING_GAMES;
                    break;
                case (TASK_NAME_ENDED_GAMES):
                    schedulerTask = SchedulerTask.ENDED_GAMES;
                    break;
                case (TASK_NAME_EVENTS_ODDS):
                    schedulerTask = SchedulerTask.EVENTS_ODDS;
                    break;
                case (TASK_NAME_BETS):
                    schedulerTask = SchedulerTask.BETS;
                    break;
                case (TASK_NAME_BETS_RESULT):
                    schedulerTask = SchedulerTask.BETS_RESULT;
                    break;

            }
            JsonNode paramNode = schedulerParams.get(key);
            HashMap<String, String> currentParams = new HashMap<>();
            currentParams.put(TASK_PARAM_INITIAL_DELAY, paramNode.get(TASK_PARAM_INITIAL_DELAY).asText());
            currentParams.put(TASK_PARAM_DELAY, paramNode.get(TASK_PARAM_DELAY).asText());
            currentParams.put(TASK_PARAM_TIME_UNIT, paramNode.get(TASK_PARAM_TIME_UNIT).asText());
            currentParams.put(TASK_PARAM_EXECUTION_PARAM, paramNode.get(TASK_PARAM_EXECUTION_PARAM).asText());
            currentParams.put(TASK_PARAM_ACTIVE, paramNode.get(TASK_PARAM_ACTIVE).asText());

            paramsFromFile.put(schedulerTask, currentParams);
        }
        return paramsFromFile;
    }

    private File getSchedulerParamFile() {
        File paramFile = null;
        String appData = System.getenv(APP_DATA);

        File dirSportLoader = new File(appData + DIR_NAME);
        if (!dirSportLoader.exists()) {
            dirSportLoader.mkdir();
        }

        paramFile = new File(dirSportLoader, FILE_NAME);
        if (!paramFile.exists()) {
            File defaultParamFile = new File(PATH_DEFAULT_PARAM_FILE);
            try {
                FileUtils.copyFile(defaultParamFile,paramFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return paramFile;
    }

    private JsonNode readParamFileToJsonNode(File file) {
        String content = "";
        try {
            content = FileUtils.readFileToString(file, "UTF8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();

        JsonNode rootNode = null;
        try {
            rootNode = mapper.readValue(content, JsonNode.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rootNode;
    }
}
