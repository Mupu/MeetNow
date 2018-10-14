package me.mupu.server.controller.admin;

import org.jooq.DSLContext;
import org.jooq.types.UInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import static org.jooq.generated.Tables.*;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/admin/ausstattung")
public class AusstattungController {

    @Autowired
    private DSLContext dslContext;

    @GetMapping()
    public ModelAndView admin() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("admin/ausstattung");

        mv.addObject("items", dslContext.selectFrom(AUSSTATTUNGSGEGENSTAND).fetch());
        return mv;
    }

    @PostMapping("/edit")
    public ModelAndView edit(@RequestParam(name = "id") int id,
                             @RequestParam(name = "name") String name,
                             @RequestParam(name = "anzahl") String anzahl) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/admin/ausstattung");

        System.out.println(id);
        System.out.println(name);
        System.out.println(anzahl);
        dslContext.update(AUSSTATTUNGSGEGENSTAND)
                .set(AUSSTATTUNGSGEGENSTAND.NAME, name)
                .set(AUSSTATTUNGSGEGENSTAND.ANZAHL, UInteger.valueOf(anzahl))
                .where(AUSSTATTUNGSGEGENSTAND.AUSSTATTUNGSGEGENSTANDID.eq(UInteger.valueOf(id)))
                .execute();

        return mv;
    }

    @PostMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable(name = "id") int id) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/admin/ausstattung");

        System.out.println("delete: " + id);
        dslContext.deleteFrom(AUSSTATTUNGSGEGENSTAND)
                .where(AUSSTATTUNGSGEGENSTAND.AUSSTATTUNGSGEGENSTANDID.eq(UInteger.valueOf(id)))
                .execute();

        return mv;
    }

    @PostMapping("/add")
    public ModelAndView add(@RequestParam(name = "name") String name,
                               @RequestParam(name = "anzahl") int anzahl) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/admin/ausstattung");

        System.out.println("add: " + name + " " + anzahl);
        dslContext.insertInto(AUSSTATTUNGSGEGENSTAND)
                .columns(AUSSTATTUNGSGEGENSTAND.NAME,
                        AUSSTATTUNGSGEGENSTAND.ANZAHL)
                .values(name,
                        UInteger.valueOf(anzahl))
                .execute();


        return mv;
    }
}
