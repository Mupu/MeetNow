package me.mupu.sql;

import org.jooq.*;
import org.jooq.exception.NoDataFoundException;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import java.sql.Connection;
import java.sql.DriverManager;

import static jooq.Tables.*;

public class SQLQuerries {

    private static SQLQuerries instance = null;
    private DSLContext dslContext;

    public static SQLQuerries getInstance() {
        return instance == null ? instance = new SQLQuerries() : instance;
    }

    private SQLQuerries() {
        // disable logo output of jooq
        System.getProperties().setProperty("org.jooq.no-logo", "true");

        try {
            Class.forName(System.getProperty("driver")).newInstance();
            Connection connection = DriverManager.getConnection(System.getProperty("url"), System.getProperty("user"), System.getProperty("password"));
            dslContext = DSL.using(connection, SQLDialect.MYSQL);
        } catch (Exception e) {
            System.err.println("Connection could not be established");
            System.exit(1);
        }
    }

    public Record checkLogin(String name, String passwort) {
        Record result = null;
        try {
            result = dslContext.select().from(BENUTZER)
                    .where(BENUTZER.BENUTZERNAME.eq(name),
                            BENUTZER.PASSWORT.eq(passwort))
                    .fetchSingle();
        } catch (NoDataFoundException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Result getTermine(int benutzerId) {
        Result result = null;
        try {
            result = dslContext.select().from(TEILNAHME).leftJoin(BESPRECHUNG).using(BESPRECHUNG.BESPRECHUNGID)
                    .where(TEILNAHME.BENUTZERID.eq(UInteger.valueOf(benutzerId)),
                            BESPRECHUNG.ZEITRAUMENDE.greaterOrEqual(DSL.currentTimestamp()))
                    .orderBy(BESPRECHUNG.ZEITRAUMSTART.asc(), BESPRECHUNG.ZEITRAUMENDE.asc())
                    .fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Result getPersonen() {
        Result result = null;
        try {
            result = dslContext.select().from(PERSON).fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Result getRaume() {
        Result result = null;
        try {
            result = dslContext.select().from(RAUM).fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Result getAusstattungsgegenstande() {
        Result result = null;
        try {
            result = dslContext.select().from(AUSSTATTUNGSGEGENSTAND).fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
