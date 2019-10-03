package Service;

import Repo.GameRepo;
import domain.Game;
import domain.League;
import domain.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GameService {

    private GameRepo gameRepo;

    @Autowired
    public GameService(GameRepo gameRepo) {
        this.gameRepo = gameRepo;
    }


    public void updateEndedGame(Game game, int date, int status,
                                       League league, Team teamHome, Team teamAway,
                                        int scoresHome, int scoresAway) {
        gameRepo.updateEndedGame(game, date, status,
                league, teamHome, teamAway,
                scoresHome, scoresAway);
    }

    public Game createEndedGame(int id, int date, int status,
                                       League league, Team teamHome, Team teamAway,
                                       int scoresHome, int scoresAway) {
        return gameRepo.createEndedGame(id, date, status, league, teamHome, teamAway, scoresHome, scoresAway);

    }


    public void updateUpcomigGame(Game game, int date, int status, League league, Team teamHome, Team teamAway) {
       gameRepo.updateUpcomigGame(game, date, status, league, teamHome, teamAway);
    }


    public Game createUpcomingGame(int id, int date, int status, League league, Team teamHome, Team teamAway) {
        return gameRepo.createUpcomingGame(id, date, status, league, teamHome, teamAway);
    }

    public Game findGameById(int id) {
        return gameRepo.findGameById(id);
    }


    public List<Game> findGamesByStatusAndByDatePeriod(int status, Date date1, Date date2) {
        return gameRepo.findGamesByStatusAndByDatePeriod(status, date1, date2);
    }

    public int gameResult(Game game) {
        int res = 0;
        if (game.getStatus() == 0)
            return res;
         else if (game.getScoresHome() > game.getScoresAway())
            return res= 1;
         else if (game.getScoresHome() == game.getScoresAway())
             return res = 2;
        else if (game.getScoresHome() < game.getScoresAway())
            return res = 3;
        return res;
    }

}
