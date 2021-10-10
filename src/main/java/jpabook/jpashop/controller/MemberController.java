package jpabook.jpashop.controller;

import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService membverService;

    @GetMapping("/members/new")
    public String createForm(Model model){  //model :
        model.addAttribute("memberForm", new MemberForm()); //빈 껍데기를 화면에 넘김
        return "members/createMemberForm";
    }
}
