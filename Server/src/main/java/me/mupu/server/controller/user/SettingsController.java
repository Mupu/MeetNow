package me.mupu.server.controller.user;

import me.mupu.server.HashPasswordEncoder;
import me.mupu.server.model.CustomUserDetails;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.generated.tables.records.PersonRecord;
import org.jooq.types.UInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static org.jooq.generated.Tables.*;

@Controller
@Secured("ROLE_USER")
@RequestMapping("user/settings")
public class SettingsController {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private HashPasswordEncoder hashPasswordEncoder;

    @GetMapping
    public ModelAndView settings() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("user/settings");

        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Record r = dslContext.select().from(PERSON).leftJoin(BENUTZER).using(PERSON.PERSONID)
                .where(BENUTZER.BENUTZERID.eq(user.getUserdata().getBenutzerid()))
                .fetchOne();
        mv.addObject("userdata", r);

        return mv;
    }

    @PutMapping()
    public ModelAndView updateData(@RequestParam String vorname,
                                   @RequestParam String nachname,
                                   @RequestParam String benutzername,
                                   @RequestParam String passwort) {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/user/settings");

        // get current logged in user
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            if (!vorname.equals(""))
                dslContext.update(PERSON)
                        .set(PERSON.VORNAME, vorname)
                        .where(PERSON.PERSONID.eq(user.getUserdata().getPersonid()))
                        .execute();

            if (!nachname.equals(""))
                dslContext.update(PERSON)
                        .set(PERSON.NACHNAME, nachname)
                        .where(PERSON.PERSONID.eq(user.getUserdata().getPersonid()))
                        .execute();

            if (!benutzername.equals(""))
                dslContext.update(BENUTZER)
                        .set(BENUTZER.BENUTZERNAME, benutzername)
                        .where(BENUTZER.BENUTZERID.eq(user.getUserdata().getBenutzerid()))
                        .execute();

            if (!passwort.equals(""))
                dslContext.update(BENUTZER)
                        .set(BENUTZER.PASSWORT, hashPasswordEncoder.encode(passwort))
                        .where(BENUTZER.BENUTZERID.eq(user.getUserdata().getBenutzerid()))
                        .execute();

            mv.addObject("dataChanged", "");
        } catch (Exception e) {
            mv.addObject("dataChangedError", "");
        }
        return mv;
    }

    @DeleteMapping()
    public ModelAndView deleteAccount() {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        dslContext.deleteFrom(BENUTZER)
                .where(BENUTZER.BENUTZERID.eq(user.getUserdata().getBenutzerid()))
                .execute();

        dslContext.deleteFrom(USER_ROLE)
                .where(USER_ROLE.BENUTZERID.eq(user.getUserdata().getBenutzerid()))
                .execute();

        dslContext.deleteFrom(PERSON)
                .where(PERSON.PERSONID.eq(user.getUserdata().getPersonid()))
                .execute();

        return new ModelAndView("redirect:/login?accountDeleted");
    }
}
