package me.mupu;

import me.mupu.sql.SQLQuerries;
import org.jooq.*;
import org.jooq.impl.DSL;


public class ServerMain {
    public static void main(String[] args) throws Exception {
        System.getProperties().setProperty("org.jooq.no-logo", "true");
        SQLQuerries querry = new SQLQuerries();
        Result r = querry.termine(6);

        System.out.println(DSL.currentTimestamp());
        System.out.println(r);

    }
}
