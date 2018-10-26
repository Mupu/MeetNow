package me.mupu.server.controller.everyone;

import me.mupu.server.service.RegistrationService;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.PersonRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static org.jooq.generated.Tables.*;

@Controller
public class ResendConfirmationEmailController {

    @Autowired
    DSLContext dslContext;

    @Autowired
    RegistrationService registrationService;

    @GetMapping("/resendConfirmationEmail")
    public ModelAndView getConfirmation() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/resendConfirmationEmail");

        return mv;
    }

    @PostMapping("/resendConfirmationEmail")
    public ModelAndView sendEmail(@RequestParam(defaultValue = "") String email,
                                  HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/resendConfirmationEmail");

        if (!email.equals("")) {
            PersonRecord person = dslContext.selectFrom(PERSON).where(PERSON.EMAIL.eq(email)).fetchOne();
            if (person != null) {
                if (registrationService.sendConfirmationMail(person, request)) {
                    mv.addObject("success", "Email has been sent. Please check your mails: ");
                    mv.addObject("provider", person.getEmail().split("@")[1]);
                    return mv;
                }
            }
        }

        mv.addObject("error", "Registration has already been completed or Email is wrong");

        return mv;
    }

}
