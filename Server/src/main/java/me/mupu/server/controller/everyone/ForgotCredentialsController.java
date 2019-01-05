package me.mupu.server.controller.everyone;

import me.mupu.server.HashPasswordEncoder;
import me.mupu.server.form.ConfirmationForm;
import me.mupu.server.form.ResetPasswordForm;
import me.mupu.server.service.EmailService;
import me.mupu.server.service.RegistrationService;
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

@Controller
@RequestMapping("/forgotCredentials")
public class ForgotCredentialsController {

    @Autowired
    DSLContext dslContext;

    @Autowired
    EmailService emailService;

    @Autowired
    HashPasswordEncoder hashPasswordEncoder;

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

    @PostMapping("/password")
    public ModelAndView sendPasswordResetMail(@RequestParam(defaultValue = "") String email,
                                              HttpServletRequest request) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/forgotPassword");

        if (!email.equals("")) {
            PersonRecord person = dslContext.selectFrom(PERSON).where(PERSON.EMAIL.eq(email)).fetchOne();
            if (person != null) {
                BenutzerRecord benutzerRecord = dslContext.selectFrom(BENUTZER).where(BENUTZER.PERSONID.eq(person.getPersonid())).fetchOne();

                if (benutzerRecord.getResetpasswordtoken().equals("")) {
                    dslContext.update(BENUTZER)
                            .set(BENUTZER.RESETPASSWORDTOKEN, UUID.randomUUID().toString())
                            .where(BENUTZER.PERSONID.eq(person.getPersonid()))
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
                + appUrl + "/forgotCredentials/resetPassword?token=" + benutzer.getResetpasswordtoken());
        mail.setFrom("noreply@" + request.getServerName());

        emailService.sendEmail(mail);
        return true;
    }

    // Process resetPassword link
    @GetMapping("/resetPassword")
    public ModelAndView getResetPassword(@RequestParam(required = false) String token,
                                         ResetPasswordForm resetPasswordForm) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/resetPassword");
        mv.addObject("resetPasswordForm", resetPasswordForm);

        BenutzerRecord benutzer = dslContext.selectFrom(BENUTZER).where(BENUTZER.RESETPASSWORDTOKEN.eq(token)).fetchOne();
        if (benutzer != null) {
            mv.addObject("resetToken", benutzer.getResetpasswordtoken());
        } else {
            mv.addObject("invalidToken", "Oops! This is an invalid link.");
        }

        return mv;
    }

    @PostMapping("/resetPassword")
    public ModelAndView resetPassword(@Valid ResetPasswordForm resetPasswordForm,
                                      BindingResult bindingResult,
                                      @RequestParam String token) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/resetPassword");

        // check if passwords are the same
        if (!resetPasswordForm.getPassword().equals(resetPasswordForm.getRepeatPassword()))
            bindingResult.rejectValue("repeatPassword", "repeatPassword", "Passwords do not match");

        if (!bindingResult.hasErrors()) {
            // check if token exists
            BenutzerRecord benutzer = dslContext.selectFrom(BENUTZER).where(BENUTZER.RESETPASSWORDTOKEN.eq(token)).fetchOne();
            if (benutzer != null) {

                try {
                    // update password
                    dslContext.update(BENUTZER)
                            .set(BENUTZER.PASSWORT, hashPasswordEncoder.encode(resetPasswordForm.getPassword()))
                            .where(BENUTZER.RESETPASSWORDTOKEN.eq(token))
                            .execute();

                    // reset token
                    dslContext.update(BENUTZER).set(BENUTZER.RESETPASSWORDTOKEN, "").where(BENUTZER.BENUTZERID.eq(benutzer.getBenutzerid())).execute();

                    mv.addObject("successMessage", "Your password has been changed!");
                } catch (Exception e) {
                    e.printStackTrace();
                    mv.addObject("resetToken", benutzer.getResetpasswordtoken());
                }

            } else {
                mv.addObject("invalidToken", "Oops! This is an invalid link.");
            }
        }

        return mv;
    }

}
