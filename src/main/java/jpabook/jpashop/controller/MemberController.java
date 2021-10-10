package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService membverService;

    @GetMapping("/members/new")
    public String createForm(Model model){  //model :
        model.addAttribute("memberForm", new MemberForm()); //빈 껍데기를 화면에 넘김
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result){
        //## 꿀팁
        //BindingResult를 사용하면 오류 페이지로 가지 않고 오류 내용이 result에 담기고 코드가 실행됌.
        if (result.hasErrors()){
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getCity());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        membverService.join(member);
        return "redirect:/";
    }
}
