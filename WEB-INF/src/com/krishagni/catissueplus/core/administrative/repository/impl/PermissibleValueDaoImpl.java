package com.krishagni.catissueplus.core.administrative.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import com.krishagni.catissueplus.core.administrative.domain.PermissibleValue;
import com.krishagni.catissueplus.core.administrative.events.ListPvCriteria;
import com.krishagni.catissueplus.core.administrative.repository.PermissibleValueDao;
import com.krishagni.catissueplus.core.common.repository.AbstractDao;
import com.krishagni.catissueplus.core.common.util.Status;


public class PermissibleValueDaoImpl extends AbstractDao<PermissibleValue> implements PermissibleValueDao {

	@Override
	public Class<PermissibleValue> getType() {
		return PermissibleValue.class;
	}

	@Override
	public PermissibleValue getById(Long id) {
		return getCurrentSession().get(PermissibleValue.class, id);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<PermissibleValue> getPvs(ListPvCriteria crit) {
		return getCurrentSession().createQuery(getPvQuery(crit))
			.setFirstResult(crit.startAt())
			.setMaxResults(crit.maxResults())
			.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public PermissibleValue getByValue(String attribute, String value) {
		List<PermissibleValue> pvs = getSessionFactory().getCurrentSession()
				.getNamedQuery(GET_BY_VALUE)
				.setString("attribute", attribute)
				.setString("value", value)
				.list();
		
		return CollectionUtils.isEmpty(pvs) ? null : pvs.iterator().next();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PermissibleValue> getByPropertyKeyValue(String attribute, String propName, String propValue) {
		Object[] bindValues = { propName, propValue, propValue + "^%", "%^" + propValue + "^%", "%^" + propValue};
		Type[] bindTypes = {StringType.INSTANCE, StringType.INSTANCE, StringType.INSTANCE, StringType.INSTANCE, StringType.INSTANCE};
		return (List<PermissibleValue>) getCurrentSession().createCriteria(PermissibleValue.class, "pv")
			.add(Restrictions.eq("pv.attribute", attribute))
			.add(Restrictions.sqlRestriction(PV_PROP_VALUE_MATCH_SQL, bindValues, bindTypes))
			.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PermissibleValue> getSpecimenClasses() {
		return getCurrentSession().getNamedQuery(GET_SPECIMEN_CLASSES).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSpecimenTypes(Collection<String> specimenClasses) {
		return getCurrentSession().getNamedQuery(GET_SPECIMEN_TYPES)
			.setParameterList("specimenClasses", specimenClasses)
			.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getSpecimenClass(String type) {
		List<String> classes = getCurrentSession().getNamedQuery(GET_SPECIMEN_CLASS)
			.setString("type", type)
			.list();
		return classes.size() == 1 ? classes.get(0) : null;
	}

	@Override
	public PermissibleValue getPv(String attribute, String value) {
		return getPv(attribute, value, false);
	}

	@Override
	public PermissibleValue getPv(String attribute, String value, boolean leafNode) {
		List<PermissibleValue> pvs = getPvs(attribute, null, Collections.singleton(value), leafNode);
		return pvs != null && pvs.size() > 0 ? pvs.iterator().next() : null;
	}

	@Override
	public PermissibleValue getPv(String attribute, String parentValue, String value) {
		List<PermissibleValue> pvs = getPvs(attribute, parentValue, Collections.singleton(value), false);
		return pvs != null && pvs.size() > 0 ? pvs.iterator().next() : null;
	}

	@Override
	public List<PermissibleValue> getPvs(String attribute, Collection<String> values) {
		return getPvs(attribute, null, values, false);
	}

	@Override
	public List<PermissibleValue> getPvs(String attribute, String parentValue, Collection<String> values, boolean leafNode) {
		Criteria query = getCurrentSession().createCriteria(PermissibleValue.class, "pv")
			.add(Restrictions.eq("pv.attribute", attribute))
			.add(Restrictions.in("pv.value", values));

		if (StringUtils.isNotBlank(parentValue)) {
			query.createAlias("pv.parent", "ppv")
				.add(Restrictions.eq("ppv.attribute", attribute))
				.add(Restrictions.eq("ppv.value", parentValue));
		}

		if (leafNode) {
			query.createAlias("children", "c", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.isNull("c.id"));
		}

		return query.list();
	}

	@Override
	public boolean exists(String attribute, Collection<String> values) {
		return exists(attribute, values, false);
	}

	@Override
	public boolean exists(String attribute, String parentValue, Collection<String> values) {
		Number count = (Number)sessionFactory.getCurrentSession().createCriteria(PermissibleValue.class)
				.createAlias("parent", "ppv")
				.add(Restrictions.eq("ppv.attribute", attribute))
				.add(Restrictions.eq("ppv.value", parentValue))
				.add(Restrictions.in("value", values))
				.setProjection(Projections.count("id"))
				.uniqueResult();
		return count.intValue() == values.size();
	}

	public boolean exists(String attribute, Collection<String> values, boolean leafLevelCheck) {
		Criteria query = sessionFactory.getCurrentSession()
				.createCriteria(PermissibleValue.class)
				.add(Restrictions.eq("attribute", attribute))
				.add(Restrictions.in("value", values))
				.setProjection(Projections.count("id"));
		
		if (leafLevelCheck) {
			query.createAlias("children", "c", JoinType.LEFT_OUTER_JOIN)
				.add(Restrictions.isNull("c.id"));
		}
		
		Number count = (Number)query.uniqueResult();				
		return count.intValue() == values.size();	
	}
	
	@Override
	public boolean exists(String attribute, int depth, Collection<String> values) {
		return exists(attribute, depth, values, false);
	}
	
	@Override
	public boolean exists(String attribute, int depth, Collection<String> values, boolean anyLevel) {
		Criteria query = sessionFactory.getCurrentSession().createCriteria(PermissibleValue.class)
				.add(Restrictions.in("value", values))
				.setProjection(Projections.count("id"));
		
		for (int i = 1; i <= depth; ++i) {			
			if (i == 1) {
				query.createAlias("parent", "pv" + i, anyLevel ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN);
			} else {
				query.createAlias("pv" + (i - 1) + ".parent", "pv" + i, anyLevel ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN);
			}			
		}
		
		Disjunction attrCond = Restrictions.disjunction();
		attrCond.add(Restrictions.eq("pv" + depth + ".attribute", attribute));
		if (anyLevel) {
			for (int i = depth - 1; i >= 1; i--) {
				attrCond.add(Restrictions.eq("pv" + i + ".attribute", attribute));
			}
			
			attrCond.add(Restrictions.eq("attribute", attribute));
		}
		
		Number count = (Number)query.add(attrCond).uniqueResult();
		return count.intValue() == values.size();
	}
	
	private CriteriaQuery<PermissibleValue> getPvQuery(ListPvCriteria crit) {
		CriteriaBuilder builder            = getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<PermissibleValue> cr = builder.createQuery(PermissibleValue.class);
		Root<PermissibleValue> pv          = cr.from(PermissibleValue.class);

		List<Predicate> predicates = new ArrayList<>();
		if (crit.values() != null) {
			List<String> values = crit.values().stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
			if (!values.isEmpty()) {
				predicates.add(pv.get("value").in(values));
			}
		}

		Map<String, Join> joins = new HashMap<>();
		if (StringUtils.isNotBlank(crit.parentAttribute()) || StringUtils.isNotBlank(crit.parentValue())) {
			Join<PermissibleValue, PermissibleValue> parent = pv.join("parent");
			joins.put("p", parent);
		}

		if (StringUtils.isNotBlank(crit.attribute())) {
			predicates.add(builder.equal(pv.get("attribute"), crit.attribute()));
		} else if (StringUtils.isNotBlank(crit.parentAttribute())) {
			predicates.add(builder.equal(joins.get("p").get("attribute"), crit.parentAttribute()));
		}

		if (StringUtils.isNotBlank(crit.parentValue())) {
			predicates.add(builder.equal(joins.get("p").get("value"), crit.parentValue()));
		}

		if (crit.includeOnlyLeafValue()) {
			SetJoin<PermissibleValue, PermissibleValue> children = pv.joinSet("children", javax.persistence.criteria.JoinType.LEFT);
			predicates.add(builder.isNull(children.get("id")));
		}

		if (crit.includeOnlyRootValue()) {
			Join<PermissibleValue, PermissibleValue> rootPv = pv.join("parent", javax.persistence.criteria.JoinType.LEFT);
			predicates.add(builder.isNull(rootPv.get("id")));
		}

		if (StringUtils.isBlank(crit.activityStatus())) {
			predicates.add(builder.equal(pv.get("activityStatus"), Status.ACTIVITY_STATUS_ACTIVE.getStatus()));
		} else if (!crit.activityStatus().equalsIgnoreCase("all")) {
			predicates.add(builder.equal(pv.get("activityStatus"), crit.activityStatus()));
		}

		List<javax.persistence.criteria.Order> orderList = new ArrayList<>();
		if (StringUtils.isNotBlank(crit.query())) {
			predicates.add(
				builder.or(
					builder.like(builder.lower(pv.get("value")), "%" + crit.query().toLowerCase() + "%"),
					builder.like(builder.lower(pv.get("conceptCode")), "%" + crit.query().toLowerCase() + "%")
				)
			);

			orderList.add(builder.asc(builder.locate(builder.lower(pv.get("value")), crit.query().toLowerCase())));
		}

		orderList.add(builder.asc(pv.get("sortOrder")));
		orderList.add(builder.asc(pv.get("value")));
		cr.select(pv).where(predicates.toArray(new Predicate[0])).orderBy(orderList);
		return cr;
	}

	private static final String FQN = PermissibleValue.class.getName();

	private static final String GET_BY_VALUE = FQN + ".getByValue";

	private static final String GET_SPECIMEN_CLASSES = FQN + ".getSpecimenClasses";

	private static final String GET_SPECIMEN_TYPES = FQN + ".getSpecimenTypes";

	private static final String GET_SPECIMEN_CLASS = FQN + ".getSpecimenClass";

	private static final String PV_PROP_VALUE_MATCH_SQL =
		"exists (" +
		"  select " +
		"    pv_id " +
		"  from" +
		"    os_pv_props " +
		"  where " +
		"    pv_id = {alias}.identifier and " +
		"    name = ? and " +
		"    (value = ? or value like ? or value like ? or value like ?)" +
		")";
}
