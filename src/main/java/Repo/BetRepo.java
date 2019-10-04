package Repo;

import Util.HibernateUtil;
import domain.Bet;
import domain.Bookmaker;
import domain.EventOdd;
import domain.Game;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class BetRepo {

    synchronized public static void createBet(Game game, Bookmaker bookmaker, EventOdd eventOdd, int event, double odd, Date addTime, double ratio) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();

            Bet bet = new Bet();
            bet.setGame(game);
            bet.setBookmaker(bookmaker);
            bet.setEventOdd(eventOdd);
            bet.setEvent(event);
            bet.setOdd(odd);
            bet.setAddTime(addTime);
            bet.setRatio(ratio);
            bet.setCreated(new Date());
            session.save(bet);

    }

    synchronized public static Bet findBetByGameAndBookmakerAndEvent(Game game, Bookmaker bookmaker, int event) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Bet bet= null;
        String sql = "from Bet where game=:game and bookmaker=:bookmaker and event=:event";
        Query query = session.createQuery(sql);
        query.setParameter("game", game);
        query.setParameter("bookmaker", bookmaker);
        query.setParameter("event", event);
        bet = (Bet) query.uniqueResult();
        return  bet;
    }

    synchronized public List<Bet> findNotСalculatedBets() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String sql = "from Bet where result=0 and game.status=3";
        Query query = session.createQuery(sql);
        List<Bet> bets =  (List<Bet>) query.list();
        return  bets;
    }

    synchronized public void setBetResult(Bet bet, int result) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        bet.setResult(result);
        session.save(bet);
    }

    public Bet findBetByEvenOdd(EventOdd eventOdd) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Bet bet= null;
        String sql = "from Bet where eventOdd=:eventOdd";
        Query query = session.createQuery(sql);
        query.setParameter("eventOdd", eventOdd);
        bet = (Bet) query.uniqueResult();
        return  bet;
    }
}
