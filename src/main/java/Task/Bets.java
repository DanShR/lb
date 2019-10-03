package Task;

import Bot.Bot;
import Interface.Task;
import Service.BetService;
import Service.GameService;
import Task.Param.TaskParam;
import Task.Param.TaskParamInitializer;
import Util.HibernateUtil;
import domain.Bookmaker;
import domain.EventOdd;
import domain.Game;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Component
@DependsOn({"taskParamInitializer"})
public class Bets implements Task {

    private final double CORRECTION_FACTOR_HOME = 1.034;
    private final double CORRECTION_FACTOR_DRAW = 1.057;
    private final double CORRECTION_FACTOR_AWAY = 1.037;
    private final int MINUTES_ADD_ODD = 240;

    private static final Logger log = Logger.getLogger(Bets.class);

    private TaskParam taskParam;
    private TaskParamInitializer taskParamInitializer;
    private GameService gameService;
    private BetService betService;
    private static Bot bot = new Bot();

    @Autowired
    public Bets(TaskParamInitializer taskParamInitializer, GameService gameService, BetService betService) {
        this.taskParamInitializer = taskParamInitializer;
        this.gameService = gameService;
        this.betService = betService;
    }

    @PostConstruct
    public void postConstructInit(){
        this.taskParam = taskParamInitializer.getBets();
    }

    @Override
    public void runTask() {
        calculateBets();
    }

    @Override
    public TaskParam getTaskParam() {
        return taskParam;
    }

    public void calculateBets() {
       /* List<Game> games = gameList();
        for (Game game : games) {
            HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
            calculateGameBets(game);
            HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
        }*/

    }

    public void calculateGameBets(Game game, List<EventOdd> eventOddList) {
        Date max = eventOddList.stream().map(EventOdd::getAddTime).max(Date::compareTo).get();
        eventOddList.sort(Comparator.comparing(EventOdd::getAddTime).reversed());
        List<EventOdd> finalOdds = new ArrayList<>();
        for (EventOdd eventOdd : eventOddList) {
            if ((max.getTime() - eventOdd.getAddTime().getTime()) / (60 *1000) > 300)
                continue;
            Bookmaker bookmaker = eventOdd.getBookmaker();
            EventOdd existOdd = finalOdds.stream().filter((od) -> od.getBookmaker() == bookmaker).findAny().orElse(null);
            if (existOdd == null) {
                finalOdds.add(eventOdd);
            }
        }
        if (finalOdds.size() < 3) {
            return;
        }
        HashMap<String, Double> probability = eventProbability(finalOdds);
        HashMap<String, List<EventOdd>> bets = betList(finalOdds, probability);
        saveBets(bets.get("home"), "home", probability);
        saveBets(bets.get("draw"), "draw", probability);
        saveBets(bets.get("away"), "away", probability);
    }

    private List<EventOdd> finalOddsList(Game game) {

        List<EventOdd> lastOdds = game.getEventOdds().stream().filter((ev) ->
                ((game.getDate().getTime() - ev.getAddTime().getTime()) / (1000 * 60)) <= MINUTES_ADD_ODD
                        && (game.getDate().getTime() > ev.getAddTime().getTime())
                        && ev.getHomeOdd() > 1 && ev.getDarwOdd() > 1 && ev.getAwayOdd() > 1
        ).collect(Collectors.toList());

        lastOdds.sort(Comparator.comparing(EventOdd::getAddTime).reversed());
        List<EventOdd>  finalOdds = new ArrayList<EventOdd>();
        for (EventOdd eventOdd : lastOdds) {
            Bookmaker bookmaker = eventOdd.getBookmaker();
            EventOdd existOdd = finalOdds.stream().filter((od) -> od.getBookmaker() == bookmaker).findAny().orElse(null);
            if (existOdd == null) {
                finalOdds.add(eventOdd);
            }
        }
        return finalOdds;
    }

