package Task;

import Interface.Task;
import Json.JsonPageReader;
import Service.GameService;
import Service.LeagueService;
import Service.TeamService;
import Task.Param.TaskParam;
import Task.Param.TaskParamInitializer;
import Util.HibernateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import domain.Game;
import domain.League;
import domain.Team;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@DependsOn({"taskParamInitializer"})
public class UpcomingGames implements Task {
    private static final Logger log = Logger.getLogger(UpcomingGames.class);
    private final String URL = "https://api.betsapi.com/v2/events/upcoming?sport_id=1";
    private final String JSON_NODE_NAME_ID = "id";
    private final String JSON_NODE_NAME_TIME = "time";
    private final String JSON_NODE_NAME_TIME_STATUS = "time_status";
    private final String JSON_NODE_NAME_HOME = "home";
    private final String JSON_NODE_NAME_AWAY = "away";
    private final String JSON_NODE_NAME_LEAGUE= "league";

    private  String token;
    private TaskParam taskParam;
    private LeagueService leagueService;
    private TeamService teamService;
    private GameService gameService;
    TaskParamInitializer taskParamInitializer;

    @Autowired
    public UpcomingGames(LeagueService leagueService, TeamService teamService, GameService gameService, TaskParamInitializer taskParamInitializer) {
        this.leagueService = leagueService;
        this.teamService = teamService;
        this.gameService = gameService;
        this.taskParamInitializer = taskParamInitializer;
    }

    @PostConstruct
    public void postConstructInit(){
        this.token = taskParamInitializer.getToken();
        this.taskParam = taskParamInitializer.getUpcomingGames();
    }

    public void runTask() {
        List<String> urls = generateUrls();
        for (String url : urls) {
            loadDataFromUrlWithPageParam(url);
        }
    }

    public void loadDataFromUrlWithPageParam(String url) {
        int created;
        int updated;
        JsonPageReader jsonPageReader = new JsonPageReader(url);
        while (jsonPageReader.readNextPage()) {
            if (jsonPageReader.results == null)
                continue;
            created = 0;
            updated = 0;
            HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
            for (final JsonNode objNode : jsonPageReader.results) {

                int id = objNode.get(JSON_NODE_NAME_ID).asInt();
                int date = objNode.get(JSON_NODE_NAME_TIME).asInt();
                int status = objNode.get(JSON_NODE_NAME_TIME_STATUS).asInt();

                League league = leagueService.getLeagueFromJsonNode(objNode.get(JSON_NODE_NAME_LEAGUE));
                Team teamHome = teamService.getTeamFromJsonNode(objNode.get(JSON_NODE_NAME_HOME));
                Team teamAway = teamService.getTeamFromJsonNode(objNode.get(JSON_NODE_NAME_AWAY));

                Game game = gameService.findGameById(id);
                if (game == null) {
                    gameService.createUpcomingGame(id, date, status, league, teamHome, teamAway);
                    created++;
                }
                else {
                    gameService.updateUpcomigGame(game, date, status, league, teamHome, teamAway);
                    updated++;
                }
            }
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
            log.info("created - " + created + " updated - " + updated);
        }
    }

    private List<String> generateUrls() {
        List<String> urls = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        for(int i = 1; i <= taskParam.getExecutionParam(); i++) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            calendar.add(Calendar.DATE, 1);
            urls.add(URL + "&token=" + token + "&day=" + dateFormat.format( calendar.getTime() ));
        }
        return urls;
    }

    public TaskParam getTaskParam() {
        return taskParam;
    }

}
