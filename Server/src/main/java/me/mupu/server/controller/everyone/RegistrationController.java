package me.mupu.server.controller.everyone;

import me.mupu.server.HashPasswordEncoder;
import me.mupu.server.form.ConfirmationForm;
import me.mupu.server.form.RegistrationForm;
import me.mupu.server.service.CustomUserDetailsService;
import me.mupu.server.service.EmailService;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.BenutzerRecord;
import org.jooq.generated.tables.records.PersonRecord;
import org.jooq.types.UByte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

import static org.jooq.generated.Tables.*;

/**
 * EDIT ACCESS RIGHTS IN SecurityConfiguration
 */
@Controller
public class RegistrationController {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private EmailService emailService;


    @GetMapping(value = {"/registration", "/register"})
    public ModelAndView registration(RegistrationForm registrationForm) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/registration");
        mv.addObject("registrationForm", registrationForm);

        return mv;
    }

    @PostMapping(value = {"/registration", "/register"})
    public ModelAndView acceptForm(@Valid RegistrationForm registrationForm, BindingResult bindingResult, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/registration");

        if (!bindingResult.hasErrors()) {
            PersonRecord person;
            try {
                person = dslContext.insertInto(PERSON)
                        .columns(PERSON.VORNAME,
                                PERSON.NACHNAME,
                                PERSON.EMAIL,
                                PERSON.TOKEN)
                        .values(registrationForm.getVorname(),
                                registrationForm.getNachname(),
                                registrationForm.getEmail(),
                                UUID.randomUUID().toString())
                        .returning().fetchOne();

                String appUrl = request.getScheme() + "://" + request.getServerName();

                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setTo(person.getEmail());
                mail.setSubject("Registration Confirmation");
                mail.setText("To confirm your e-mail address, please click the link below:\n"
                        + appUrl + "/confirmation?token=" + person.getToken());
                mail.setFrom("noreply@domain.com");

                emailService.sendEmail(mail);

                mv.addObject("success", "Please confirm your email: ");
                mv.addObject("provider", registrationForm.getEmail().split("@")[1]);
            } catch (Exception e) {


                e.printStackTrace();
                mv.addObject("error", "error");
            }
        }

        return mv;
    }

}
