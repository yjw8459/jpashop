package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository     //Component Scan으로 자동으로 스프링 빈으로 관리됨
@RequiredArgsConstructor
public class MemberRepository {
/*
    //JPA 표준 어노테이션
    @PersistenceContext //PersistenceContext 어노테이션이 있을 경우 EntityManager를 자동 주입해줌
    @Autowired          //스프링 부트를 사용하면 @PersistenceContext -> @Autowired로 가능하다.
    private EntityManager em;   // 스프링이 EntityManager를 만들어서 자동으로 주입함.
    */

    private final EntityManager em; // @Autowired, @PersistenceContext 없어도 됨. @RequiredArgsConstructor때문

    /*
        엔티티 매니저 팩토리
        @PersistenceUnit
        private EntityManagerFactory emf;
        */
    public void save(Member member){
        //persist를 하면 영속성 컨텍스트에 Member 객체(엔티티)를 넣음
        //트랜잭션이 커밋되는 시점에 DB에 반영(insert 쿼리 날아감)
        em.persist(member);
    }

    public Member findOne(Long id){
        //단 건 조회, 첫 번째로 타입, 두 번째로 PK
        return em.find(Member.class, id);
    }


/*  "select m from Member m" :JPQL
    SQL : 테이블 대상
    JPQL : 엔티티 객체 대상
 */
    public List<Member> findAll(){
        return em.createQuery("select m from Member m", Member.class)
            .getResultList();
    }

    public List<Member> findbyName(String name){
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
