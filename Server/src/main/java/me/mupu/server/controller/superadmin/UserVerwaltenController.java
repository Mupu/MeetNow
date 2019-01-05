package me.mupu.server.controller.superadmin;

import me.mupu.server.HashPasswordEncoder;
import me.mupu.server.form.SuperadminAddUserForm;
import me.mupu.server.form.UserVerwaltenForm;
import me.mupu.server.model.CustomUser;
import org.jooq.*;
import org.jooq.generated.tables.records.BenutzerRecord;
import org.jooq.generated.tables.records.PersonRecord;
import org.jooq.generated.tables.records.RoleRecord;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

import static org.jooq.generated.Tables.*;

@Controller
@Secured("ROLE_SUPERADMIN")
@RequestMapping("/superadmin/userVerwalten")
public class UserVerwaltenController {

    //todo fix add user

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private HashPasswordEncoder hashPasswordEncoder;

    @GetMapping()
    public ModelAndView getUserForm(UserVerwaltenForm userVerwaltenForm,
                                    SuperadminAddUserForm superadminAddUserForm) {
        return createDefaultUserVerwaltenControllerView(userVerwaltenForm, superadminAddUserForm);
    }

    @PutMapping("/edit/{benutzerId}")
    public ModelAndView edit(@PathVariable int benutzerId,
                             @Valid UserVerwaltenForm userVerwaltenForm,
                             BindingResult bindingResult,
                             SuperadminAddUserForm superadminAddUserForm,
                             HttpServletRequest request,
                             HttpServletResponse response) {

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


            if (userVerwaltenForm.getRanks() != null) {
                ArrayList<String> selectedRanks = new ArrayList<>(Arrays.asList(userVerwaltenForm.getRanks()));

                ArrayList<Record> tempList = new ArrayList<>();
                for (Record r : // remove roles that stayed the same
                        previousRanks) {
                    if (selectedRanks.contains(String.valueOf((r.get(ROLE.ROLEID)).intValue())))
                        tempList.add(r);
                }


                tempList.forEach(tl -> {
                    String i = String.valueOf(tl.get(ROLE.ROLEID).intValue());
                    previousRanks.remove(tl);
                    selectedRanks.removeIf(sr -> sr.equals(i));
                });

                selectedRanks.forEach(sr -> {
                    if (previousRanks.size() > 0) { // replace an removed role with an added one
                        dslContext
                                .update(USER_ROLE)
                                .set(USER_ROLE.ROLEID, UInteger.valueOf(sr))
                                .where(USER_ROLE.BENUTZERID.eq(user.getBenutzerid()))
                                .and(USER_ROLE.ROLEID.eq((UInteger) previousRanks.remove(0).get(ROLE.ROLEID)))
                                .execute();
                    } else // just add new role
                        dslContext
                                .insertInto(USER_ROLE)
                                .values(user.getBenutzerid(),
                                        UInteger.valueOf(sr))
                                .execute();
                });

                previousRanks.forEach(pr ->
                    dslContext
                            .deleteFrom(USER_ROLE)
                            .where(USER_ROLE.BENUTZERID.eq(user.getBenutzerid()))
                            .and(USER_ROLE.ROLEID.eq((UInteger) pr.get(ROLE.ROLEID)))
                            .execute()
                );

            } else // no roles - remove all rights
                dslContext.deleteFrom(USER_ROLE).where(USER_ROLE.BENUTZERID.eq(user.getBenutzerid())).execute();


            BenutzerRecord currentLoggedInUser = ((CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();
            // if you edited yourself
            if (user.getBenutzerid().intValue() == currentLoggedInUser.getBenutzerid().intValue()) {
                // logout the user
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null) {
                    new SecurityContextLogoutHandler().logout(request, response, auth);
                }
                return new ModelAndView("redirect:/login?relog");
            }
        }

        return createDefaultUserVerwaltenControllerView(userVerwaltenForm, superadminAddUserForm);
    }

    @PostMapping("/add")
    public ModelAndView add(UserVerwaltenForm userVerwaltenForm,
                            @Valid SuperadminAddUserForm superadminAddUserForm,
                            BindingResult bindingResult) {

        if (superadminAddUserForm.getSAUFpasswort() == null
                || superadminAddUserForm.getSAUFpasswort().equals(""))
            bindingResult.rejectValue("SAUFpasswort", "SAUFpasswort", "Passwort darf nicht leer sein.");

        if (!bindingResult.hasErrors()) {
            PersonRecord addedPerson = dslContext.insertInto(PERSON)
                    .columns(PERSON.VORNAME,
                            PERSON.NACHNAME,
                            PERSON.EMAIL,
                            PERSON.CONFIRMATIONTOKEN)
                    .values(superadminAddUserForm.getSAUFvorname(),
                            superadminAddUserForm.getSAUFnachname(),
                            superadminAddUserForm.getSAUFemail(),
                            "")
                    .returning()
                    .fetchOne();

            BenutzerRecord addedBenutzer = dslContext.insertInto(BENUTZER)
                    .columns(BENUTZER.PERSONID,
//                            BENUTZER.BENUTZERID,
                            BENUTZER.BENUTZERNAME,
                            BENUTZER.PASSWORT,
                            BENUTZER.ISENABLED,
                            BENUTZER.RESETPASSWORDTOKEN)
                    .values(addedPerson.getPersonid(),
//                            addedPerson.getPersonid(),
                            superadminAddUserForm.getSAUFbenutzername(),
                            hashPasswordEncoder.encode(superadminAddUserForm.getSAUFpasswort()),
                            UByte.valueOf(1),
                            "")
                    .returning()
                    .fetchOne();

            for (String s :
                    superadminAddUserForm.getSAUFranks()) {
                dslContext.insertInto(USER_ROLE).values(addedBenutzer.getBenutzerid(), UInteger.valueOf(s)).execute();
            }

        }

        return createDefaultUserVerwaltenControllerView(userVerwaltenForm, superadminAddUserForm);
    }

    @DeleteMapping("/delete/{benutzerId}")
    public ModelAndView delete(@PathVariable int benutzerId,
                               UserVerwaltenForm userVerwaltenForm,
                               SuperadminAddUserForm superadminAddUserForm,
                               HttpServletRequest request,
                               HttpServletResponse response) {


        BenutzerRecord user = dslContext.selectFrom(BENUTZER).where(BENUTZER.BENUTZERID.eq(UInteger.valueOf(benutzerId))).fetchSingle();

        dslContext.deleteFrom(PERSON).where(PERSON.PERSONID.eq(user.getPersonid())).execute();

        BenutzerRecord currentLoggedInUser = ((CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();

        ModelAndView mv = createDefaultUserVerwaltenControllerView(userVerwaltenForm, superadminAddUserForm);

        // if you deleted yourself
        if (user.getBenutzerid().intValue() == currentLoggedInUser.getBenutzerid().intValue()) {
            // logout the user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            mv.setViewName("redirect:/login?accountDeleted");
        }

        return mv;
    }

    private ModelAndView createDefaultUserVerwaltenControllerView(UserVerwaltenForm userVerwaltenForm,
                                                                  SuperadminAddUserForm superadminAddUserForm) {
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

        mv.addObject("roles", roles);
        mv.addObject("userList", userList);
        mv.addObject("userRanks", userRanks);
        mv.addObject("userVerwaltenForm", userVerwaltenForm);
        mv.addObject("superadminAddUserForm", superadminAddUserForm);
        return mv;
    }
}
