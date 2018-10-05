package me.mupu.sql;

import jooq.tables.records.BenutzerRecord;
import me.mupu.Hash;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.types.UInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static jooq.Tables.*;
/**
 * Based on meetnowTestdata.sql script
 */
public class SQLQueryTests {

    @Test
    void TestCheckLogin() {
        String name = "Alex";
        String pw = "Schmidt";

        BenutzerRecord userTest1 = SQLQuery.checkLogin(name, pw);
        Assertions.assertNotNull(userTest1);

        System.out.println(name + ":" + pw + ":true");
        System.out.println(userTest1);

        BenutzerRecord userTest2 = SQLQuery.checkLogin(name, "wrongPassword");
        Assertions.assertNull(userTest2);
        System.out.println(name + ":" + "wrongPassword" + ":false");
    }

    @Test
    void TestChangeUserdata() throws Exception{
        String name = "RoxannePeters";
        String pw = "RoxannePeters";

        BenutzerRecord userdata = SQLQuery.checkLogin(name, pw);

        SQLQuery.changeUserdata(userdata);

        userdata.setBenutzername("rocky");
        userdata.setPasswort(Hash.generatePasswordHash("rocky1"));
        Assertions.assertTrue(SQLQuery.changeUserdata(userdata));

        userdata.setBenutzername("rocky1");
        userdata.setPasswort(Hash.generatePasswordHash("rocky2"));
        Assertions.assertTrue(SQLQuery.changeUserdata(userdata));

        userdata.setBenutzername("RoxannePeters");
        userdata.setPasswort(Hash.generatePasswordHash("RoxannePeters"));
        Assertions.assertTrue(SQLQuery.changeUserdata(userdata));
    }

    @Test
    void TestAddAndDeleteUserToAndFromTermin() {
        BenutzerRecord caller = new BenutzerRecord();
        caller.setBenutzerid(UInteger.valueOf(9));

        Assertions.assertTrue(SQLQuery.addUserToTermin(caller, 2, 4));

        Assertions.assertFalse(SQLQuery.addUserToTermin(caller, 2, 4));

        Assertions.assertTrue(SQLQuery.deleteUserFromTermin(caller, 2, 4));

        Assertions.assertTrue(SQLQuery.deleteUserFromTermin(caller, 2, 4));



    }

}
