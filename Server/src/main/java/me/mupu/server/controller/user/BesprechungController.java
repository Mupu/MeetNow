package me.mupu.server.controller.user;

import com.sun.org.apache.xml.internal.utils.UnImplNode;
import me.mupu.server.form.BesprechungForm;
import me.mupu.server.model.CustomUserDetails;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.BenutzerRecord;
import org.jooq.generated.tables.records.RaumRecord;
import org.jooq.types.UInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static org.jooq.generated.Tables.*;

@Controller
@Secured("ROLE_USER")
@RequestMapping("user/neueBesprechung")
public class BesprechungController {

    @Autowired
    private DSLContext dslContext;

    @GetMapping
    public ModelAndView besprechungForm(BesprechungForm besprechungForm) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("user/neueBesprechung");
        mv.addObject("besprechungForm", besprechungForm);

        // get current logged in user
        BenutzerRecord user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();

        mv.addObject("userList", dslContext.selectFrom(PERSON).orderBy(PERSON.VORNAME, PERSON.NACHNAME).fetch());
        mv.addObject("gegenstandList", dslContext.selectFrom(AUSSTATTUNGSGEGENSTAND).fetch());
        mv.addObject("userId", user.getPersonid().intValue());
        mv.addObject("rooms", dslContext.selectFrom(RAUM).fetch());
        return mv;
    }

    @PutMapping()
    public ModelAndView neueBesprechung(@Valid BesprechungForm besprechungForm, BindingResult bindingResult) {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("user/neueBesprechung");

        System.out.println(Arrays.toString(besprechungForm.getChosenItems()));
        System.out.println(Arrays.toString(besprechungForm.getChosenItemsCount()));

        // get current logged in user
        BenutzerRecord user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();

        RaumRecord raum = dslContext.selectFrom(RAUM).where(RAUM.RAUMID.eq(UInteger.valueOf(besprechungForm.getRaumId()))).fetchAny();
        if (raum == null)
            bindingResult.rejectValue("raumId", "raumId", "Bitte wähle einen Raum aus");

        if (besprechungForm.getZeitraumStart() == null || besprechungForm.getZeitraumEnde() == null
            ||besprechungForm.getZeitraumStart().compareTo(besprechungForm.getZeitraumEnde()) >= 0)
            bindingResult.rejectValue("zeitraumEnde", "zeitraumEnde", "Endzeitpunkt muss nach Startzeitpunkt liegen");

        if (!bindingResult.hasErrors()) {
            System.out.println("keine fehler");
        }

        mv.addObject("userList", dslContext.selectFrom(PERSON).orderBy(PERSON.VORNAME, PERSON.NACHNAME).fetch());
        mv.addObject("gegenstandList", dslContext.selectFrom(AUSSTATTUNGSGEGENSTAND).fetch());
        mv.addObject("userId", user.getPersonid().intValue());
        mv.addObject("rooms", dslContext.selectFrom(RAUM).fetch());
        return mv;
    }

    @DeleteMapping()
    public ModelAndView deleteBesprechung() {
        return new ModelAndView("redirect:/user/neueBesprechung");
    }
}
