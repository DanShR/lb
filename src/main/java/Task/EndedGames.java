package Task;

import Interface.Task;
import Json.JsonReader;
import Service.BetService;
import Service.GameService;
import Service.LeagueService;
import Service.TeamService;
import Task.Param.TaskParam;
import Task.Param.TaskParamInitializer;
import Util.HibernateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import domain.Game;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@DependsOn({"taskParamInitializer"})
public class EndedGames implements Task {
    private static final Logger log = Logger.getLogger(EndedGames.class);

    private final String URL = "https://api.betsapi.com/v1/event/view?";

    private final String JSON_NODE_NAME_TIME_STATUS = "time_status";
    private final String JSON_NODE_NAME_SCORES = "scores";
    private final String JSON_NODE_NAME_HOME = "home";
    private final String JSON_NODE_NAME_AWAY = "away";

    private  String token;
    private TaskParam taskParam;
    private LeagueService  leagueService;
    private TeamService teamService;
    private GameService gameService;
    private BetService betService;
    TaskParamInitializer taskParamInitializer;

    @Autowired
    public EndedGames(LeagueService leagueService, TeamService teamService, GameService gameService, BetService betService, TaskParamInitializer taskParamInitializer) {
        this.leagueService = leagueService;
        this.teamService = teamService;
        this.gameService = gameService;
        this.betService = betService;
        this.taskParamInitializer = taskParamInitializer;
    }

    @PostConstruct
    public void postConstructInit(){
        this.token = taskParamInitializer.getToken();
        this.taskParam = taskParamInitializer.getEndedGames();
    }

    public void runTask() {
        HashMap<Game, String> urls = generateUrls();
        for (Map.Entry<Game, String> url : urls.entrySet()) {
            loadDataFromUrl(url.getKey(), url.getValue());
        }
    }

    private void loadDataFromUrl(Game game, String url) {
        int loaded;
        JsonReader jsonReader = new JsonReader(url);
        if (jsonReader.read()) {
            HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
            for (final JsonNode objNode : jsonReader.results) {
                int status = objNode.get(JSON_NODE_NAME_TIME_STATUS).asInt();
                if (status == 3) {
                    JsonNode scoresNode = objNode.get(JSON_NODE_NAME_SCORES);
                    int scoresHome = scoresNode.get("2").get(JSON_NODE_NAME_HOME).asInt();
                    int scoresAway = scoresNode.get("2").get(JSON_NODE_NAME_AWAY).asInt();
                    gameService.setGameScores(game, status, scoresHome, scoresAway);
                }
            }
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
        }
    }

    private HashMap<Game, String> generateUrls() {
        HashMap<Game,String> urls = new HashMap<>();
        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        List<Game> gameList = betService.findGamesFromBetsWithNotendedStatus();
        for (Game game : gameList) {
            urls.put(game, URL + "&token=" + token +"&event_id=" + game.getId());
        }
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
        return urls;

    }

    public TaskParam getTaskParam() {
        return taskParam;
    }
}
