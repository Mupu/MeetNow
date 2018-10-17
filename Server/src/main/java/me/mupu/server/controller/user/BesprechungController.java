package me.mupu.server.controller.user;

import me.mupu.server.form.BesprechungForm;
import me.mupu.server.model.CustomUserDetails;
import org.jooq.*;
import org.jooq.generated.tables.records.*;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.nio.file.NoSuchFileException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.jooq.generated.Tables.*;
import static org.jooq.impl.DSL.inline;

@Controller
@Secured("ROLE_USER")
@RequestMapping("user/")
public class BesprechungController {

    @Autowired
    private DSLContext dslContext;

    /**
     * ###################################
     * #        neue Besprechung
     * *##################################
     */

    @GetMapping("neueBesprechung")
    public ModelAndView besprechungForm(BesprechungForm besprechungForm) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("user/neueBesprechung");


        // get current logged in user
        BenutzerRecord user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();

        // default start end date
        Calendar c = Calendar.getInstance();
        besprechungForm.setZeitraumStart(c.getTime());

        c.add(Calendar.HOUR, 1);
        besprechungForm.setZeitraumEnde(c.getTime());

        Result<PersonRecord> userList = dslContext.selectFrom(PERSON).orderBy(PERSON.VORNAME, PERSON.NACHNAME).fetch();
        // remove yourself from list
        userList.removeIf(personRecord -> user.getPersonid().intValue() == personRecord.getPersonid().intValue());