    private HashMap<String, Double> eventProbability(List<EventOdd> finalOdds) {

        double sumHome = 0, sumDraw = 0, sumAway = 0;
        double avgHome, avgDraw, avgAway;
        double probabilitySum;
        double probabilityHome, probabilityDraw, probabilityAway;
        double probabilityConsHome, probabilityConsDraw, probabilityConsAway;
        double probabilityRealHome, probabilityRealDraw, probabilityRealAway;

        for (EventOdd eventOdd : finalOdds) {
            sumHome += eventOdd.getHomeOdd();
            sumDraw += eventOdd.getDarwOdd();
            sumAway += eventOdd.getAwayOdd();
        }

        avgHome = sumHome / finalOdds.size();
        avgDraw = sumDraw / finalOdds.size();
        avgAway = sumAway / finalOdds.size();

        probabilityHome = 1 / avgHome;
        probabilityDraw = 1 / avgDraw;
        probabilityAway = 1 / avgAway;

        probabilitySum = probabilityHome + probabilityDraw + probabilityAway;

        probabilityConsHome = probabilityHome / probabilitySum * 100;
        probabilityConsDraw = probabilityDraw / probabilitySum * 100;
        probabilityConsAway = probabilityAway / probabilitySum * 100;

        probabilityRealHome = CORRECTION_FACTOR_HOME / probabilityConsHome * 100;
        probabilityRealDraw = CORRECTION_FACTOR_DRAW / probabilityConsDraw * 100;
        probabilityRealAway = CORRECTION_FACTOR_AWAY / probabilityConsAway * 100;

        HashMap<String, Double> probability = new HashMap<String, Double>();
        probability.put("home", probabilityRealHome);
        probability.put("draw", probabilityRealDraw);
        probability.put("away", probabilityRealAway);
        return probability;
    }

    private HashMap<String, List<EventOdd>> betList(List<EventOdd> finalOdds, HashMap<String, Double> probability) {

        List<EventOdd> finalHome = finalOdds
                .stream()
                .filter((od) ->
                        od.getHomeOdd() > probability.get("home"))
                .collect(Collectors.toList());

        List<EventOdd> finalDraw = finalOdds
                .stream()
                .filter((od) ->
                        od.getDarwOdd() > probability.get("draw"))
                .collect(Collectors.toList());

        List<EventOdd> finalAway = finalOdds
                .stream()
                .filter((od) ->
                        od.getAwayOdd() > probability.get("away"))
                .collect(Collectors.toList());

        HashMap<String, List<EventOdd>> finalEventOdds = new HashMap<>();
        finalEventOdds.put("home", finalHome);
        finalEventOdds.put("draw", finalDraw);
        finalEventOdds.put("away", finalAway);

        return finalEventOdds;
    }

    private void saveBets(List<EventOdd> eventOdds, String type, HashMap<String, Double> probability) {
        for (EventOdd eventOdd : eventOdds) {
            double odd = 0;
            int event = 0;
            double probabilityEvent = 0;
            double ratio = 0;

            if (type == "home") {
                odd = eventOdd.getHomeOdd();
                event = 1;
                probabilityEvent = probability.get("home");
            }
            else if (type == "draw") {
                odd = eventOdd.getDarwOdd();
                event = 2;
                probabilityEvent = probability.get("draw");
            }
            else if (type == "away") {
                odd = eventOdd.getAwayOdd();
                event = 3;
                probabilityEvent = probability.get("away");
            }

            if (probabilityEvent == 0 )
                ratio = 0;
            else
                ratio = odd / probabilityEvent;

            if (betService.findBetByEvenOdd(eventOdd) == null) {
                Game game = eventOdd.getGame();

                betService.createBet(game, eventOdd.getBookmaker(), eventOdd, event, odd, eventOdd.getAddTime(), ratio);
                if (odd <= 2.5) {
                    bot.sendMsg("430539607",
                            game.getLeague().getName()
                                    + "\n" + game.getDate()
                            + "\n" + game.getTeamHome().getName() + " - " + game.getTeamAway().getName()
                            + "\nevent " + event + " - " + odd + " ratio - " + ratio
                            + "\n" + eventOdd.getBookmaker().getName());
                }
            }
        }

    }

    private List<Game> gameList() {
        Date date1 = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //calendar.add(Calendar.HOUR, taskParam.getExecutionParam());
        calendar.add(Calendar.HOUR, taskParam.getExecutionParam());
        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        List<Game> games = gameService.findGamesByStatusAndByDatePeriod(0, date1, calendar.getTime());
        games.forEach(g-> Hibernate.initialize(g.getEventOdds()));
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
        log.info("generated game list. Count - " + games.size());
        return games;
    }
}
