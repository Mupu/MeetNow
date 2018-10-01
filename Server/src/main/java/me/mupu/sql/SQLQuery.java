package me.mupu.sql;

import me.mupu.Hash;
import org.jooq.*;
import org.jooq.exception.NoDataFoundException;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import java.sql.Connection;
import java.sql.DriverManager;

import static jooq.Tables.*;

public class SQLQuery {

    private static SQLQuery instance = null;
    private DSLContext dslContext;


    private static SQLQuery getInstance() {
        return instance == null ? instance = new SQLQuery() : instance;
    }

    private SQLQuery() {
        // disable logo output of jooq
        System.getProperties().setProperty("org.jooq.no-logo", "true");

        try {
            Class.forName(System.getProperty("driver")).newInstance();
            Connection connection = DriverManager.getConnection(System.getProperty("url"), System.getProperty("user"), System.getProperty("password"));
            dslContext = DSL.using(connection, SQLDialect.MYSQL);
        } catch (Exception e) {
            System.err.println("Connection could not be established");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Checks if the user exists, and if so, if the password is correct.
     *
     * @return Found Record if the above is true. Otherwise null
     */
    public static Record checkLogin(String name, String passwort) {
        Record result = null;
        boolean isCorrect = false;
        try {
            result = getInstance().dslContext.select().from(BENUTZER)
                    .where(BENUTZER.BENUTZERNAME.eq(name))
                    .fetchSingle();

            isCorrect = Hash.validatePassword(passwort, result.get(BENUTZER.PASSWORT));

        } catch (Exception e) {
//            e.printStackTrace();
        }
        if (!isCorrect) {
            System.out.println("Login failed!");
            result = null;
        } else
            System.out.println("Logged in!");
        return result;
    }

    public static Result getTermine(int benutzerId) {
        Result result = null;
        try {
            result = getInstance().dslContext.select().from(TEILNAHME).leftJoin(BESPRECHUNG).using(BESPRECHUNG.BESPRECHUNGID)
                    .where(TEILNAHME.BENUTZERID.eq(UInteger.valueOf(benutzerId)),
                            BESPRECHUNG.ZEITRAUMENDE.greaterOrEqual(DSL.currentTimestamp()))
                    .orderBy(BESPRECHUNG.ZEITRAUMSTART.asc(), BESPRECHUNG.ZEITRAUMENDE.asc())
                    .fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result getPersonen() {
        Result result = null;
        try {
            result = getInstance().dslContext.select().from(PERSON).fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result getRaume() {
        Result result = null;
        try {
            result = getInstance().dslContext.select().from(RAUM).fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result getAusstattungsgegenstande() {
        Result result = null;
        try {
            result = getInstance().dslContext.select().from(AUSSTATTUNGSGEGENSTAND).fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // DEBUG ONLY TODO remove this for deploy
    public static DSLContext getContext() {
        return getInstance().dslContext;
    }

}
