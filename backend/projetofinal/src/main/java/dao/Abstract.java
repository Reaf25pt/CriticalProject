package dao;


import java.io.Serializable;
import java.util.List;

import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;

@TransactionAttribute(TransactionAttributeType.REQUIRED)
public abstract class Abstract<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Class<T> clazz;

    @PersistenceContext(unitName = "PersistenceUnit")
    protected EntityManager em;

    public Abstract(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T find(Object id) {
        return em.find(clazz, id);
    }

    /*
    public UserEntity findUserByToken(String token) {

        UserEntity ent = null;
        try {

            ent = (UserEntity) em.createNamedQuery("UserEntity.findUserByToken").setParameter("token", token)
                    .getSingleResult();

        } catch (Exception e) {
            e.printStackTrace();
            // return null;
        }
        return ent;
    }
*/
    public void persist(final T entity) {
        em.persist(entity);
    }

    public void detach(final T entity) {
        em.detach(entity);
    }

    public void merge(final T entity) {
        em.merge(entity);
    }

    public void remove(final T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    public List<T> findAll() {
        final CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(clazz);
        criteriaQuery.select(criteriaQuery.from(clazz));
        return em.createQuery(criteriaQuery).getResultList();
    }

    public void deleteAll() {
        final CriteriaDelete<T> criteriaDelete = em.getCriteriaBuilder().createCriteriaDelete(clazz);
        criteriaDelete.from(clazz);
        em.createQuery(criteriaDelete).executeUpdate();
    }

    public void flush() {
        em.flush();
    }
}