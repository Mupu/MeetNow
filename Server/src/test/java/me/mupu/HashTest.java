package me.mupu;

import me.mupu.sql.SQLQuery;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static jooq.Tables.*;

/**
 * Based on meetnowTestdata.sql script
 */
class HashTest {

    @Test
    void checkHashIsWorking() throws Exception {
        String pw = "testpassword";
        System.out.println(pw);
        String hashedPw = Hash.generatePasswordHash(pw);
        System.out.println(hashedPw);
        Assertions.assertEquals(true, Hash.validatePassword(pw, hashedPw));
    }

    @Test
    void hashTestDatabase() throws Exception {
        String originalPassword;

        Result<Record2<String, String>> data = SQLQuery.getContext()
                .select(PERSON.NACHNAME.as("orPassword"), BENUTZER.PASSWORT)
                .from(PERSON, BENUTZER)
                .where(BENUTZER.PERSONID.eq(PERSON.PERSONID))
                .fetch();

        System.out.println(data);
        for (Record r :
                data) {
            originalPassword = r.get(PERSON.NACHNAME.as("orPassword"));
            if (originalPassword.equals("Peters"))
                originalPassword = "RoxannePeters";

            boolean isCorrect = Hash.validatePassword(originalPassword, r.get(BENUTZER.PASSWORT));
            System.out.println(originalPassword + ": " + isCorrect);
            Assertions.assertTrue(isCorrect);
        }
    }


}
