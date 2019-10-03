package Repo;

import Util.HibernateUtil;
import domain.Bookmaker;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookmakerRepo {

    synchronized public Bookmaker createBookmaker(String name) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Bookmaker bookmaker = new Bookmaker();
        bookmaker.setName(name);
        session.save(bookmaker);
        return  bookmaker;
    }

    synchronized public Bookmaker getBookmaker(String name) {

        Bookmaker bookmaker = null;
        bookmaker = findBookmakerByName(name);
        if (bookmaker == null)
            bookmaker = createBookmaker(name);
        return bookmaker;
    }

    synchronized public Bookmaker findBookmakerByName(String name) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Bookmaker bookmaker = null;
        String sql = "from Bookmaker where name=:name";
        Query query = session.createQuery(sql);
        query.setParameter("name", name);
        bookmaker = (Bookmaker) query.uniqueResult();
        return  bookmaker;

    }

    synchronized public List<Bookmaker> findAllBookmaker() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        String sql = "from Bookmaker ";
        Query query = session.createQuery(sql);
        List<Bookmaker> bookmakerList= (List<Bookmaker>) query.list();

        return  bookmakerList;
    }
}
