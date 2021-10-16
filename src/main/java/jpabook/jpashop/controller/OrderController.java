package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private  final ItemService itemService;

    //order Form
    @GetMapping("/order")
    public String createForm(Model model){

        List<Member> members = memberService.findMembers(); //모든 멤버
        List<Item> items = itemService.findItems();         //모든 아이템

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";   //모든 멤버와 모든 아이템을 오더폼으로 넘김.
    }

    @PostMapping("/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId,
                        @RequestParam("count") int count){
        /**
         * 컨트롤러에선 가급적 필요한 데이터만 받아서 처리하도록 한다.
         * 데이터가 많을 경우는 DTO를 사용하여 데이터를 받도록 한다. (엔티티 사용 X)
         *
         */


        orderService.order(memberId, itemId, count);    //어떤 고객이 어떤 상품을 몇개 주문하는지
        return "redirect:/orders";
    }
}
