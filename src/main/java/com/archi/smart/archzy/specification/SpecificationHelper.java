package com.archi.smart.archzy.specification;

import com.archi.smart.archzy.bean.BusinessObject;
import com.archi.smart.archzy.util.ListUtil;
import com.archi.smart.archzy.util.NumberUtil;
import com.archi.smart.archzy.criteria.BaseCriteria;
import com.archi.smart.archzy.util.StringUtil;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SpecificationHelper<Criteria extends BaseCriteria, H extends BusinessObject> implements Specification<H> {

    protected Criteria criteria;
    protected boolean distinct;
    protected Root<H> root;
    protected CriteriaQuery<?> query;
    protected CriteriaBuilder builder;
    protected List<Predicate> predicates;

    public void attachSearchElement(Root<H> root, CriteriaQuery<?> query, CriteriaBuilder builder, List<Predicate> predicates) {
        this.root = root;
        this.query = query;
        this.builder = builder;
        this.predicates = predicates;
    }

    public void addPredicate(String name, Long value) {
        if (NumberUtil.isPostif(value)) {
            predicates.add(builder.equal(root.<Long>get(name), value));
        }
    }


    public void addPredicateIn(String name, List<Long> values) {
        List<Long> results = null;
        if (ListUtil.isNotEmpty(values)) {
            results = values.stream().filter(NumberUtil::isPostif).collect(Collectors.toList());
        }
        if (ListUtil.isNotEmpty(results) && results.size() == 1) {
            addPredicate(name, results.get(0));
        } else if (ListUtil.isNotEmpty(results) && results.size() > 1) {
            predicates.add(root.<Long>get(name).in(values));
        }
    }

    public void addPredicateDouble(String name, String value) {
        if (StringUtil.isNotEmpty(value)) {
            predicates.add(builder.equal(root.<Double>get(name), Double.parseDouble(value)));
        }
    }
    public void addPredicateBigDecimal(String name, String value) {
        if (StringUtil.isNotEmpty(value)) {
            predicates.add(builder.equal(root.<BigDecimal>get(name), new BigDecimal(value)));
        }
    }
    public void addPredicateId(String name, Long value, Long valueNot, List<Long> idsIn, List<Long> idsNotIn) {
        addPredicate(name, value);
        addPredicateNot(name, valueNot);
        addPredicateIn(name, idsIn);
        addPredicateNotIn(name, idsNotIn);
    }

    public void addPredicateNot(String name, Long value) {
        if (NumberUtil.isPostif(value)) {
            predicates.add(builder.notEqual(root.<Boolean>get(name), value));
        }
    }

    public void addPredicate(String name, String value, String valueLike) {
        if (StringUtil.isNotEmpty(valueLike)) {
            predicates.add(builder.like(builder.lower(root.get(name)), "%" + valueLike + "%"));

        } else if (StringUtil.isNotEmpty(value)) {
            predicates.add(builder.equal(root.<String>get(name), value));
        }
    }

    public void addPredicate(String name, String value) {
        if (StringUtil.isNotEmpty(value)) {
            predicates.add(builder.equal(root.<String>get(name), value));
        }
    }

    public void addPredicate(String name, LocalDateTime value) {
        if (value != null) {
            predicates.add(builder.equal(root.<LocalDateTime>get(name), value));
        }
    }

    public void addPredicateInt(String name, String value) {
        if (StringUtil.isNotEmpty(value)) {
            predicates.add(builder.equal(root.<Integer>get(name), Integer.parseInt(value)));
        }
    }

    public void addPredicateLong(String name, String value) {
        if (StringUtil.isNotEmpty(value)) {
            predicates.add(builder.equal(root.<Long>get(name), Long.parseLong(value)));
        }
    }

    public void addPredicateBool(String name, String value) {
        if (StringUtil.isNotEmpty(value)) {
            predicates.add(builder.equal(root.<Integer>get(name), Boolean.valueOf(value)));
        }
    }

    public void addPredicate(String name, LocalDateTime value, LocalDateTime valueMin, LocalDateTime valueMax) {
        addPredicate(name, value);
        addPredicate(name, valueMin, valueMax);
    }

    public void addPredicate(String name, LocalDateTime valueMin, LocalDateTime valueMax) {
        if (valueMin != null && valueMax != null) {
            predicates.add(builder.between(root.get(name), valueMin, valueMax));
        } else if (valueMin != null) {
            predicates.add(builder.greaterThan(root.get(name), valueMin));
        } else if (valueMax != null) {
            predicates.add(builder.lessThan(root.get(name), valueMax));
        }
    }

    public void addPredicateNotIn(String name, List<Long> values) {
        if (ListUtil.isNotEmpty(values)) {
            predicates.add(builder.not(root.<Long>get(name).in(values)));
        }
    }

    public void addOrderAndFilter() {
        addFilterConstraint();
        addOrderByAscConstraint();
        addOrderByDescConstraint();
    }

    private void addOrderByDescConstraint() {
        if (StringUtil.isNotEmpty(criteria.getOrderByDesc())) {
            List<Order> orderList = new ArrayList();
            for (int i = 0; i < criteria.getOrderByDesc().length; i++) {
                orderList.add(builder.desc(root.get(criteria.getOrderByDesc()[i])));
            }
            query.orderBy(orderList);
        }
    }

    private void addOrderByAscConstraint() {
        if (StringUtil.isNotEmpty(criteria.getOrderByAsc())) {
            List<Order> orderList = new ArrayList();
            for (int i = 0; i < criteria.getOrderByAsc().length; i++) {
                orderList.add(builder.asc(root.get(criteria.getOrderByAsc()[i])));
            }
            query.orderBy(orderList);
        }
    }

    private void addFilterConstraint() {
        if (StringUtil.isNotEmpty(criteria.getFilterName()) && StringUtil.isNotEmpty(criteria.getFilterWord())) {
            Expression<String> path = root.get(criteria.getFilterName());
            Expression<String> lower = builder.lower(path);
            predicates.add(builder.like(lower, "%" + criteria.getFilterWord().toLowerCase() + "%"));
        }
    }

    public Predicate getResult() {
        if (distinct)
            query.distinct(true);
        return builder.and(predicates.toArray(new Predicate[0]));
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

}
