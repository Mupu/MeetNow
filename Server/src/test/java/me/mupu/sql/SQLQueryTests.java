package me.mupu.sql;

import org.jooq.Record;
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

        Record userTest1 = SQLQuery.checkLogin(name, pw);
        Assertions.assertNotNull(userTest1);

        System.out.println(name + ":" + pw + ":true");
        System.out.println(userTest1);

        Record userTest2 = SQLQuery.checkLogin(name, "wrongPassword");
        Assertions.assertNull(userTest2);
        System.out.println(name + ":" + "wrongPassword" + ":false");
    }

}
