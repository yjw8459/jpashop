package jpabook.jpashop.repository;

import jpabook.jpashop.api.OrderSimpleApiController;
import jpabook.jpashop.domain.Order;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

//주문 레포지토리
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

    public List<Order> findAllWithMemberDelivery() {
        /**
         * Order를 조회하는데 member랑 delivery를 SQL상으로 조인해서 한 번에 다 가져옴.
         * LAZY를 무시하고 프록시도 아닌 진짜 엔티티를 가져옴.
         * join fetch : JPA에만 있는 키워드이다. 실제 spl에는 없음.
         *
         * ****fetch join은 꼭 100% 이해하고 사용할 것.
         *
         * JPA 성능 문제의 90는 n+1으로 인해 발생. (Order를 조회할 때 Order안에 연관되어있는 엔티티들을 전파하면서 조회하는 것.)
         * 쿼리 조회 결과는 order + member + delivery로 데이터가 길게 조회되고
         * JPA가 적당하게 잘라서 매핑해줌.
         */
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    public List<OrderSimpleQueryDto> findOrderDtos() {
        /**
         * 가급적 Controller > Service > Repository 한 방향으로 흘러야한다.
         * 엔티티나 ValueObject(Embedable)만 반환할 수 있음.(JPA)
         * DTO로 반환하려면 new 로 생성해서 파라미터를 다 넣어줘야함.
         */
        return em.createQuery(
                    "select o from Order o" +
                            " join o.member m" +
                            " join o.delivery d", OrderSimpleQueryDto.class
        ).getResultList();
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
