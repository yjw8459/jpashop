package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch){
//        em.createQuery("select o from Order o join o.member m", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())   //파라미터 바인딩
//                .setParameter("name", orderSearch.getMemberName())      //파라미터 바인딩
//                .setMaxResults(1000)                                      // 페이징 옵션 : 1000개까지(최대 1000건)
//                .getResultList();
        //join일 경우 join으로 ! left join일 경우 left join
        //: 파라미터 바인딩

        /**
         * JPA Criteria 방식
         * 동적 쿼리 생성에 메리트가 있으나, 복잡성으로 인해 잘 사용하지 않는다.
         */
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();   //조건 목록

        //주문 상테 검색
        if ( orderSearch.getOrderStatus() != null ){
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);   //조건 추가
        }
        //회원 이름 검색
        if ( StringUtils.hasText(orderSearch.getMemberName()) ){
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);     //조건 추가
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()]))); //toArray : 컬렉션 List -> 배열[]
        /**
         * criteria.toArray(new Predicate[criteria.size()])
         * -> Predicate[] = criteria.toArray(new Predicate[2])
         * -> Predicate[2]
         */
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();

    }

    /**
     * 그 밖에 String 쿼리를 직접 조립해서 하는 방법, JPA Criteria 방법이 있음.
     */

//    public List<Order> findAll(OrderSearch orderSearch){
//        em.createQuery("select o from Order o join o.member m" +
//                " where o.status = :status" +
//                "   and m.name like :name", Order.class)
//                .setParameter("status", orderSearch.getOrderStatus())   //파라미터 바인딩
//                .setParameter("name", orderSearch.getMemberName())      //파라미터 바인딩
////                .setFirstResult(100)                                      // 페이징 옵션 : 100부터
//                .setMaxResults(1000)                                      // 페이징 옵션 : 1000개까지(최대 1000건)
//                .getResultList();
//        //join일 경우 join으로 ! left join일 경우 left join
//        //: 파라미터 바인딩
//
//    }

}
