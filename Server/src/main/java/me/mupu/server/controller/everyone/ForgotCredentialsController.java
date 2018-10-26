package me.mupu.server.controller.everyone;

import me.mupu.server.form.ConfirmationForm;
import me.mupu.server.service.EmailService;
import me.mupu.server.service.RegistrationService;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.BenutzerRecord;
import org.jooq.generated.tables.records.PersonRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import java.util.UUID;

import static org.jooq.generated.Tables.*;

@Controller
@RequestMapping("/forgotCredentials")
public class ForgotCredentialsController {

    @Autowired
    DSLContext dslContext;

    @Autowired
    EmailService emailService;

    @GetMapping("/username")
    public ModelAndView getUsername() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/forgotUsername");

        return mv;
    }

    @PostMapping("/username")
    public ModelAndView sendUsernameMail(@RequestParam(defaultValue = "") String email,
                                    HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/forgotUsername");

        if (!email.equals("")) {
            PersonRecord person = dslContext.selectFrom(PERSON).where(PERSON.EMAIL.eq(email)).fetchOne();
            if (person != null) {
                if (sendUsernameMail(person, request)) {
                    mv.addObject("success", "Email has been sent. Please check your mails: ");
                    mv.addObject("provider", person.getEmail().split("@")[1]);
                    return mv;
                }
            }
        }

        mv.addObject("error", "Wrong Email");

        return mv;
    }

    private boolean sendUsernameMail(PersonRecord person, HttpServletRequest request) {
        if (person.getEmail().equals(""))
            return false;

        BenutzerRecord benutzer = dslContext.selectFrom(BENUTZER).where(BENUTZER.PERSONID.eq(person.getPersonid())).fetchOne();

        if (benutzer == null)
            return false;

        String appUrl = request.getScheme() + "://" + request.getServerName();

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(person.getEmail());
        mail.setSubject("Forgotten Username");
        mail.setText("Hello " + person.getVorname() + " " + person.getNachname() + ",\n"
                    + "your username is: " + benutzer.getBenutzername() + ".\n\n"
                    + "If you don't remember your password, you can reset it here: \n"
                    + appUrl + "/forgotCredentials/password");
        mail.setFrom("noreply@" + request.getServerName());

        emailService.sendEmail(mail);
        return true;
    }

    @GetMapping("/password")
    public ModelAndView getPassword() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/forgotPassword");

        return mv;
    }
    // todo reset passwort muss noch hinzugefügt werden
    @PostMapping("/password")
    public ModelAndView sendPasswordResetMail(@RequestParam(defaultValue = "") String email,
                                    HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/forgotPassword");

        if (!email.equals("")) {
            PersonRecord person = dslContext.selectFrom(PERSON).where(PERSON.EMAIL.eq(email)).fetchOne();

            if (person != null) {
                if (person.getResetpasswordtoken().equals("")) {
                    person = dslContext.update(PERSON)
                            .set(PERSON.RESETPASSWORDTOKEN, UUID.randomUUID().toString())
                            .where(PERSON.PERSONID.eq(person.getPersonid()))
                            .returning()
                            .fetchOne();
                }

                if (sendPasswordResetMail(person, request)) {
                    mv.addObject("success", "Email has been sent. Please check your mails: ");
                    mv.addObject("provider", person.getEmail().split("@")[1]);
                    return mv;
                }
            }
        }

        mv.addObject("error", "Wrong Email");

        return mv;
    }

    private boolean sendPasswordResetMail(PersonRecord person, HttpServletRequest request) {
        if (person.getEmail().equals(""))
            return false;

        BenutzerRecord benutzer = dslContext.selectFrom(BENUTZER).where(BENUTZER.PERSONID.eq(person.getPersonid())).fetchOne();

        if (benutzer == null)
            return false;

        String appUrl = request.getScheme() + "://" + request.getServerName();

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(person.getEmail());
        mail.setSubject("Reset Forgotten Password");
        mail.setText("Hello " + person.getVorname() + " " + person.getNachname() + ",\n"
                + "you can reset your password here: \n"
                + appUrl + "/" + person.getResetpasswordtoken());
        mail.setFrom("noreply@" + request.getServerName());

        emailService.sendEmail(mail);
        return true;
    }

}
