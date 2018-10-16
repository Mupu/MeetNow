package me.mupu.server.controller.admin;

import org.jooq.DSLContext;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static org.jooq.generated.Tables.AUSSTATTUNGSGEGENSTAND;
import static org.jooq.generated.Tables.RAUM;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/admin/raum")
public class RaumController {

    @Autowired
    private DSLContext dslContext;

    @GetMapping()
    public ModelAndView admin() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("admin/raum");

        mv.addObject("items", dslContext.selectFrom(RAUM).fetch());
        return mv;
    }

    @PutMapping("/edit")
    public ModelAndView edit(@RequestParam int id,
                             @RequestParam String ort,
                             @RequestParam int anzahlStuhl,
                             @RequestParam int anzahlTisch,
                             @RequestParam int anzahlLaptop,
                             @RequestParam(required = false) boolean whiteboard,
                             @RequestParam(required = false) boolean barrierefrei,
                             @RequestParam(required = false) boolean klimaanlage) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/admin/raum");

        dslContext.update(RAUM)
                .set(RAUM.ORT, ort)
                .set(RAUM.ANZAHLSTUHL, UInteger.valueOf(anzahlStuhl))
                .set(RAUM.ANZAHLTISCH, UInteger.valueOf(anzahlTisch))
                .set(RAUM.ANZAHLLAPTOP, UInteger.valueOf(anzahlLaptop))
                .set(RAUM.WHITEBOARD, (byte) (whiteboard ? 1 : 0))
                .set(RAUM.BARRIEREFREI, (byte) (barrierefrei ? 1 : 0))
                .set(RAUM.KLIMAANLAGE, (byte) (klimaanlage ? 1 : 0))
                .where(RAUM.RAUMID.eq(UInteger.valueOf(id)))
                .execute();


        return mv;
    }

    @DeleteMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable(name = "id") int id) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/admin/raum");

        dslContext.deleteFrom(RAUM)
                .where(RAUM.RAUMID.eq(UInteger.valueOf(id)))
                .execute();

        return mv;
    }

    @PostMapping("/add")
    public ModelAndView add(@RequestParam String ort,
                            @RequestParam int anzahlStuhl,
                            @RequestParam int anzahlTisch,
                            @RequestParam int anzahlLaptop,
                            @RequestParam(required = false) boolean whiteboard,
                            @RequestParam(required = false) boolean barrierefrei,
                            @RequestParam(required = false) boolean klimaanlage) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/admin/raum");

        dslContext.insertInto(RAUM)
                .columns(RAUM.ORT,
                        RAUM.ANZAHLSTUHL,
                        RAUM.ANZAHLTISCH,
                        RAUM.ANZAHLLAPTOP,
                        RAUM.WHITEBOARD,
                        RAUM.BARRIEREFREI,
                        RAUM.KLIMAANLAGE)
                .values(ort,
                        UInteger.valueOf(anzahlStuhl),
                        UInteger.valueOf(anzahlTisch),
                        UInteger.valueOf(anzahlLaptop),
                        (byte) (whiteboard ? 1 : 0),
                        (byte) (barrierefrei ? 1 : 0),
                        (byte) (klimaanlage ? 1 : 0))
                .execute();


        return mv;
    }
}
