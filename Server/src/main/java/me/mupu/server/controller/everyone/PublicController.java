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

    @RequestMapping("/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/login");

        return mv;
    }

    @RequestMapping(value = {"/", "/home"})
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/home");

        return mv;
    }

}
