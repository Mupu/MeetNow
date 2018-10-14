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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.UUID;

import static org.jooq.generated.Tables.*;

/**
 * EDIT ACCESS RIGHTS IN SecurityConfiguration
 */
@Controller
public class PublicController {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private HashPasswordEncoder hashPasswordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @RequestMapping("/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/login");

        return mv;
    }

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

    // Process confirmation link
    @GetMapping(value = {"/confirm", "/confirmation"})
    public ModelAndView getConfirmation(@RequestParam(name = "token", required = false) String token, ConfirmationForm confirmationForm) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/confirmation");
        mv.addObject("confirmationForm", confirmationForm);

        PersonRecord person = userDetailsService.findUserByConfimationToken(token);

        if (person != null) {
            mv.addObject("confirmationToken", person.getToken());
        } else {
            mv.addObject("invalidToken", "Oops! This is an invalid confirmation link.");
        }

        return mv;
    }

    @PostMapping(value = {"/confirm", "/confirmation"})
    public ModelAndView processConfirmationForm(@Valid ConfirmationForm confirmationForm, BindingResult bindingResult,
                                                @RequestParam(name = "token", required = false) String token) {
        ModelAndView mv = new ModelAndView();

        mv.setViewName("everyone/confirmation");
        PersonRecord person = userDetailsService.findUserByConfimationToken(token);
        if (person != null) {


            if (!bindingResult.hasErrors()) {
                try {
                    BenutzerRecord benutzerRecord = dslContext.insertInto(BENUTZER)
                            .columns(BENUTZER.PERSONID,
                                    BENUTZER.BENUTZERNAME,
                                    BENUTZER.PASSWORT,
                                    BENUTZER.ISENABLED)
                            .values(person.getPersonid(),
                                    confirmationForm.getUsername(),
                                    hashPasswordEncoder.encode(confirmationForm.getPassword()),
                                    UByte.valueOf(1))
                            .returning()
                            .fetchOne();

                    dslContext.insertInto(USER_ROLE)
                            .values(benutzerRecord.getBenutzerid(),
                                    dslContext.selectFrom(ROLE)
                                            .where(ROLE.NAME.eq("USER"))
                                            .fetchOne().getRoleid())
                            .execute();

                    dslContext.update(PERSON).set(PERSON.TOKEN, "").where(PERSON.PERSONID.eq(person.getPersonid())).execute();

                    mv.addObject("successMessage", "Your account has been created!");
                } catch (Exception e) {
                    e.printStackTrace();
                    mv.addObject("confirmationToken", person.getToken());
                    mv.addObject("nameTaken", "Username already in use. Please provide a different name.");
                }
            } else {
                mv.addObject("confirmationToken", person.getToken());
            }
        } else {
            mv.addObject("invalidToken", "Oops! This is an invalid confirmation link.");

        }

        return mv;
    }

    @RequestMapping(value = {"/", "/home"})
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/home");

        return mv;
    }

}
