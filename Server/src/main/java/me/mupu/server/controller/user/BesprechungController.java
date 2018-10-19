package me.mupu.server.controller.user;

import me.mupu.server.form.BesprechungForm;
import me.mupu.server.form.RegistrationForm;
import me.mupu.server.model.CustomUser;
import me.mupu.server.service.RegistrationService;
import org.jooq.*;
import org.jooq.generated.tables.records.*;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.jooq.generated.Tables.*;
import static org.jooq.impl.DSL.ifnull;
import static org.jooq.impl.DSL.inline;

@Controller
@Secured("ROLE_USER")
@RequestMapping("user")
public class BesprechungController {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private RegistrationService registrationService;

    /**
     * ###################################
     * #        neue Besprechung
     * *##################################
     */

    @GetMapping("/neueBesprechung")
    public ModelAndView besprechungForm(BesprechungForm besprechungForm) {

        // get current logged in user
        BenutzerRecord user = ((CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();

        // default start end date
        Calendar c = Calendar.getInstance();
        besprechungForm.setZeitraumStart(c.getTime());

        c.add(Calendar.HOUR, 1);
        besprechungForm.setZeitraumEnde(c.getTime());

        return createDefaultNeueBesprechungView(user, besprechungForm, null);
    }

    @PostMapping("/neueBesprechung")
    public ModelAndView neueBesprechung(@Valid BesprechungForm besprechungForm, BindingResult bindingResult) {

        // get current logged in user
        BenutzerRecord user = ((CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();

        RaumRecord raum = dslContext.selectFrom(RAUM).where(RAUM.RAUMID.eq(UInteger.valueOf(besprechungForm.getRaumId()))).fetchAny();
        if (raum == null)
            bindingResult.rejectValue("raumId", "raumId", "Bitte wähle einen Raum aus");

        if (besprechungForm.getZeitraumStart() == null || besprechungForm.getZeitraumEnde() == null
                || besprechungForm.getZeitraumStart().compareTo(besprechungForm.getZeitraumEnde()) >= 0)
            bindingResult.rejectValue("zeitraumEnde", "zeitraumEnde", "Endzeitpunkt muss nach Startzeitpunkt liegen");

        if (!bindingResult.hasErrors()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // add besprechung
            BesprechungRecord besprechung = dslContext
                    .insertInto(BESPRECHUNG)
                    .columns(BESPRECHUNG.RAUMID,
                            BESPRECHUNG.BESITZERPID,
                            BESPRECHUNG.THEMA,
                            BESPRECHUNG.ZEITRAUMSTART,
                            BESPRECHUNG.ZEITRAUMENDE
                    )
                    .values(UInteger.valueOf(besprechungForm.getRaumId()),
                            user.getPersonid(),
                            besprechungForm.getThema(),
                            Timestamp.valueOf(sdf.format(besprechungForm.getZeitraumStart())),
                            Timestamp.valueOf(sdf.format(besprechungForm.getZeitraumEnde()))
                    )
                    .returning()
                    .fetchOne();

            // add termine
            dslContext.insertInto(TEILNAHME)
                    .values(user.getPersonid(), besprechung.getBesprechungid())
                    .execute();
            for (String u :
                    besprechungForm.getInvitedUsers()) {
                dslContext.insertInto(TEILNAHME)
                        .values(UInteger.valueOf(u), besprechung.getBesprechungid())
                        .execute();
            }

            // add items
            for (String u :
                    besprechungForm.getChosenItemsCount()) {
                String[] p = u.split(":");
                if (UInteger.valueOf(p[1]).intValue() > 0) {
                    dslContext.insertInto(AUSLEIHE)
                            .values(besprechung.getBesprechungid(), p[0], p[1])
                            .execute();
                }
            }

            return createDefaultNeueBesprechungView(user, besprechungForm, "redirect:/user/termine");
        }

        return createDefaultNeueBesprechungView(user, besprechungForm, null);
    }

    private ModelAndView createDefaultNeueBesprechungView(BenutzerRecord user, BesprechungForm besprechungForm, String viewName) {
        ModelAndView mv = new ModelAndView();
        if (viewName == null)
        mv.setViewName("user/neueBesprechung");
        else
        mv.setViewName(viewName);


        Result<PersonRecord> userList = dslContext.selectFrom(PERSON).orderBy(PERSON.VORNAME, PERSON.NACHNAME).fetch();
        // remove yourself from list
        userList.removeIf(personRecord -> user.getPersonid().intValue() == personRecord.getPersonid().intValue());

        mv.addObject("userList", userList);
        mv.addObject("userId", user.getPersonid().intValue());
        mv.addObject("gegenstandList", getAvailableItems(besprechungForm.getZeitraumStart(), besprechungForm.getZeitraumEnde()));
        mv.addObject("rooms", getAvailableRooms(besprechungForm.getZeitraumStart(), besprechungForm.getZeitraumEnde()));
        return mv;
    }

    /**
     * ###################################
     * #        edit Besprechung
     * *##################################
     */

    @GetMapping("/editBesprechung/{besprechungId}")
    public ModelAndView getEditBesprechung(@PathVariable int besprechungId,
                                           BesprechungForm besprechungForm,
                                           RegistrationForm registrationForm) {
        System.out.println("EDIT GET");

        // get current logged in user
        BenutzerRecord owner = ((CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();

        // check if user owns that meeting
        BesprechungRecord besprechung = isOwnerOfRoom(owner.getPersonid(), besprechungId);

        return createDefaultEditBesprechungView(
                owner.getPersonid(),
                besprechungForm,
                registrationForm,
                besprechung
        );
    }

    @PutMapping("/editBesprechung/{besprechungId}")
    public ModelAndView editBesprechung(@PathVariable int besprechungId,
                                        @Valid BesprechungForm besprechungForm,
                                        BindingResult bindingResult,
                                        RegistrationForm registrationForm,
                                        BindingResult bindingResulttest) {
        System.out.println("EDIT PUT");


        // get current logged in user
        BenutzerRecord owner = ((CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();

        // check if user owns that meeting
        BesprechungRecord besprechung = isOwnerOfRoom(owner.getPersonid(), besprechungId);

        // check if times are correct
        if (besprechungForm.getZeitraumStart() == null || besprechungForm.getZeitraumEnde() == null
                || besprechungForm.getZeitraumStart().compareTo(besprechungForm.getZeitraumEnde()) >= 0)
            bindingResult.rejectValue("zeitraumEnde", "zeitraumEnde", "Endzeitpunkt muss nach Startzeitpunkt liegen");


        if (!bindingResult.hasErrors()) {
            // todo make this into own method called change date
            // datetime pattern
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dslContext.update(BESPRECHUNG)
                    .set(BESPRECHUNG.THEMA, besprechungForm.getThema())
                    .set(BESPRECHUNG.ZEITRAUMSTART, Timestamp.valueOf(sdf.format(besprechungForm.getZeitraumStart())))
                    .set(BESPRECHUNG.ZEITRAUMENDE, Timestamp.valueOf(sdf.format(besprechungForm.getZeitraumEnde())))
                    .set(BESPRECHUNG.RAUMID, UInteger.valueOf(besprechungForm.getRaumId()))
                    .where(BESPRECHUNG.BESPRECHUNGID.eq(besprechung.getBesprechungid()))
                    .execute();

            // users
            updateTeilnahmeDatabase(owner, besprechung, besprechungForm);

            // items
            updateAusleiheDatabase(besprechung, besprechungForm);
        }


        return createDefaultEditBesprechungView(
                owner.getPersonid(),
                besprechungForm,
                registrationForm,
                besprechung);
    }

    @PostMapping("/registration/{besprechungId}")
    public ModelAndView registerForm(@PathVariable int besprechungId,
                                     @Valid RegistrationForm registrationForm,
                                     BindingResult bindingResult,
                                     BesprechungForm besprechungForm,
                                     HttpServletRequest request) {

        System.out.println("registration POST");
        ModelAndView mv = new ModelAndView();
        mv.setViewName("user/editBesprechung");

        // get current logged in user
        BenutzerRecord user = ((CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();

        BesprechungRecord besprechung = isOwnerOfRoom(user.getPersonid(), besprechungId);

        Result<RaumRecord> rooms = getAvailableRooms(besprechung.getZeitraumstart(), besprechung.getZeitraumende());
        // add current room to the list as current room
        rooms.add(0, dslContext.selectFrom(RAUM).where(RAUM.RAUMID.eq(besprechung.getRaumid())).fetchSingle());

        Result<PersonRecord> userList = dslContext.selectFrom(PERSON).orderBy(PERSON.VORNAME, PERSON.NACHNAME).fetch();
        // remove yourself from list
        userList.removeIf(personRecord -> user.getPersonid().intValue() == personRecord.getPersonid().intValue());

        //             id        name    anzahl
        Result<Record3<UInteger, String, Integer>> availableItems = getAvailableItems(besprechungForm.getZeitraumStart(), besprechungForm.getZeitraumEnde());

        Result<AusleiheRecord> currentlyReservedItems = dslContext.selectFrom(AUSLEIHE).where(AUSLEIHE.BESPRECHUNGID.eq(besprechung.getBesprechungid())).fetch();

        // add currentlyReservedItems to available to get complete selection for this meeting
        currentlyReservedItems.forEach(ri ->
                availableItems.forEach(ai -> {
                    if (ri.getAusstattungsgegenstandid().intValue() == ((UInteger) ai.get(0)).intValue()) {
                        ai.set(AUSSTATTUNGSGEGENSTAND.ANZAHL, ri.getAnzahl().add((int) ai.getValue("Anzahl")));
                    }
                })
        );

        if (!bindingResult.hasErrors()) {
            PersonRecord addedUser;
            if ((addedUser = registrationService.registerUser(registrationForm, request)) != null) {
                mv.addObject("success", "Registered: " + addedUser.getVorname() + " " + addedUser.getNachname());
                // add newly added user to list
                userList.add(addedUser);
            } else
                mv.addObject("error", "Couldnt add user");
        }

        mv.addObject("registrationForm", registrationForm);
        mv.addObject("besprechungForm", besprechungForm);
        mv.addObject("rooms", rooms);
        mv.addObject("gegenstandList", availableItems);
        mv.addObject("userList", userList);
        mv.addObject("bId", besprechung.getBesprechungid());
        return mv;
    }

    @DeleteMapping("/deleteBesprechung/{besprechungId}")
    public ModelAndView deleteBesprechung(@PathVariable int besprechungId) {

        // get current logged in user
        BenutzerRecord user = ((CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();

        // check if user owns that meeting
        BesprechungRecord besprechung = dslContext.selectFrom(BESPRECHUNG).
                where(BESPRECHUNG.BESPRECHUNGID.eq(UInteger.valueOf(besprechungId))) // gleicher raum?
                .and(BESPRECHUNG.BESITZERPID.eq(user.getPersonid()))// ist besitzer ?
                .and(BESPRECHUNG.ZEITRAUMENDE.greaterOrEqual(DSL.now())) // is still active or in future
                .fetchSingle();

        dslContext.deleteFrom(BESPRECHUNG).where(BESPRECHUNG.BESPRECHUNGID.eq(besprechung.getBesprechungid())).execute();
        System.out.println("DELETE BESPRECHUNG" + besprechung.getBesprechungid());
        return new ModelAndView("redirect:/user/termine");
    }

    /**
     * Adds/removes the users to/from the database based on the selection in besprechungForm
     */
    private void updateTeilnahmeDatabase(BenutzerRecord owner, BesprechungRecord besprechung, BesprechungForm besprechungForm) {
        // this is all previously invited users but will be filtered to removedUsers
        Result<TeilnahmeRecord> removedUsers = dslContext.selectFrom(TEILNAHME).where(TEILNAHME.BESPRECHUNGID.eq(besprechung.getBesprechungid())).fetch();

        Supplier<Stream<TeilnahmeRecord>> stillInvitedUsers = () ->
                removedUsers.stream().filter(us ->
                        Arrays.stream(besprechungForm.getInvitedUsers()).anyMatch(b ->
                                us.getPersonid().intValue() == Integer.valueOf(b)
                        )
                );

        ArrayList<String> newlyAddedUsers = new ArrayList<>(Arrays.asList(besprechungForm.getInvitedUsers()));
        // remove users that are were invited before
        newlyAddedUsers.removeIf(nau ->
                stillInvitedUsers.get().anyMatch(siu ->
                        siu.getPersonid().intValue() == Integer.valueOf(nau)
                )
        );

        // remove owner from list
        removedUsers.removeIf(u -> u.getPersonid().intValue() == owner.getPersonid().intValue());
        // remove users that are still invited
        removedUsers.removeIf(u ->
                stillInvitedUsers.get().anyMatch(x ->
                        x.getPersonid().intValue() == u.getPersonid().intValue()
                )
        );

        // remove removedUsers
        removedUsers.forEach(ru -> {
            // replace a removed users id with an added one
            if (newlyAddedUsers.size() > 0) {
                UInteger newPid = UInteger.valueOf(newlyAddedUsers.get(0));
                newlyAddedUsers.remove(0);
                dslContext.update(TEILNAHME)
                        .set(TEILNAHME.PERSONID, newPid)
                        .where(TEILNAHME.PERSONID.eq(ru.getPersonid()))
                        .and(TEILNAHME.BESPRECHUNGID.eq(ru.getBesprechungid()))
                        .execute();
            } else // delete user
                dslContext.deleteFrom(TEILNAHME)
                        .where(TEILNAHME.PERSONID.eq(ru.getPersonid()))
                        .and(TEILNAHME.BESPRECHUNGID.eq(ru.getBesprechungid()))
                        .execute();
        });

        // add remaining newly added users
        newlyAddedUsers.forEach(nau ->
                dslContext.insertInto(TEILNAHME)
                        .values(UInteger.valueOf(nau),
                                besprechung.getBesprechungid())
                        .execute()
        );
    }

    /**
     * Adds/removes the items to/from the database based on the selection in besprechungForm
     */
    // todo check for changed inputs ( [id : count] <---- )
    private void updateAusleiheDatabase(BesprechungRecord besprechung, BesprechungForm besprechungForm) {

        Result<AusleiheRecord> previousItems = dslContext.selectFrom(AUSLEIHE).where(AUSLEIHE.BESPRECHUNGID.eq(besprechung.getBesprechungid())).fetch();

        // split string and convert to map
        Map<Integer, Integer> besprechungChosenItemsMap = new HashMap<>();
        for (String s : besprechungForm.getChosenItemsCount()) {
            String[] parts = s.split(":");
            besprechungChosenItemsMap.put(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]));
        }

        // is previously selected items but will be filtered to removedItems
        Result<AusleiheRecord> removedItems = dslContext.selectFrom(AUSLEIHE).where(AUSLEIHE.BESPRECHUNGID.eq(besprechung.getBesprechungid())).fetch();

        Supplier<Stream<AusleiheRecord>> stillSelectedItems = () ->
                removedItems.stream().filter(ri ->
                        besprechungChosenItemsMap.keySet().stream().anyMatch(psiks ->
                                ri.getAusstattungsgegenstandid().intValue() == psiks
                        )
                );


        ArrayList<Integer> newlyAddedItems = new ArrayList<>(besprechungChosenItemsMap.keySet());
        // remove still selected and unimportant items
        newlyAddedItems.removeIf(nai ->
                stillSelectedItems.get().anyMatch(ssi ->
                        ssi.getAusstattungsgegenstandid().intValue() == nai
                                && (ssi.getAnzahl().intValue() == besprechungChosenItemsMap.get(nai))
                )
                        || besprechungChosenItemsMap.get(nai) == 0
        );

        // remove items that are on both lists
        removedItems.removeIf(ri ->                     // ?????????????????????
                removedItems.stream().noneMatch(nai ->  // ?????????????????????
                        nai.getAusstattungsgegenstandid().intValue() == ri.getAusstattungsgegenstandid().intValue())
                        || besprechungChosenItemsMap.get(ri.getAusstattungsgegenstandid().intValue()) != 0
        );


        // delete all removed items
        removedItems.forEach(ri ->
                dslContext.deleteFrom(AUSLEIHE)
                        .where(AUSLEIHE.AUSSTATTUNGSGEGENSTANDID.eq(ri.getAusstattungsgegenstandid()))
                        .and(AUSLEIHE.BESPRECHUNGID.eq(ri.getBesprechungid()))
                        .execute()
        );

        // add/update newly added/updateted items/itemcounts
        newlyAddedItems.forEach(nai -> {
            if (previousItems.stream().anyMatch(pi -> pi.getAusstattungsgegenstandid().intValue() == nai)) // has the item been in the database ?
                dslContext.update(AUSLEIHE)
                        .set(AUSLEIHE.ANZAHL, UInteger.valueOf(besprechungChosenItemsMap.get(nai)))
                        .where(AUSLEIHE.AUSSTATTUNGSGEGENSTANDID.eq(UInteger.valueOf(nai)))
                        .execute();
            else
                dslContext.insertInto(AUSLEIHE)
                        .values(besprechung.getBesprechungid(),
                                UInteger.valueOf(nai),
                                UInteger.valueOf(besprechungChosenItemsMap.get(nai)))
                        .execute();
        });
    }

    private ModelAndView createDefaultEditBesprechungView(UInteger ownerPId,
                                                          BesprechungForm besprechungForm,
                                                          RegistrationForm registrationForm,
                                                          BesprechungRecord besprechung

    ) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("user/editBesprechung");


        // set default values
        // only set these default values of they havent been set yet (on get request)
        if (besprechungForm.getRaumId() == -1) {
            besprechungForm.setThema(besprechung.getThema());
            besprechungForm.setZeitraumStart(besprechung.getZeitraumstart());
            besprechungForm.setZeitraumEnde(besprechung.getZeitraumende());
            besprechungForm.setRaumId(besprechung.getRaumid().intValue());
        }

        Result<PersonRecord> allExistingUsers = dslContext.selectFrom(PERSON).orderBy(PERSON.VORNAME, PERSON.NACHNAME).fetch();
        // check invited users
        besprechungForm.setInvitedUsers(getInvitedUsersForThymeleaf(ownerPId, besprechung, allExistingUsers));


        //             id        name    anzahl
        Result<Record3<UInteger, String, Integer>> availableItems = getAvailableItems(
                besprechung.getBesprechungid(),
                besprechungForm.getZeitraumStart(),
                besprechungForm.getZeitraumEnde()
        );
        // select items
        besprechungForm.setChosenItemsCount(getSelectedItemsForThymeleaf(besprechung, availableItems));


        Result<RaumRecord> availableRooms = getAvailableRooms(besprechungForm.getZeitraumStart(), besprechungForm.getZeitraumEnde());
        // add currently selected room to the list as current room
        availableRooms.add(0, dslContext.selectFrom(RAUM).where(RAUM.RAUMID.eq(UInteger.valueOf(besprechungForm.getRaumId()))).fetchSingle());


        // pass information to mv
        mv.addObject("registrationForm", registrationForm);
        mv.addObject("besprechungForm", besprechungForm);
        mv.addObject("rooms", availableRooms);
        mv.addObject("gegenstandList", availableItems);
        mv.addObject("userList", allExistingUsers);
        mv.addObject("bId", besprechung.getBesprechungid());
        return mv;
    }

    private String[] getSelectedItemsForThymeleaf(BesprechungRecord besprechung, Result<Record3<UInteger, String, Integer>> availableItems) {
        Result<AusleiheRecord> currentlyReservedItems = dslContext.selectFrom(AUSLEIHE).where(AUSLEIHE.BESPRECHUNGID.eq(besprechung.getBesprechungid())).fetch();

        System.out.println(availableItems);
        System.out.println(currentlyReservedItems);
        // convert stream to string stream
        Stream<String> ris = currentlyReservedItems.stream().map(ri ->
                String.valueOf(ri.getAusstattungsgegenstandid()) + ":" + String.valueOf(ri.getAnzahl())
        );

        return ris.toArray(String[]::new);
    }

    private String[] getInvitedUsersForThymeleaf(UInteger ownerPId, BesprechungRecord besprechung, Result<PersonRecord> allExistingUsers) {
        // remove owner from list
        allExistingUsers.removeIf(personRecord -> ownerPId.intValue() == personRecord.getPersonid().intValue());

        Result<Record1<UInteger>> invitedUsers = dslContext
                .select(TEILNAHME.PERSONID)
                .from(TEILNAHME)
                .where(TEILNAHME.BESPRECHUNGID.eq(besprechung.getBesprechungid()))
                .and(TEILNAHME.PERSONID.ne(ownerPId)) // remove the owner from the list
                .fetch();

        Stream<String> invitedUsersStream = invitedUsers.stream().map(s -> String.valueOf(((UInteger) s.get(0)).intValue()));

        return invitedUsersStream.toArray(String[]::new);
    }

    private BesprechungRecord isOwnerOfRoom(UInteger ownerPId, int roomId) {
        return dslContext.selectFrom(BESPRECHUNG).
                where(BESPRECHUNG.BESPRECHUNGID.eq(UInteger.valueOf(roomId))) // gleicher raum?
                .and(BESPRECHUNG.BESITZERPID.eq(ownerPId))// ist besitzer ?
                .and(BESPRECHUNG.ZEITRAUMENDE.greaterOrEqual(DSL.now())) // is still active or in future
                .fetchSingle();
    }

    private Result<Record3<UInteger, String, Integer>> getAvailableItems(Date startDate, Date endDate) {
        return this.getAvailableItems(null, startDate, endDate);
    }

    private Result<Record3<UInteger, String, Integer>> getAvailableItems(UInteger besprechungId, Date startDate, Date endDate) {

        Table<BesprechungRecord> meetings = dslContext
                .selectFrom(BESPRECHUNG)
                .where(DSL.timestamp(startDate).between(BESPRECHUNG.ZEITRAUMSTART, BESPRECHUNG.ZEITRAUMENDE))
                .or(DSL.timestamp(endDate).between(BESPRECHUNG.ZEITRAUMSTART, BESPRECHUNG.ZEITRAUMENDE))
                .asTable();

        Table<Record2<UInteger, Integer>> reservedItems;
        if (besprechungId != null)
            reservedItems = dslContext
                    .select(AUSLEIHE.AUSSTATTUNGSGEGENSTANDID, DSL.sum(AUSLEIHE.ANZAHL).mul(-1).cast(Integer.class).as("Anzahl"))
                    .from(AUSLEIHE)
                    .leftJoin(meetings).on(AUSLEIHE.BESPRECHUNGID.eq(meetings.field(BESPRECHUNG.BESPRECHUNGID)))
                    .where(meetings.field(BESPRECHUNG.BESPRECHUNGID).isNotNull())
                    .and(meetings.field(BESPRECHUNG.BESPRECHUNGID).ne(besprechungId))
                    .groupBy(AUSLEIHE.AUSSTATTUNGSGEGENSTANDID)
                    .asTable();
        else
            reservedItems = dslContext
                    .select(AUSLEIHE.AUSSTATTUNGSGEGENSTANDID, DSL.sum(AUSLEIHE.ANZAHL).mul(-1).cast(Integer.class).as("Anzahl"))
                    .from(AUSLEIHE)
                    .leftJoin(meetings).on(AUSLEIHE.BESPRECHUNGID.eq(meetings.field(BESPRECHUNG.BESPRECHUNGID)))
                    .where(meetings.field(BESPRECHUNG.BESPRECHUNGID).isNotNull())
                    .groupBy(AUSLEIHE.AUSSTATTUNGSGEGENSTANDID)
                    .asTable();


        SelectJoinStep<Record2<UInteger, UInteger>> allItemsWithMaxCount = dslContext
                .select(AUSSTATTUNGSGEGENSTAND.AUSSTATTUNGSGEGENSTANDID, AUSSTATTUNGSGEGENSTAND.ANZAHL)
                .from(AUSSTATTUNGSGEGENSTAND);


        Table<Record2<UInteger, UInteger>> combined = dslContext
                .select(reservedItems.field(AUSSTATTUNGSGEGENSTAND.AUSSTATTUNGSGEGENSTANDID),
                        reservedItems.field(AUSSTATTUNGSGEGENSTAND.ANZAHL))
                .from(reservedItems)
                .union(allItemsWithMaxCount)
                .asTable();


        SelectHavingStep<Record2<Integer, BigDecimal>> availableitems = dslContext
                .select(combined.field("AusstattungsgegenstandId").cast(Integer.class).as("AusstattungsgegenstandId"),
                        DSL.sum(combined.field("Anzahl").cast(Integer.class)).as("Anzahl"))
                .from(combined)
                .groupBy(combined.field("AusstattungsgegenstandId"));


        Result<Record3<UInteger, String, Integer>> listWithName = dslContext
                .select(AUSSTATTUNGSGEGENSTAND.AUSSTATTUNGSGEGENSTANDID,
                        AUSSTATTUNGSGEGENSTAND.NAME,
                        availableitems.field("Anzahl").cast(Integer.class).as("Anzahl"))
                .from(availableitems)
                .leftJoin(AUSSTATTUNGSGEGENSTAND).using(AUSSTATTUNGSGEGENSTAND.AUSSTATTUNGSGEGENSTANDID)
                .where(availableitems.field("Anzahl").cast(Integer.class).gt(0))
                .fetch();

        return listWithName;
    }

    private Result<RaumRecord> getAvailableRooms(Date startDate, Date endDate) {
        Table<BesprechungRecord> bussyRooms = dslContext
                .selectFrom(BESPRECHUNG)
                .where(DSL.timestamp(startDate).between(BESPRECHUNG.ZEITRAUMSTART, BESPRECHUNG.ZEITRAUMENDE))
                .or(DSL.timestamp(endDate).between(BESPRECHUNG.ZEITRAUMSTART, BESPRECHUNG.ZEITRAUMENDE))
                .groupBy(BESPRECHUNG.RAUMID).asTable();

        return dslContext
                .select()
                .from(RAUM)
                .leftJoin(bussyRooms)
                .on(bussyRooms.field(BESPRECHUNG.RAUMID).eq(RAUM.RAUMID))
                .where(bussyRooms.field(BESPRECHUNG.RAUMID).isNull())
                .fetchInto(RAUM);
    }
}
