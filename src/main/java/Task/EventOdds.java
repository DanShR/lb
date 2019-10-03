package Task;

import Bot.Bot;
import Interface.Task;
import Json.JsonReader;
import Service.BookmakerService;
import Service.EventOddService;
import Service.GameService;
import Task.Param.TaskParam;
import Task.Param.TaskParamInitializer;
import Util.HibernateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import domain.Bookmaker;
import domain.EventOdd;
import domain.Game;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@DependsOn({"taskParamInitializer"})
public class EventOdds implements Task {

    private static final Logger log = Logger.getLogger(EventOdds.class);

    private final String URL = "https://api.betsapi.com/v2/event/odds/summary?";
    private final String JSON_NODE_NAME_MATCHING_DIR = "matching_dir";
    private final String JSON_NODE_NAME_ODDS = "odds";
    private final String JSON_NODE_NAME_END = "end";
    private final String JSON_NODE_NAME_1_1 = "1_1";

    private final String JSON_NODE_NAME_ID = "id";
    private final String JSON_NODE_NAME_HOME_OD = "home_od";
    private final String JSON_NODE_NAME_DRAW_OD = "draw_od";
    private final String JSON_NODE_NAME_AWAY_OD = "away_od";
    private final String JSON_NODE_NAME_TIME_STR = "time_str";
    private final String JSON_NODE_NAME_ADD_TIME = "add_time";

    private  String token;

    private TaskParam taskParam;
    private EventOddService eventOddService;
    private BookmakerService bookmakerService;
    private GameService gameService;
    private Bets bets;
    private TaskParamInitializer taskParamInitializer;



    @Autowired
    public EventOdds(EventOddService eventOddService, BookmakerService bookmakerService, TaskParamInitializer taskParamInitializer, GameService gameService, Bets bets) {
        this.eventOddService = eventOddService;
        this.bookmakerService = bookmakerService;
        this.taskParamInitializer = taskParamInitializer;
        this.gameService = gameService;
        this.bets = bets;
    }

    @PostConstruct
    public void postConstructInit(){
        this.token = taskParamInitializer.getToken();
        this.taskParam = taskParamInitializer.getEventOdds();
    }

    public void runTask() {
        HashMap<Game, String> urls = generateUrls();
        for (Map.Entry<Game, String> url : urls.entrySet()) {
            loadDataFromUrl(url.getKey(), url.getValue());
        }
    }

    private void loadDataFromUrl(Game game, String url) {
        int created;
        int updated;
        JsonReader jsonReader = new JsonReader(url);
        if (jsonReader.read()) {
            created = 0;
            updated = 0;
            HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
            for (Iterator<String> it = jsonReader.results.fieldNames(); it.hasNext(); ) {
                String bookmakerName = it.next();
                Bookmaker bookmaker = bookmakerService.getBookmaker(bookmakerName);
                JsonNode res = jsonReader.results.get(bookmakerName);

                if (res.get(JSON_NODE_NAME_MATCHING_DIR).asInt() != 1)
                    continue;

                JsonNode odds = res
                        .get(JSON_NODE_NAME_ODDS)
                        .get(JSON_NODE_NAME_END)
                        .get(JSON_NODE_NAME_1_1);

                if (odds.asText().equals("null"))
                    continue;

                int id = odds.get(JSON_NODE_NAME_ID).asInt();
                double homeOdd = odds.get(JSON_NODE_NAME_HOME_OD).asDouble();
                double drawOdd = odds.get(JSON_NODE_NAME_DRAW_OD).asDouble();
                double awayOdd = odds.get(JSON_NODE_NAME_AWAY_OD).asDouble();
                int addTime = odds.get(JSON_NODE_NAME_ADD_TIME).asInt();
                int timeStr = 0;
                if (!odds.get(JSON_NODE_NAME_TIME_STR).asText().equals("null"))
                    timeStr =  odds.get(JSON_NODE_NAME_TIME_STR).asInt();

                EventOdd eventOdd =  eventOddService.findEventOddById(id);
                if (eventOdd == null) {
                    eventOddService.createEventOdd(id, game, bookmaker, homeOdd, drawOdd, awayOdd, timeStr, addTime);
                    created++;
                }
                else {
                    eventOddService.updateEventOdd(eventOdd, game, bookmaker, homeOdd, drawOdd, awayOdd, timeStr, addTime);
                    updated++;
                }
            }
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
            log.info("created - " + created + " updated - " + updated);

            HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
            List<EventOdd> eventOddList = eventOddService.gameEventOdds(game);
            if (eventOddList.size() > 0)
                bets.calculateGameBets(game, eventOddList);
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();

        }
    }

    private HashMap<Game, String> generateUrls() {
        HashMap<Game,String> urls = new HashMap<>();
        List<Game> gameList = gameList();
        for (Game game : gameList) {
            urls.put(game, URL + "&token=" + token +"&event_id=" + game.getId());
        }
        return urls;
    }

    private List<Game> gameList() {
        Date date1 = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //calendar.add(Calendar.HOUR, taskParam.getExecutionParam());
        calendar.add(Calendar.HOUR,18);
        Date start = calendar.getTime();
        calendar.add(Calendar.HOUR,30);
        Date end = calendar.getTime();

        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        List<Game> games = gameService.findGamesByStatusAndByDatePeriod(0, start, end);
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
        log.info("generated game list. Count - " + games.size());
        return games;
    }

    public TaskParam getTaskParam() {
        return taskParam;
    }


}
