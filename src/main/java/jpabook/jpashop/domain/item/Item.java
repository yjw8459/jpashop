package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


/*
 *   상속관계 매핑이기 때문에 상속관계 전략을 지정해야함 전략은 부모 클래스에 지정해줘야함.
 *   Inheritance :   JOINED: 가장 정규화된 스타일로 하는 것.
 *                   SINGLE_TABLE : 한 테이블에 다 넣는 방식, (현재 전략 )
 *                   TABLE_PER_CLASS : Book, Album, Movie 3개의 테이블이 나오는 방식
 *
 * */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter @Setter
public abstract class Item {    //상속 관계 매핑을 위한 Abstract Class

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직 ==//
    /*
        Stock 증가
     */
    public void addStock(int quantity){ this.stockQuantity += quantity; }

    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if ( restStock > 0 ){
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }


}
