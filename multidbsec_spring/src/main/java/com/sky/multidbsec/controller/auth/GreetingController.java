package com.sky.multidbsec.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.sky.multidbsec.service.auth.AuthService;
import com.sky.multidbsec.service.viewobj.Greeting;
import com.sky.multidbsec.service.viewobj.PasswordUpdate;

@Controller
public class GreetingController {
  @Autowired
  AuthService authService;

  // 정보 확인
  @GetMapping("/greeting")
  public String greetingForm(Model model) {
    model.addAttribute("greeting", new Greeting());
    return "auth/greeting";
  }

  @PostMapping("/greeting")
  public String greetingSubmit(@ModelAttribute Greeting greeting, Model model) {
    model.addAttribute("greeting", greeting);
    return "authorized/result";
  }

    // 회원 등록
  @GetMapping("/regist")
  public String registForm(Model model) {
    model.addAttribute("greeting", new Greeting());
    return "public/registration";
  }

  @PostMapping("/regist")
  public String registSubmit(@ModelAttribute Greeting greeting, Model model) {
    if(authService.registUser(greeting.getUsername(),greeting.getPassword())){

    }else{

    }
    model.addAttribute("greeting", greeting);
    return "public/home";
  }

  // 패스워드 수정
  @GetMapping("/change-password")
  public String updateForm(Model model) {
    model.addAttribute("change-password", new PasswordUpdate());
    return "auth/change-password";
  }

  @PostMapping("/change-password")
  public String changeSubmit(@ModelAttribute PasswordUpdate passwordUpdate, Model model) {

    try{
      authService.updateUserPassword(passwordUpdate.getUsername(),passwordUpdate.getPassword_old(),passwordUpdate.getPassword_new());
      model.addAttribute("change-password", passwordUpdate);

    }catch (Exception e){
      System.out.println(" fail to post update password");
    }
    return "auth/hello";
  }

    // 회원 탈퇴 Withdrawal or Unsubscribe
  @GetMapping("/unsubscribe")
  public String unsubscribeForm(Model model) {
    model.addAttribute("greeting", new Greeting());
    return "auth/unsubscribe";
  }

  @PostMapping("/unsubscribe")
  public String unsubscribeSubmit(@ModelAttribute Greeting greeting, Model model) {
    try{
      authService.deleteUser();
      model.addAttribute("greeting", greeting);
      // // 로그아웃 호출
      // if (authentication != null) {
      //     new SecurityContextLogoutHandler().logout(request, response, authentication);
      // }
    }catch (Exception e){
      System.out.println(" fail to unsubscribe ");
      System.out.println(e);
    }
    return "auth/hello";
  }

}