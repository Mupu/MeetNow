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
public class ConfirmationController {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private HashPasswordEncoder hashPasswordEncoder;

    @Autowired
    private CustomUserDetailsService userDetailsService;

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

}
