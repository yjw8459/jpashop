package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

    @Id @GeneratedValue
    @Column(name = "category_id")
    private long id;

    private String name;

    /*
    *   JoinTable이 필요함. 중간 테이블로 매핑해줘야 함.
    *   Category <1:n Category_item(중간 테이블) n:1> Item  (다대다)
    *
    *   실무에서 사용하지 말 것. 관계형 DB에서 다대다는 문제가 많음.
    *
    *   다대다 관계도 연관관계의 주인을 정해야함.
    *
    * */
    @ManyToMany
    @JoinTable(name = "category_item",
               joinColumns = @JoinColumn(name = "category_id"),
               inverseJoinColumns = @JoinColumn(name = "item_id")   //catego줌ry_item 테이블에 item쪽으로 들어가는 값을 매핑해
    )
    private List<Item> items = new ArrayList<>();


    /*
    *   카테고리 구조(계층 구조로 쭉 내려감)
    *
    * */

    //부모 ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Category parent;


    //자식    ^ 위의 거울
    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    //==연관관계 메서드==//
    public void addChildCategory(Category child){
        this.child.add(child);
        child.setParent(this);
    }
}
