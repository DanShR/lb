package Repo;

import Util.HibernateUtil;
import domain.Bookmaker;
import domain.EventOdd;
import domain.Game;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventOddRepo {

    synchronized public void updateEventOdd(EventOdd eventOdd, Game game, Bookmaker bookmaker, double homeOdd, double drawOdd, double awayOdd, int timeStr, int addTime) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        eventOdd.setGame(game);
        eventOdd.setBookmaker(bookmaker);
        eventOdd.setHomeOdd(homeOdd);
        eventOdd.setDarwOdd(drawOdd);
        eventOdd.setAwayOdd(awayOdd);
        eventOdd.setTimeStr(timeStr);
        eventOdd.setAddTime(addTime);
        session.save(eventOdd);
    }


    synchronized public EventOdd createEventOdd(int id, Game game, Bookmaker bookmaker, double homeOdd, double drawOdd, double awayOdd, int timeStr, int addTime) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        EventOdd eventOdd = new EventOdd();
        eventOdd.setId(id);
        eventOdd.setGame(game);
        eventOdd.setBookmaker(bookmaker);
        eventOdd.setHomeOdd(homeOdd);
        eventOdd.setDarwOdd(drawOdd);
        eventOdd.setAwayOdd(awayOdd);
        eventOdd.setTimeStr(timeStr);
        eventOdd.setAddTime(addTime);
        session.save(eventOdd);
        return eventOdd;

    }

    synchronized public EventOdd findEventOddById(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        EventOdd eventOdd;
        String sql = "from EventOdd where id=:id";
        Query query = session.createQuery(sql);
        query.setParameter("id", id);
        eventOdd = (EventOdd) query.uniqueResult();

        return  eventOdd;
    }

    synchronized public List<EventOdd> gameEventOdds(Game game) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        EventOdd eventOdd;
        String sql = "from EventOdd where game=:game";
        Query query = session.createQuery(sql);
        query.setParameter("game", game);
        List<EventOdd> eventOdds = query.list();

        return  eventOdds;
    }
}
