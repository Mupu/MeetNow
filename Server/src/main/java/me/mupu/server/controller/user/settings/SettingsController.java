package me.mupu.server.controller.user.settings;

import me.mupu.server.HashPasswordEncoder;
import me.mupu.server.model.CustomUser;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
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


    @GetMapping()
    public ModelAndView settings() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("user/settings/settings");

        CustomUser user = (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Record r = dslContext.select().from(PERSON).leftJoin(BENUTZER).using(PERSON.PERSONID)
                .where(BENUTZER.BENUTZERID.eq(user.getUserdata().getBenutzerid()))
                .fetchOne();
        mv.addObject("userdata", r);

        return mv;
    }
}
