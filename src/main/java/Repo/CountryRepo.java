package Repo;

import Util.HibernateUtil;
import domain.Country;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class CountryRepo {
    synchronized public Country findCountryByCode(String code) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Country country ;
        String sql = "from Country where code=:code";
        Query query = session.createQuery(sql);
        if (code != "null") {
            query.setParameter("code", code);
        } else {
            query.setParameter("code","wr");
        }
        country = (Country) query.uniqueResult();

        return  country;
    }

}
