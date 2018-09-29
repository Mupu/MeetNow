package me.mupu;

import me.mupu.sql.SQLQuerries;


public class ServerMain {
    public static void main(String[] args) throws Exception {
        // disable logo output of jooq
        System.getProperties().setProperty("org.jooq.no-logo", "true");

        System.out.println(SQLQuerries.getInstance().getTermine(6));


    }
}
