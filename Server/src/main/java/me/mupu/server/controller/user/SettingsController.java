package me.mupu.server.controller.user;

import me.mupu.server.model.CustomUserDetails;
import org.jooq.DSLContext;
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
public class SettingsController {
    @GetMapping("/settings")
    public ModelAndView settings() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("user/settings");

        return mv;
    }

    @Autowired
    private DSLContext dslContext;

    @PutMapping()
    public ModelAndView updateData() {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return new ModelAndView("redirect:/login?dataChanged");
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
