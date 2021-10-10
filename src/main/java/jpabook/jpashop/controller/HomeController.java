package jpabook.jpashop.controller;


import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller 
@Slf4j
public class HomeController {

    //Slf4j를 사용하고 이렇게 작성하지 않고 어노테이션을 사용하면 같은 의미
//    Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping("/")
    public String home(){
        log.info("home Controller");
        return "home";//Command 버튼을 누르고 클릭 시 해당 뷰 페이지로 이동
    }

}
