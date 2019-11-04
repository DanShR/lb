package Repo;

import Util.HibernateUtil;
import domain.Game;
import domain.League;
import domain.Team;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public class GameRepo {

    synchronized public void updateEndedGame(Game game, int date, int status,
                                League league, Team teamHome, Team teamAway,
                                int scoresHome, int scoresAway) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        game.setDate(date);
        game.setStatus(status);
        game.setLeague(league);
        game.setTeamHome(teamHome);
        game.setTeamAway(teamAway);
        game.setScoresHome(scoresHome);
        game.setScoresAway(scoresAway);
        session.save(game);
    }

    synchronized public Game createEndedGame(int id, int date, int status,
                                League league, Team teamHome, Team teamAway,
                                int scoresHome, int scoresAway) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Game game = new Game();
        game.setId(id);
        game.setDate(date);
        game.setStatus(status);
        game.setLeague(league);
        game.setTeamHome(teamHome);
        game.setTeamAway(teamAway);
        game.setScoresHome(scoresHome);
        game.setScoresAway(scoresAway);
        session.save(game);
        return game;

    }

    synchronized public void updateUpcomigGame(Game game, int date, int status, League league, Team teamHome, Team teamAway) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        game.setDate(date);
        game.setStatus(status);
        game.setLeague(league);
        game.setTeamHome(teamHome);
        game.setTeamAway(teamAway);
        session.save(game);
    }


    synchronized public Game createUpcomingGame(int id, int date, int status, League league, Team teamHome, Team teamAway) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Game game = new Game();
        game.setId(id);
        game.setDate(date);
        game.setStatus(status);
        game.setLeague(league);
        game.setTeamHome(teamHome);
        game.setTeamAway(teamAway);
        session.save(game);
        return game;
    }

    synchronized public Game findGameById(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Game game = null;
        String sql = "from Game where id=:id";
        Query query = session.createQuery(sql);
        query.setParameter("id", id);
        game = (Game) query.uniqueResult();
        return  game;
    }


    @Transactional
    synchronized public List<Game> findGamesByStatusAndByDatePeriod(int status, Date date1, Date date2) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String sql = "from Game where status =:status and date between :d1 and  :d2";
        Query query = session.createQuery(sql);
        query.setParameter("d1", date1);
        query.setParameter("d2", date2);
        query.setParameter("status", status);
        List<Game> gameList= (List<Game>) query.list();
        return  gameList;
    }

    synchronized public void setGameScores(Game game, int status, int scoresHome, int scoresAway) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        game.setStatus(status);
        game.setScoresHome(scoresHome);
        game.setScoresAway(scoresAway);
        session.update(game);
    }
}
