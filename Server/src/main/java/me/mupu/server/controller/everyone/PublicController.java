package me.mupu.server.controller.everyone;

import me.mupu.server.HashPasswordEncoder;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.generated.tables.Person;
import org.jooq.generated.tables.records.BenutzerRecord;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import java.util.Collections;

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

    @RequestMapping("/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/login");

        return mv;
    }

    @GetMapping(value = {"/registration", "/register"})
    public ModelAndView registration() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/registration");

        return mv;
    }

    @PostMapping(value = {"/registration", "/register"})
    public ModelAndView acceptForm(@RequestParam() String vorname,
                                   @RequestParam() String nachname,
                                   @RequestParam() String benutzername,
                                   @RequestParam() String passwort) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/registration");

        int pId = dslContext.insertInto(PERSON)
                .columns(PERSON.VORNAME, PERSON.NACHNAME)
                .values(vorname, nachname)
//                .returningResult(PERSON.PERSONID)
                .returning()
                .fetchOne().getPersonid().intValue();

        int bId = dslContext.insertInto(BENUTZER,
                BENUTZER.PERSONID,
                BENUTZER.BENUTZERNAME,
                BENUTZER.PASSWORT,
                BENUTZER.ACCOUNTSTATUS)
                .values(UInteger.valueOf(pId), benutzername, hashPasswordEncoder.encode(passwort), UByte.valueOf(1))
                .returning()
                .fetchOne().getBenutzerid().intValue();

        dslContext.insertInto(USER_ROLE)
                .values(bId, 1)
                .execute();
        return mv;
    }

    @RequestMapping(value = {"/", "/home"})
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("everyone/home");

        return mv;
    }
}
