package me.mupu.server.controller.user;

import me.mupu.server.form.BesprechungForm;
import me.mupu.server.model.CustomUserDetails;
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

import javax.validation.Valid;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
        mv.addObject("userId", user.getPersonid().intValue());
        mv.addObject("rooms", getAvailableRooms(besprechungForm.getZeitraumStart(), besprechungForm.getZeitraumEnde()));
        mv.addObject("gegenstandList", getAvailableItems(besprechungForm.getZeitraumStart(), besprechungForm.getZeitraumEnde()));
        return mv;
    }

    private Result<Record3<UInteger, String, Integer>> getAvailableItems(Date startDate, Date endDate) {

        Table<BesprechungRecord> meetings= dslContext
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
                .groupBy(combined.field("AusstattungsgegenstandId"))
                ;


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
        Table<BesprechungRecord> bussyRooms= dslContext
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

    @PutMapping()
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
                            BESPRECHUNG.BESITZERID,
                            BESPRECHUNG.THEMA,
                            BESPRECHUNG.ZEITRAUMSTART,
                            BESPRECHUNG.ZEITRAUMENDE
                    )
                    .values(UInteger.valueOf(besprechungForm.getRaumId()),
                            user.getBenutzerid(),
                            besprechungForm.getThema(),
                            Timestamp.valueOf(sdf.format(besprechungForm.getZeitraumStart())),
                            Timestamp.valueOf(sdf.format(besprechungForm.getZeitraumEnde()))
                    )
                    .returning()
                    .fetchOne();

            // add termine
            dslContext.insertInto(TEILNAHME)
                    .values(user.getBenutzerid(), besprechung.getBesprechungid())
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

        mv.addObject("userList", dslContext.selectFrom(PERSON).orderBy(PERSON.VORNAME, PERSON.NACHNAME).fetch());
        mv.addObject("userId", user.getPersonid().intValue());
        mv.addObject("gegenstandList", getAvailableItems(besprechungForm.getZeitraumStart(), besprechungForm.getZeitraumEnde()));
        mv.addObject("rooms", getAvailableRooms(besprechungForm.getZeitraumStart(), besprechungForm.getZeitraumEnde()));
        return mv;
    }

    @DeleteMapping()
    public ModelAndView deleteBesprechung() {
        return new ModelAndView("redirect:/user/neueBesprechung");
    }
}
