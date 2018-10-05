package me.mupu.sql;

import jooq.tables.records.BenutzerRecord;
import jooq.tables.records.BesprechungRecord;
import jooq.tables.records.TeilnahmeRecord;
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
     * @return true if successful otherwise false
     */
    public static boolean changeUserdata(@NonNull final BenutzerRecord changedRecord) {
        if (!changedRecord.changed())
            return false;

        try {
            if (Arrays.toString(getInstance().dslContext.batchUpdate(changedRecord).execute()).equals("[1]"))
                return true;
        } catch (Exception ignored) { }

        return false;
    }

    /**
     * Returns all appointments for the given urserID
     */
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

    public static boolean addUserToTermin(final BenutzerRecord caller, final int besprechungId, final int userIdToAdd) {
        try {
            BesprechungRecord besprechung = getInstance().dslContext
                    .selectFrom(BESPRECHUNG)
                    .where(BESPRECHUNG.BESITZERID.eq(caller.getBenutzerid()))
                    .and(BESPRECHUNG.BESPRECHUNGID.eq(UInteger.valueOf(besprechungId)))
                    .fetchSingle();

            getInstance().dslContext
                    .insertInto(TEILNAHME)
                    .values(getInstance().dslContext    //check if user exists
                            .selectFrom(BENUTZER)
                            .where(BENUTZER.BENUTZERID.eq(UInteger.valueOf(userIdToAdd)))
                            .fetchSingle().getBenutzerid()
                            , besprechung.getBesprechungid())
                    .execute();
            return true;
        } catch (Exception ignored) {
//            ignored.printStackTrace();
        }
        return false;
    }

    public static boolean deleteUserFromTermin(final BenutzerRecord caller, final int besprechungId, final int userIdToRemove) {
        try {
            BesprechungRecord besprechung = getInstance().dslContext
                    .selectFrom(BESPRECHUNG)
                    .where(BESPRECHUNG.BESITZERID.eq(caller.getBenutzerid()))
                    .and(BESPRECHUNG.BESPRECHUNGID.eq(UInteger.valueOf(besprechungId)))
                    .fetchSingle();

            getInstance().dslContext
                    .deleteFrom(TEILNAHME)
                    .where(TEILNAHME.BENUTZERID.eq(
                            getInstance().dslContext    //check if user exists
                                    .selectFrom(BENUTZER)
                                    .where(BENUTZER.BENUTZERID.eq(UInteger.valueOf(userIdToRemove)))
                                    .fetchSingle()
                                    .getBenutzerid()))
                            .and(TEILNAHME.BESPRECHUNGID.eq(besprechung.getBesprechungid()))
                    .execute();
            return true;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return false;
    }

    public static Result<Record> getPersonen() {
        Result<Record> result = null;
        try {
            result = getInstance().dslContext.select().from(PERSON).fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result<Record> getRaume() {
        Result<Record> result = null;
        try {
            result = getInstance().dslContext.select().from(RAUM).fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Result<Record> getAusstattungsgegenstande() {
        Result<Record> result = null;
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
