package me.mupu.server.controller;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.generated.tables.records.AusstattungsgegenstandRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import static org.jooq.generated.Tables.AUSSTATTUNGSGEGENSTAND;

@Controller
public class TestController {
    

    @Autowired
    DSLContext dslContext;

    public Result<AusstattungsgegenstandRecord> getAll() {
        return dslContext
                .selectFrom(AUSSTATTUNGSGEGENSTAND)
                .fetch();
    }

    @RequestMapping("/test")
    public ModelAndView test() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("test");
        mv.addObject("sql", getAll());

        return mv;
    }

}
