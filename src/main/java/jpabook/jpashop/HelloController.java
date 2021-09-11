package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model){
        //Model : 데이터를 담는 그릇,
        model.addAttribute("data", "hello!!!");
        return "hello";//관례상 ViewPage의 이름
    }
}
