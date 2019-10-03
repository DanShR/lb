package Repo;

import Util.HibernateUtil;
import domain.Country;
import domain.Team;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class TeamRepo {

    synchronized public Team findTeamById(int id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Team team = null;
        String sql = "from Team where id=:id";
        Query query = session.createQuery(sql);
        query.setParameter("id", id);
        team = (Team) query.uniqueResult();
        return  team;
    }

    synchronized public Team createTeam(int id, String name, Country country, String image_id) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Team team = new Team();
        team.setId(id);
        team.setName(name);
        team.setCountry(country);
        team.setImage_id(image_id);
        session.save(team);
        return team;
    }
}
