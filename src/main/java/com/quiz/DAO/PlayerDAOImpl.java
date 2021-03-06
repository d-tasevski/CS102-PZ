package com.quiz.DAO;

import com.quiz.entities.Player;
import com.quiz.exceptions.AuthException;
import com.quiz.interfaces.PlayerDAO;
import com.quiz.util.HibernateUtil;
import com.quiz.util.PasswordUtils;
import org.hibernate.Session;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Data Access Object za Player klasu. Ova klasa se koristi u Player kontroleru za komunikaciju sa bazom podataka.
 */
public class PlayerDAOImpl implements PlayerDAO {
    /**
     * Podrazumevani konstruktor
     */
    public PlayerDAOImpl() {
    }

    /**
     * @param email    String - email igrača
     * @param password String - Enkriptovana šifra korisnika
     * @return instanca Player igrača, ukoliko je ulogovan uspešno
     * @throws AuthException - greška koja se izbacuje ukoliko je došlo do greške prilikom logovanja
     */
    public Player login(String email, String password) throws AuthException {
        Session session = HibernateUtil.getCurrentSession();
        session.beginTransaction();

        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Player> query = builder.createQuery(Player.class);
            Root<Player> root = query.from(Player.class);
            query.where(builder.equal(root.get("email"), email));
            TypedQuery<Player> q = session.createQuery(query);
            Player result = q.getSingleResult();
            boolean correctPass = PasswordUtils.checkPassword(password, result.getPassword());

            if (!correctPass) {
                throw new AuthException("Login Failed");
            }
            return result;
        } finally {
            session.getTransaction().commit();
        }
    }

    /**
     * @param newPlayer instanca klase Player, koju trebamo registrovati u bazi podataka
     */
    public void register(Player newPlayer) {
        Session s = HibernateUtil.getCurrentSession();
        s.beginTransaction();
        s.save(newPlayer);
        s.getTransaction().commit();
    }

    /**
     * @param player Player objekat koji menjamo
     */
    public void updatePlayer(Player player) {
        Session s = HibernateUtil.getCurrentSession();
        s.beginTransaction();
        s.update(player);
        s.getTransaction().commit();
    }

    /**
     * @return lista igrača iz baze podataka
     */
    @Override
    public List<Player> getPlayerData() {
        Session s = HibernateUtil.getCurrentSession();
        s.beginTransaction();

        List<Player> studentData = s.createQuery("from Player p ORDER BY p.totalPoints DESC").getResultList();
        s.getTransaction().commit();

        return studentData;
    }
}
