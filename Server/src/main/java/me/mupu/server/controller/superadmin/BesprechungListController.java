package me.mupu.server.controller.superadmin;

import org.jooq.DSLContext;
import org.jooq.Record16;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.generated.Tables.*;

@Controller
@Secured("ROLE_SUPERADMIN")
@RequestMapping("/superadmin")
public class BesprechungListController {

    @Autowired
    private DSLContext dslContext;

    @GetMapping("/besprechungList")
    public ModelAndView getBesprechungen() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("superadmin/besprechungList");


        Result<Record16<UInteger, UInteger, String, Timestamp, Timestamp, String, UInteger, UInteger, UInteger, Byte, Byte, Byte, UInteger, String, String, String>>
                termineMitRaumUndBesitzer =
                dslContext.select(
                        TEILNAHME.PERSONID,
                        BESPRECHUNG.BESPRECHUNGID,
                        BESPRECHUNG.THEMA,
                        BESPRECHUNG.ZEITRAUMSTART,
                        BESPRECHUNG.ZEITRAUMENDE,
                        RAUM.ORT,
                        RAUM.ANZAHLSTUHL,
                        RAUM.ANZAHLTISCH,
                        RAUM.ANZAHLLAPTOP,
                        RAUM.WHITEBOARD,
                        RAUM.BARRIEREFREI,
                        RAUM.KLIMAANLAGE,
                        BESPRECHUNG.BESITZERPID,
                        PERSON.VORNAME.as("BesitzerVorname"),
                        PERSON.NACHNAME.as("BesitzerNachname"),
                        PERSON.EMAIL.as("BesitzerEmail")
                )
                        .from(TEILNAHME)
                        .leftJoin(BESPRECHUNG).using(BESPRECHUNG.BESPRECHUNGID)
                        .leftJoin(RAUM).using(BESPRECHUNG.RAUMID)
                        .leftJoin(PERSON).on(BESPRECHUNG.BESITZERPID.eq(PERSON.PERSONID))
                        .where(BESPRECHUNG.ZEITRAUMENDE.greaterOrEqual(DSL.currentTimestamp()))
                        .groupBy(BESPRECHUNG.BESPRECHUNGID)
                        .orderBy(BESPRECHUNG.ZEITRAUMSTART.asc(), BESPRECHUNG.ZEITRAUMENDE.asc())
                        .fetch();

        List<Result<Record2<String, String>>> teilnehmerList = new ArrayList<>();
        for (Record16<UInteger, UInteger, String, Timestamp, Timestamp, String, UInteger, UInteger, UInteger, Byte, Byte, Byte, UInteger, String, String, String> r:
                termineMitRaumUndBesitzer) {
            Result<Record2<String, String>> teilnehmer =
                    dslContext.select(
                            PERSON.VORNAME,
                            PERSON.NACHNAME
                    )
                            .from(TEILNAHME)
                            .leftJoin(PERSON).using(TEILNAHME.PERSONID)
                            .where(TEILNAHME.BESPRECHUNGID.eq(r.getValue(BESPRECHUNG.BESPRECHUNGID)))
                            .orderBy(PERSON.VORNAME, PERSON.NACHNAME)
                            .fetch();
            teilnehmerList.add(teilnehmer);
        }

        List<Result<Record2<String, UInteger>>> ausstattungsgegenstand = new ArrayList<>();
        for (Record16<UInteger, UInteger, String, Timestamp, Timestamp, String, UInteger, UInteger, UInteger, Byte, Byte, Byte, UInteger, String, String, String> r:
                termineMitRaumUndBesitzer) {
            Result<Record2<String, UInteger>> gegenstand =
                    dslContext.select(
                            AUSSTATTUNGSGEGENSTAND.NAME,
                            AUSLEIHE.ANZAHL
                    )
                            .from(AUSSTATTUNGSGEGENSTAND)
                            .leftJoin(AUSLEIHE).using(AUSSTATTUNGSGEGENSTAND.AUSSTATTUNGSGEGENSTANDID)
                            .where(AUSLEIHE.BESPRECHUNGID.eq(r.getValue(BESPRECHUNG.BESPRECHUNGID)))
                            .fetch();
            ausstattungsgegenstand.add(gegenstand);
        }

        mv.addObject("termineMitRaumUndBesitzer", termineMitRaumUndBesitzer);
        mv.addObject("teilnehmerResultList", teilnehmerList);
        mv.addObject("ausstattungsgegenstandResultList", ausstattungsgegenstand);
        return mv;
    }

}