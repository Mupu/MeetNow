package me.mupu.server.controller.everyone;

import me.mupu.server.form.RegistrationForm;
import me.mupu.server.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * EDIT ACCESS RIGHTS IN SecurityConfiguration
 */
@Controller
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;


    @GetMapping("/registration")
    public ModelAndView registration(RegistrationForm registrationForm) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/registration");
        mv.addObject("registrationForm", registrationForm);

        return mv;
    }

    @PostMapping("/registration")
    public ModelAndView acceptForm(@Valid RegistrationForm registrationForm, BindingResult bindingResult, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/registration");

        if (!bindingResult.hasErrors()) {
            if (registrationService.registerUser(registrationForm, request) != null) {
                mv.addObject("success", "Please confirm your SAUFemail: ");
                mv.addObject("provider", registrationForm.getEmail().split("@")[1]);
            } else
                mv.addObject("error", "error");
        }

        return mv;
    }

}
