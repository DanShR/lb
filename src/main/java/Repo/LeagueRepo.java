package Repo;

import Util.HibernateUtil;
import domain.Country;
import domain.League;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class LeagueRepo {
    synchronized public League findLeagueById(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        League league = null;
        String sql = "from League where id=:id";
        Query query = session.createQuery(sql);
        query.setParameter("id", id);
        league = (League) query.uniqueResult();

        return  league;
    }

    synchronized public League createLeague(int id, String name, Country country) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        League league = new League();
        league.setId(id);
        league.setName(name);
        league.setCountry(country);
        session.save(league);
        return league;
    }
}
