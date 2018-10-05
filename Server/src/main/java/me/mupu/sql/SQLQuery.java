package me.mupu.sql;

import jooq.tables.records.BenutzerRecord;
import lombok.NonNull;
import me.mupu.Hash;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;

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
            System.err.println("Database-Connection could not be established");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Checks if the user exists, and if so, if the password is correct.
     *
     * @return Found Record if the above is true. Otherwise null
     */
    public static BenutzerRecord checkLogin(final String name, final String password) {
        if (name == null || password == null)
            return null;

        BenutzerRecord result = null;
        boolean isCorrect = false;
        try {
            result = getInstance().dslContext.selectFrom(BENUTZER)
                    .where(BENUTZER.BENUTZERNAME.eq(name))
                    .fetchOne();

            isCorrect = Hash.validatePassword(password, result.getPasswort());
        } catch (Exception ignored) { }

        if (!isCorrect)
            result = null;
        return result;
    }

    /**
     * Will perform an SQL "update" for the given record.
     *
     * @param changedRecord
     * @return true if successful otherwise false
     */
    public static boolean changeUserdata(final BenutzerRecord changedRecord) {
        if (!changedRecord.changed())
            return false;

        try {
            if (Arrays.toString(getInstance().dslContext.batchUpdate(changedRecord).execute()).equals("[1]"))
                return true;
        } catch (Exception ignored) { }

        return false;
    }

    public static Result<Record> getTermine(final int benutzerId) {

        Result<Record> result = null;
        try {
            result = getInstance().dslContext
                    .select()
                    .from(TEILNAHME).leftJoin(BESPRECHUNG).using(BESPRECHUNG.BESPRECHUNGID)
                    .where(TEILNAHME.BENUTZERID.eq(UInteger.valueOf(benutzerId)),
                            BESPRECHUNG.ZEITRAUMENDE.greaterOrEqual(DSL.currentTimestamp()))
                    .orderBy(BESPRECHUNG.ZEITRAUMSTART.asc(), BESPRECHUNG.ZEITRAUMENDE.asc())
                    .fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result<Record> getPersonen() {
        Result result = null;
        try {
            result = getInstance().dslContext.select().from(PERSON).fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result<Record> getRaume() {
        Result result = null;
        try {
            result = getInstance().dslContext.select().from(RAUM).fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result<Record> getAusstattungsgegenstande() {
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
