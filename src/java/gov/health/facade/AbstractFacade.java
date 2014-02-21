package gov.health.facade;

import java.util.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;

/**
 *
 * @author Buddhika
 * @param <T>
 */
public abstract class AbstractFacade<T> {

    private final Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void refresh(T entity) {
        getEntityManager().refresh(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll(boolean withoutRetired) {
        return findAll(null, null, withoutRetired);
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery<T> cq = cb.createQuery(entityClass);
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findAll(String fieldName) {
        return findAll(fieldName, "", false);
    }

    public List<T> findAll(String fieldName, boolean withoutRetired) {
        return findAll(fieldName, "", withoutRetired);
    }

    public List<T> findAll(String fieldName, String fieldValue) {
        return findAll(fieldName, fieldValue, false);
    }

    public List<T> findBySQL(String temSQL) {
        TypedQuery<T> qry = getEntityManager().createQuery(temSQL, entityClass);
        return qry.getResultList();
    }

    public List<T> findBySQL(String temSQL, int maxResults) {
        TypedQuery<T> qry = getEntityManager().createQuery(temSQL, entityClass);
        qry.setMaxResults(maxResults);
        return qry.getResultList();
    }

    public List<T> findBySQL(String temSQL, Map<String, Object> parameters) {
        TypedQuery<T> qry = getEntityManager().createQuery(temSQL, entityClass);
        Set s = parameters.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry m = (Map.Entry) it.next();
            String pPara = (String) m.getKey();
            if (m.getValue() instanceof Date) {
                Date pVal = (Date) m.getValue();
                qry.setParameter(pPara, pVal, TemporalType.DATE);
                System.out.println("Parameter " + pPara + "\tVal" + pVal);
            } else {
                Object pVal = (Object) m.getValue();
                qry.setParameter(pPara, pVal);
                System.out.println("Parameter " + pPara + "\tVal" + pVal);
            }
        }
        return qry.getResultList();
    }

    public List<T> findBySQL(String temSQL, Map<String, Object> parameters, TemporalType tt) {
        TypedQuery<T> qry = getEntityManager().createQuery(temSQL, entityClass);
        Set s = parameters.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry m = (Map.Entry) it.next();
            Object pVal = m.getValue();
            String pPara = (String) m.getKey();
            if (pVal instanceof Date) {
                Date d = (Date) pVal;
                qry.setParameter(pPara, d, tt);
            } else {
                qry.setParameter(pPara, pVal);
            }
            //    System.out.println("Parameter " + pPara + "\tVal" + pVal);
        }
        return qry.getResultList();
    }

    public double findDoubleByJpql(String temSQL, Map<String, Object> parameters, TemporalType tt) {
        TypedQuery<Double> qry = (TypedQuery<Double>) getEntityManager().createQuery(temSQL);
        Set s = parameters.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry m = (Map.Entry) it.next();
            Object pVal = m.getValue();
            String pPara = (String) m.getKey();
            if (pVal instanceof Date) {
                System.out.println("pval is a date");
                Date d = (Date) pVal;
                qry.setParameter(pPara, d, tt);
            } else {
                System.out.println("p val is NOT a date");
                qry.setParameter(pPara, pVal);
            }
            System.out.println("Parameter " + pPara + "\t and Val\t " + pVal);
        }
        try {
            return (double) qry.getSingleResult();
        } catch (Exception e) {
            return 0.0;
        }
    }

    
    public Long findLongByJpql(String temSQL, Map<String, Object> parameters, TemporalType tt) {
        TypedQuery<Long> qry = (TypedQuery<Long>) getEntityManager().createQuery(temSQL);
        Set s = parameters.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry m = (Map.Entry) it.next();
            Object pVal = m.getValue();
            String pPara = (String) m.getKey();
            if (pVal instanceof Date) {
                System.out.println("pval is a date");
                Date d = (Date) pVal;
                qry.setParameter(pPara, d, tt);
            } else {
                System.out.println("p val is NOT a date");
                qry.setParameter(pPara, pVal);
            }
            System.out.println("Parameter " + pPara + "\t and Val\t " + pVal);
        }
        try {
            return (Long) qry.getSingleResult();
        } catch (Exception e) {
            return 0l;
        }
    }

    
    
    
    public List<T> findBySQL(String temSQL, Map<String, Object> parameters, TemporalType tt, int maxRecords) {
        TypedQuery<T> qry = getEntityManager().createQuery(temSQL, entityClass);
        Set s = parameters.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry m = (Map.Entry) it.next();
            Object pVal = m.getValue();
            String pPara = (String) m.getKey();
            if (pVal instanceof Date) {
                Date d = (Date) pVal;
                qry.setParameter(pPara, d, tt);
            } else {
                qry.setParameter(pPara, pVal);
            }
            System.out.println("Parameter " + pPara + "\tVal" + pVal);
        }
        qry.setMaxResults(maxRecords);
        return qry.getResultList();
    }

    private void test(Class myClass, Object ob) {
    }

    public Long countBySql(String sql) {
        Query q = getEntityManager().createQuery(sql);
        return (Long) q.getSingleResult();
    }

    public Double sumBySql(String sql) {
        Query q = getEntityManager().createQuery(sql);
        return (Double) q.getSingleResult();
    }

    public List<T> findAll(String fieldName, String fieldValue, boolean withoutRetired) {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery<T> cq = cb.createQuery(entityClass);
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        ParameterExpression<String> p = cb.parameter(String.class);
        Predicate predicateField = cb.like(rt.<String>get(fieldName), fieldValue);
        Predicate predicateRetired = cb.equal(rt.<Boolean>get("retired"), false);
        Predicate predicateFieldRetired = cb.and(predicateField, predicateRetired);

        if (withoutRetired && !fieldValue.equals("")) {
            cq.where(predicateFieldRetired);
        } else if (withoutRetired) {
            cq.where(predicateRetired);
        } else if (!fieldValue.equals("")) {
            cq.where(predicateField);
        }

        if (!fieldName.equals("")) {
            cq.orderBy(cb.asc(rt.get(fieldName)));
        }

        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findExact(String fieldName, String fieldValue, boolean withoutRetired) {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery<T> cq = cb.createQuery(entityClass);
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        ParameterExpression<String> p = cb.parameter(String.class);
//        Predicate predicateField = cb.like(rt.<String>get(fieldName), fieldValue);
        Predicate predicateField = cb.equal(cb.upper(rt.<String>get(fieldName)), fieldValue.toLowerCase());
        Predicate predicateRetired = cb.equal(rt.<Boolean>get("retired"), false);
        Predicate predicateFieldRetired = cb.and(predicateField, predicateRetired);

        if (withoutRetired && !fieldValue.equals("")) {
            cq.where(predicateFieldRetired);
        } else if (withoutRetired) {
            cq.where(predicateRetired);
        } else if (!fieldValue.equals("")) {
            cq.where(predicateField);
        }

        if (!fieldName.equals("")) {
            cq.orderBy(cb.asc(rt.get(fieldName)));
        }

        return getEntityManager().createQuery(cq).getResultList();
    }

    public List findGroupingBySql(String sql) {
        Query qry = getEntityManager().createQuery(sql);
        return qry.getResultList();
    }

    public List findGroupingBySql(String sql, Map parameters) {
        return findGroupingBySql(sql, parameters, TemporalType.DATE);
    }

    public List findGroupingBySql(String sql, Map parameters, TemporalType tt) {
        Query qry = getEntityManager().createQuery(sql);
        Set s = parameters.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry m = (Map.Entry) it.next();
            Object pVal = m.getValue();
            String pPara = (String) m.getKey();
            if (pVal instanceof Date) {
                Date d = (Date) pVal;
                qry.setParameter(pPara, d, tt);
            } else {
                qry.setParameter(pPara, pVal);
            }
            //    System.out.println("Parameter " + pPara + "\tVal" + pVal);
        }
        return qry.getResultList();
    }

    public T findByField(String fieldName, String fieldValue, boolean withoutRetired) {
        List<T> lstAll = findExact(fieldName, fieldValue, true);
        if (lstAll.isEmpty()) {
            System.out.println("Null");
            return null;
        } else {
            System.out.println("Not Null " + lstAll.get(0).toString());
            return lstAll.get(0);
        }
    }

    public T findFirstBySQL(String temSQL) {
        TypedQuery<T> qry = getEntityManager().createQuery(temSQL, entityClass);
        try {
            return qry.getResultList().get(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public T findFirstBySQL(String temSQL, Map<String, Object> parameters, TemporalType tt) {
        TypedQuery<T> qry = getEntityManager().createQuery(temSQL, entityClass);
        Set s = parameters.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry m = (Map.Entry) it.next();
            Object pVal = m.getValue();
            String pPara = (String) m.getKey();
            if (pVal instanceof Date) {
                Date d = (Date) pVal;
                qry.setParameter(pPara, d, tt);
            } else {
                qry.setParameter(pPara, pVal);
            }
            //    System.out.println("Parameter " + pPara + "\tVal" + pVal);
        }

        if (!qry.getResultList().isEmpty()) {
            return qry.getResultList().get(0);
        } else {
            return null;
        }
    }

    public T findFirstBySQL(String temSQL, Map<String, Object> parameters) {
        return findFirstBySQL(temSQL, parameters, TemporalType.DATE);
    }

    public <U> List<T> testMethod(U[] a, Collection<U> all) {
        List<T> myList = new ArrayList<T>();
        return myList;
    }

    public <U> List<T> findAll(String fieldName, int searchID, boolean withoutRetired) {

//        final long userId,
//    final long contactNumber){
//
//    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//    final CriteriaQuery<TaUser> query = cb.createQuery(TaUser.class);
//    final Root<TaUser> root = query.from(TaUser.class);
//    query
//        .where(cb.and(
//            cb.equal(root.get("userId"), userId),
//            cb.equal(root.get("taContact").get("contactNumber"), contactNumber)
//        ));
//    return entityManager.createQuery(query).getSingleResult();
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery<T> cq = cb.createQuery(entityClass);
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);

        if (withoutRetired) {
            cq.where(cb.and(cb.equal(rt.get("retired"), false)),
                    (cb.equal(rt.get(fieldName).get("id"), searchID)));
        } else {
            cq.where(cb.equal(rt.get("retired"), false));
        }

        return getEntityManager().createQuery(cq).getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public Double findAggregateDbl(String strJQL) {
        Query q = getEntityManager().createQuery(strJQL);
        Double temd;
        try {
            temd = (Double) q.getSingleResult();
            if (temd == null) {
                temd = 0.0;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            temd = 0.0;
        }
        return temd;
    }

    public Long findLongByJpql(String strJQL) {
//         Ex. select max(a.bhtLong) from Admission a where a.retired=false
        Query q = getEntityManager().createQuery(strJQL);
        try {
            return (Long) q.getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0l;
        }
    }

    public List<String> findString(String strJQL) {
        Query q = getEntityManager().createQuery(strJQL);
        try {
            return q.getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    public List<String> findString(String strJQL, Map parameters, TemporalType tt) {
        TypedQuery<String> qry = getEntityManager().createQuery(strJQL, String.class);
        Set s = parameters.entrySet();
        Iterator it = s.iterator();
        while (it.hasNext()) {
            Map.Entry m = (Map.Entry) it.next();
            Object pVal = m.getValue();
            String pPara = (String) m.getKey();
            if (pVal instanceof Date) {
                Date d = (Date) pVal;
                qry.setParameter(pPara, d, tt);
            } else {
                qry.setParameter(pPara, pVal);
            }
        }
        return qry.getResultList();
    }
    
    public List<String> findString(String strJQL, Map parameters) {
        return findString(strJQL, parameters, TemporalType.DATE);
    }

    public List<Object[]> findAggregates(String temSQL, Map<String, Date> parameters) {
        TypedQuery<Object[]> qry = getEntityManager().createQuery(temSQL, Object[].class);
        Set s = parameters.entrySet();
        Iterator it = s.iterator();

        while (it.hasNext()) {
            Map.Entry m = (Map.Entry) it.next();
            Date pVal = (Date) m.getValue();
            String pPara = (String) m.getKey();
            qry.setParameter(pPara, pVal, TemporalType.DATE);
            System.out.println("Parameter " + pPara + "\tVal" + pVal);
        }

        try {
            return qry.getResultList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Double findAggregateDbl(String temSQL, Map<String, Date> parameters) {
        TypedQuery<Double> qry = getEntityManager().createQuery(temSQL, Double.class);
        Set s = parameters.entrySet();
        Iterator it = s.iterator();

        while (it.hasNext()) {
            Map.Entry m = (Map.Entry) it.next();
            Date pVal = (Date) m.getValue();
            String pPara = (String) m.getKey();
            qry.setParameter(pPara, pVal, TemporalType.DATE);
            System.out.println("Parameter " + pPara + "\tVal" + pVal);
        }

        try {
            return (Double) qry.getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return 0.0;
        }
    }
}
