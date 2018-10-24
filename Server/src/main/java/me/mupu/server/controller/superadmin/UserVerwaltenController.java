package me.mupu.server.controller.superadmin;

import me.mupu.server.HashPasswordEncoder;
import me.mupu.server.form.UserVerwaltenForm;
import me.mupu.server.model.CustomUser;
import org.jooq.*;
import org.jooq.generated.tables.records.BenutzerRecord;
import org.jooq.generated.tables.records.RoleRecord;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.*;

import static org.jooq.generated.Tables.*;

@Controller
@Secured("ROLE_SUPERADMIN")
@RequestMapping("/superadmin/userVerwalten")
public class UserVerwaltenController {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private HashPasswordEncoder hashPasswordEncoder;

    @GetMapping()
    public ModelAndView getUserForm(UserVerwaltenForm userVerwaltenForm) {
        return createDefaultUserVerwaltenControllerView(userVerwaltenForm);
    }

    @PutMapping("/edit/{benutzerId}")
    public ModelAndView edit(@PathVariable int benutzerId,
                             @Valid UserVerwaltenForm userVerwaltenForm,
                             BindingResult bindingResult) {
        ModelAndView mv = createDefaultUserVerwaltenControllerView(userVerwaltenForm);

        if (!bindingResult.hasErrors()) {
            BenutzerRecord user = dslContext.selectFrom(BENUTZER).where(BENUTZER.BENUTZERID.eq(UInteger.valueOf(benutzerId))).fetchSingle();

            dslContext
                    .update(PERSON)
                    .set(PERSON.VORNAME, userVerwaltenForm.getVorname())
                    .set(PERSON.NACHNAME, userVerwaltenForm.getNachname())
                    .set(PERSON.EMAIL, userVerwaltenForm.getEmail())
                    .where(PERSON.PERSONID.eq(user.getPersonid()))
                    .execute();

            dslContext
                    .update(BENUTZER)
                    .set(BENUTZER.BENUTZERNAME, userVerwaltenForm.getBenutzername())
                    .where(BENUTZER.BENUTZERID.eq(UInteger.valueOf(benutzerId)))
                    .execute();

            if (userVerwaltenForm.getPasswort() != null && !userVerwaltenForm.getPasswort().equals("")) {
                dslContext
                        .update(BENUTZER)
                        .set(BENUTZER.PASSWORT, hashPasswordEncoder.encode(userVerwaltenForm.getPasswort()))
                        .where(BENUTZER.BENUTZERID.eq(UInteger.valueOf(benutzerId)))
                        .execute();
            }

            Result<Record> previousRanks = dslContext
                    .select()
                    .from(USER_ROLE)
                    .rightJoin(ROLE).using(ROLE.ROLEID)
                    .where(USER_ROLE.BENUTZERID.eq(UInteger.valueOf(benutzerId)))
                    .fetch();

            List<String> selectedRanks = Arrays.asList(userVerwaltenForm.getRanks());

            ArrayList<Record> tempList = new ArrayList<>();
            for (Record r :
                    previousRanks) {
                if (selectedRanks.contains(String.valueOf((r.get(ROLE.ROLEID)).intValue())))
                    tempList.add(r);
            }

            tempList.forEach(tl -> {
                String i = String.valueOf(tl.get(ROLE.ROLEID).intValue());
                previousRanks.remove(tl);
            });

            selectedRanks.removeIf(sr -> tempList.stream().anyMatch(tl -> String.valueOf(tl.get(ROLE.ROLEID).intValue()).equals(sr)));

            System.out.println("add " + Arrays.toString(selectedRanks.toArray()));
            System.out.println("remove "+ previousRanks);
        }

        return mv;
    }

    @PutMapping("/add/{id}")
    public ModelAndView add(@PathVariable int id,
                            @Valid UserVerwaltenForm userVerwaltenForm,
                            BindingResult bindingResult) {
        ModelAndView mv = createDefaultUserVerwaltenControllerView(userVerwaltenForm);

        return mv;
    }

    @PutMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable int id,
                               @Valid UserVerwaltenForm userVerwaltenForm,
                               BindingResult bindingResult) {
        ModelAndView mv = createDefaultUserVerwaltenControllerView(userVerwaltenForm);

        return mv;
    }

    private ModelAndView createDefaultUserVerwaltenControllerView(UserVerwaltenForm userVerwaltenForm) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("superadmin/userVerwalten");

        Result<Record> userList = dslContext
                .select(PERSON.asterisk(),
                        BENUTZER.asterisk(),
                        DSL.groupConcat(ROLE.ROLEID).as("Roles"))
                .from(BENUTZER)
                .leftJoin(PERSON).using(PERSON.PERSONID)
                .rightJoin(USER_ROLE).using(USER_ROLE.BENUTZERID)
                .rightJoin(ROLE).using(ROLE.ROLEID)
                .groupBy(BENUTZER.BENUTZERID)
                .fetch();

        Result<RoleRecord> roles = dslContext.selectFrom(ROLE).fetch();

        Map<UInteger, List<String>> userRanks = new HashMap<>();
        for (Record user :
                userList) {
            UInteger id = (UInteger) user.get("BenutzerId");
            List<String> ranks = Arrays.asList(((String) user.get("Roles")).split(","));
            userRanks.put(id, ranks);
        }

        System.out.println(userList);

        mv.addObject("roles", roles);
        mv.addObject("userList",  userList);
        mv.addObject("userRanks",  userRanks);
        mv.addObject("userVerwaltenForm", userVerwaltenForm);
        return mv;
    }
}