        mv.addObject("besprechungForm", besprechungForm);
        mv.addObject("userList", userList);
        mv.addObject("userId", user.getPersonid().intValue());
        mv.addObject("rooms", getAvailableRooms(besprechungForm.getZeitraumStart(), besprechungForm.getZeitraumEnde()));
        mv.addObject("gegenstandList", getAvailableItems(besprechungForm.getZeitraumStart(), besprechungForm.getZeitraumEnde()));
        return mv;
    }

    @PostMapping("neueBesprechung")
    public ModelAndView neueBesprechung(@Valid BesprechungForm besprechungForm, BindingResult bindingResult) {

        ModelAndView mv = new ModelAndView();
        mv.setViewName("user/neueBesprechung");


        // get current logged in user
        BenutzerRecord user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();

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
        }

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

    @GetMapping("editBesprechung/{besprechungId}")
    public ModelAndView getEditBesprechung(@PathVariable int besprechungId, BesprechungForm besprechungForm) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("user/editBesprechung");

        // get current logged in user
        BenutzerRecord user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();

        // check if user owns that meeting
        BesprechungRecord besprechung = dslContext.selectFrom(BESPRECHUNG).
                where(BESPRECHUNG.BESPRECHUNGID.eq(UInteger.valueOf(besprechungId))) // gleicher raum?
                .and(BESPRECHUNG.BESITZERPID.eq(user.getPersonid()))    // owner ?
                .and(BESPRECHUNG.ZEITRAUMENDE.greaterOrEqual(DSL.now())) // is still active or in future
                .fetchSingle();

        // fill in values
        besprechungForm.setThema(besprechung.getThema());
        besprechungForm.setZeitraumStart(besprechung.getZeitraumstart());
        besprechungForm.setZeitraumEnde(besprechung.getZeitraumende());
        // preselect current room as default
        besprechungForm.setRaumId(besprechung.getRaumid().intValue());

        Result<RaumRecord> rooms = getAvailableRooms(besprechung.getZeitraumstart(), besprechung.getZeitraumende());
        // add current room to the list as current room
        rooms.add(0, dslContext.selectFrom(RAUM).where(RAUM.RAUMID.eq(besprechung.getRaumid())).fetchSingle());

        Result<PersonRecord> userList = dslContext.selectFrom(PERSON).orderBy(PERSON.VORNAME, PERSON.NACHNAME).fetch();
        // remove yourself from list
        userList.removeIf(personRecord -> user.getPersonid().intValue() == personRecord.getPersonid().intValue());

        // get invited users
        Result<Record1<UInteger>> invitedUsers = dslContext
                .select(TEILNAHME.PERSONID)
                .from(TEILNAHME)
                .where(TEILNAHME.BESPRECHUNGID.eq(besprechung.getBesprechungid()))
                .and(TEILNAHME.PERSONID.ne(user.getPersonid())) // remove the owner from the list
                .fetch();
        // converts UIntegers to String stream
        Stream<String> stream = invitedUsers.stream().map(s -> String.valueOf(((UInteger) s.get(0)).intValue()));
        // converts stream to array and add it as default to besprechungForm
        besprechungForm.setInvitedUsers(stream.toArray(String[]::new));

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

        // convert stream to string stream
        Stream<String> ris = currentlyReservedItems.stream().map(ri ->
                String.valueOf(ri.getAusstattungsgegenstandid()) + ":" + String.valueOf(ri.getAnzahl())
        );

        // add preselected items
        besprechungForm.setChosenItemsCount(ris.toArray(String[]::new));

        mv.addObject("besprechungForm", besprechungForm);
        mv.addObject("rooms", rooms);
        mv.addObject("gegenstandList", availableItems);
        mv.addObject("userList", userList);
        mv.addObject("bId", besprechung.getBesprechungid());
        return mv;
    }

    @PutMapping("editBesprechung/{besprechungId}")
    public ModelAndView editBesprechung(@PathVariable int besprechungId, @Valid BesprechungForm besprechungForm, BindingResult bindingResult) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("user/editBesprechung");

        // get current logged in user
        BenutzerRecord user = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserdata();

        // check if user owns that meeting
        BesprechungRecord besprechung = dslContext.selectFrom(BESPRECHUNG).
                where(BESPRECHUNG.BESPRECHUNGID.eq(UInteger.valueOf(besprechungId))) // gleicher raum?
                .and(BESPRECHUNG.BESITZERPID.eq(user.getPersonid()))// ist besitzer ?
                .and(BESPRECHUNG.ZEITRAUMENDE.greaterOrEqual(DSL.now())) // is still active or in future
                .fetchSingle();

        if (besprechungForm.getZeitraumStart() == null || besprechungForm.getZeitraumEnde() == null
                || besprechungForm.getZeitraumStart().compareTo(besprechungForm.getZeitraumEnde()) >= 0)
            bindingResult.rejectValue("zeitraumEnde", "zeitraumEnde", "Endzeitpunkt muss nach Startzeitpunkt liegen");


        if (!bindingResult.hasErrors()) {
            // datetime pattern
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dslContext.update(BESPRECHUNG)
                    .set(BESPRECHUNG.THEMA, besprechungForm.getThema())
                    .set(BESPRECHUNG.ZEITRAUMSTART, Timestamp.valueOf(sdf.format(besprechungForm.getZeitraumStart())))
                    .set(BESPRECHUNG.ZEITRAUMENDE, Timestamp.valueOf(sdf.format(besprechungForm.getZeitraumEnde())))
                    .set(BESPRECHUNG.RAUMID, UInteger.valueOf(besprechungForm.getRaumId()))
                    .where(BESPRECHUNG.BESPRECHUNGID.eq(besprechung.getBesprechungid()))
                    .execute();

            // this is all previously invited users but will be filtered to removedUsers
            Result<TeilnahmeRecord> removedUsers = dslContext.selectFrom(TEILNAHME).where(TEILNAHME.BESPRECHUNGID.eq(besprechung.getBesprechungid())).fetch();

            Supplier<Stream<TeilnahmeRecord>> stillInvitedUsers = () ->
                    removedUsers.stream().filter(us ->
                            Arrays.stream(besprechungForm.getInvitedUsers()).anyMatch(b ->
                                    us.getPersonid().intValue() == Integer.valueOf(b)
                            )
                    );

            // newly added users
            ArrayList<String> newlyAddedUsers = new ArrayList<>(Arrays.asList(besprechungForm.getInvitedUsers()));
            // remove users from list that are were invited before
            newlyAddedUsers.removeIf(nau ->
                    stillInvitedUsers.get().anyMatch(siu ->
                            siu.getPersonid().intValue() == Integer.valueOf(nau)
                    )
            );

            // remove yourself from list
            removedUsers.removeIf(u -> u.getPersonid().intValue() == user.getPersonid().intValue());
            // remove users that are on both lists ( they stay invited and are not interesting to us )
            removedUsers.removeIf(u ->
                    stillInvitedUsers.get().anyMatch(x ->
                            x.getPersonid().intValue() == u.getPersonid().intValue()
                    )
            );




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
                            .and(TEILNAHME.BESPRECHUNGID.eq(besprechung.getBesprechungid()))
                            .execute();
            });

            // add remaining newly added users
            newlyAddedUsers.forEach(nau ->
                        dslContext.insertInto(TEILNAHME)
                                .values(UInteger.valueOf(nau),
                                        besprechung.getBesprechungid())
                                .execute()
            );

            System.out.println(Arrays.toString(besprechungForm.getChosenItemsCount()));
        }

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

        mv.addObject("besprechungForm", besprechungForm);
        mv.addObject("rooms", rooms);
        mv.addObject("gegenstandList", availableItems);
        mv.addObject("userList", userList);
        mv.addObject("bId", besprechung.getBesprechungid());
        return mv;
    }

    @DeleteMapping("deleteBesprechung/{besprechungId}")
    public ModelAndView deleteBesprechung(@PathVariable int besprechungId) {
        return new ModelAndView("redirect:/user/neueBesprechung");
    }

    private Result<Record3<UInteger, String, Integer>> getAvailableItems(Date startDate, Date endDate) {

        Table<BesprechungRecord> meetings = dslContext
                .selectFrom(BESPRECHUNG)
                .where(DSL.timestamp(startDate).between(BESPRECHUNG.ZEITRAUMSTART, BESPRECHUNG.ZEITRAUMENDE))
                .or(DSL.timestamp(endDate).between(BESPRECHUNG.ZEITRAUMSTART, BESPRECHUNG.ZEITRAUMENDE))
                .asTable();


        Table<Record2<UInteger, Integer>> reservedItems = dslContext
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
