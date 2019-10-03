package Task;

import Interface.Task;
import Service.BetService;
import Service.GameService;
import Task.Param.TaskParam;
import Task.Param.TaskParamInitializer;
import Util.HibernateUtil;
import domain.Bet;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@DependsOn({"taskParamInitializer"})
public class BetsResult implements Task {

    private static final Logger log = Logger.getLogger(BetsResult.class);

    private TaskParam taskParam;
    private TaskParamInitializer taskParamInitializer;
    private GameService gameService;
    private BetService betService;


    @Autowired
    public BetsResult(TaskParamInitializer taskParamInitializer, GameService gameService, BetService betService) {
        this.taskParamInitializer = taskParamInitializer;
        this.gameService = gameService;
        this.betService = betService;
    }

    @PostConstruct
    public void postConstructInit(){
        this.taskParam = taskParamInitializer.getBetsResult();
    }

    @Override
    public void runTask() {
        HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        List<Bet> notСalculatedBets = betService.findNotСalculatedBets();
        for (Bet bet : notСalculatedBets) {
            if (gameService.gameResult(bet.getGame()) == bet.getEvent())
                betService.setWinResult(bet);
            else
                betService.setLoseResult(bet);
        }
        HibernateUtil.getSessionFactory().getCurrentSession().getTransaction().commit();
    }

    @Override
    public TaskParam getTaskParam() {
        return taskParam;
    }

}
