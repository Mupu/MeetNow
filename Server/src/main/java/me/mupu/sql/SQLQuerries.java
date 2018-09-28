package me.mupu.sql;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;
import java.sql.Connection;
import java.sql.DriverManager;

import static jooq.Tables.*;

public class SQLQuerries {

    public SQLQuerries() {

    }

    public Result login(String name, String passwort) {
        Result result = null;
        try {
            Class.forName(System.getProperty("driver")).newInstance();
            Connection connection = DriverManager.getConnection(System.getProperty("url"), System.getProperty("user"), System.getProperty("password"));
            DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL);
            result = dslContext.select().from(BENUTZER)
                    .where(BENUTZER.BENUTZERNAME.eq(name),
                            BENUTZER.PASSWORT.eq(passwort))
                    .fetch();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Result termine(int benutzerId) {
        Result result = null;
        try {
            Class.forName(System.getProperty("driver")).newInstance();
            Connection connection = DriverManager.getConnection(System.getProperty("url"), System.getProperty("user"), System.getProperty("password"));
            DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL);
            result = dslContext.select().from(TEILNAHME).leftJoin(BESPRECHUNG).using(BESPRECHUNG.BESPRECHUNGID)
//                    .where(TEILNAHME.BENUTZERID.eq(UInteger.valueOf(benutzerId)),
//                            BESPRECHUNG.ZEITRAUMENDE.greaterOrEqual(DSL.currentTimestamp()))
                    .orderBy(BESPRECHUNG.ZEITRAUMSTART.asc(), BESPRECHUNG.ZEITRAUMENDE.asc())
                    .fetch();
            System.out.println(DSL.currentTimestamp());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
