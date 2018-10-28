package me.mupu.server.controller.everyone;

import me.mupu.server.HashPasswordEncoder;
import me.mupu.server.form.ConfirmationForm;
import me.mupu.server.service.CustomUserService;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.BenutzerRecord;
import org.jooq.generated.tables.records.PersonRecord;
import org.jooq.types.UByte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

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
    private CustomUserService userDetailsService;

    // Process confirmation link
    @GetMapping("/confirmation")
    public ModelAndView getConfirmation(@RequestParam(required = false) String token,
                                        ConfirmationForm confirmationForm) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/confirmation");
        mv.addObject("confirmationForm", confirmationForm);

        PersonRecord person = userDetailsService.findUserByConfimationToken(token);
        if (person != null) {
            mv.addObject("confirmationToken", person.getConfirmationtoken());
        } else {
            mv.addObject("invalidToken", "Oops! This is an invalid confirmation link.");
        }

        return mv;
    }

    @PostMapping("/confirmation")
    public ModelAndView processConfirmationForm(@Valid ConfirmationForm confirmationForm,
                                                BindingResult bindingResult,
                                                @RequestParam String token) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/confirmation");

        // check if confirmation token exists
        PersonRecord person = userDetailsService.findUserByConfimationToken(token);
        if (person != null) {

            if (!bindingResult.hasErrors()) {
                // add user to database
                try {
                    BenutzerRecord benutzerRecord = dslContext.insertInto(BENUTZER)
                            .columns(BENUTZER.PERSONID,
                                    BENUTZER.BENUTZERNAME,
                                    BENUTZER.PASSWORT,
                                    BENUTZER.ISENABLED,
                                    BENUTZER.RESETPASSWORDTOKEN)
                            .values(person.getPersonid(),
                                    confirmationForm.getUsername(),
                                    hashPasswordEncoder.encode(confirmationForm.getPassword()),
                                    UByte.valueOf(1),
                                    "")
                            .returning()
                            .fetchOne();

                    dslContext.insertInto(USER_ROLE)
                            .values(benutzerRecord.getBenutzerid(),
                                    dslContext.selectFrom(ROLE)
                                            .where(ROLE.NAME.eq("USER"))
                                            .fetchOne().getRoleid())
                            .execute();

                    dslContext.update(PERSON).set(PERSON.CONFIRMATIONTOKEN, "").where(PERSON.PERSONID.eq(person.getPersonid())).execute();

                    mv.addObject("successMessage", "Your account has been created!");
                } catch (Exception e) {
                    e.printStackTrace();
                    mv.addObject("confirmationToken", person.getConfirmationtoken());
                    mv.addObject("nameTaken", "Username already in use. Please provide a different name.");
                }
            } else {
                mv.addObject("confirmationToken", person.getConfirmationtoken());
            }
        } else {
            mv.addObject("invalidToken", "Oops! This is an invalid confirmation link.");
        }

        return mv;
    }

}
