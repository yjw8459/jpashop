package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

/**
 * 총 주문 2개
 * *userA
 *  * JPA1 BOOK
 *  * JPA2 BOOK
 *
 * *userB
 *  * SPRING BOOK
 *  * SPRING BOOK
 *
 */
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    /**
     * 서버 띄우면 컴포넌트 스캔을 읽으면서
     * 스프링 빈이 다 엮이고 마지막에 PostConstruct 스프링이 호출
     *
     */
    @PostConstruct
    public void init(){ //애플리케이션 실행 시점에 실행하기 위함.
        initService.dbInit1();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{

        private final EntityManager em;
        public void dbInit1(){
            Member member = createMember("userA", "서울", "1", "1111");
            em.persist(member); //member를 영속상태로 만들어줌


            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);//book1을 10000에 구매
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 1);//book2을 20000에 구매

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);//...문법으로 orderItem1, orderItem2
            //...문법은 List는 사용X
            em.persist(order);      //order 영속화
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());   //고객의 주소
            return delivery;
        }

        private Book createBook(String name, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(name);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity); //재고 수량
            return book1;
        }

        public void dbInit2(){
            Member member = createMember("userB", "진주", "2", "2222");
            em.persist(member); //member를 영속상태로 만들어줌


            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);//book1을 10000에 구매
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 1);//book2을 20000에 구매

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);//...문법으로 orderItem1, orderItem2
            //...문법은 List는 사용X
            em.persist(order);      //order 영속화
        }

        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

    }
}
