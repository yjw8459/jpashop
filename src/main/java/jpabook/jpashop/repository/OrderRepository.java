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

    public List<Order> findAllWithItem() {
        /**
         * 컬렉션 페치조인이란?
         *  - 일대다 관계에서의 fetch join을 말한다.
         *
         * 쿼리를 간편하게 작성하는 QueryDS를 알아볼 것.
         * Order -> Member, Delivery 문제 없음. (데이터 뻥튀기 X)
         * Order -> OrderItem 문제가 발생 Order는 1개이고 OrderItem은 2개일 때 데이터는 2개가 됨.
         * DB입장에선, fetch join도 select 절에 더 데이터를 넣어주냐 정도의 차이이고, 일반적인 join과 다르지 않다.
         * 데이터가 뻥뒤기 되는 것은 JPA 입장에선 알 수 없음.
         *
         * 뻥튀기 된 데이터는 엔티티의 주소 값도 같다.
         * 이 경우 distinct(중복제거)를 넣어준다.
         * JPA에서의 distinct는 SQL에서의 distinct에서 추가적인 기능이 있다.
         * SQL에서의 distinct는 로우의 데이터 전부가 똑같아야 중복 제거가 된다.
         *
         * JPA에서의 distinct는 같은 ID값이면 중복을 제거해준다(하나를 버림).
         * 애플리케이션에 다 가져와서 한번 더 ID 값을 기준으로 한번 더 중복제거를 해줌.
         * 즉, JPA에서의 distinct는 실제 SQL로 distinct 역할을 하고 애플리케이션에서 한번 더 중복제거 작업을 수행한다.
         *
         * 중요한 또 하나
         *  - 1대 다를 fetch join하면 페이징이 안된다(쿼리가 날아가지 않음).
         *  - Hibernate가 Warning 로그를 내면서 collection fetch와 같이 정의되었다는 문구와 함께
         *    메모리에서 sorting 해버림
         *  - 데이터가 만약 10000건 이었다면, 10000건을 애플리케이션으로 다 퍼올리려서 메모리에서 소트함.
         *  - 치명적인 오류가 발생할 수 있음.
         *  - 이유는 JPA 입장에선 데이터의 갯수 측정이 안되기 때문이다.
         *
         *  - 일대다 컬렉션 fetch join은 하나만 사용할 수 있다.
         *
         *
         *  정리
         *  JPA에서의 distinct는 SQL의 distinct에서 추가적인 기능을 한다.
         *  일대다 관계에서의 fetch join을 한 경우는 페이징 처리를 절대 하면 안된다.
         *  일대다 컬렉션 fetch join은 하나만 사용할 수 있다.
         */
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class
        )
        .setFirstResult(1)  //1대 다 관계로 페이징 안됨
        .setMaxResults(100) //1대 다 관계로 페이징 안됨
        .getResultList();
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
