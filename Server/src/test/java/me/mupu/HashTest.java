package me.mupu;

import me.mupu.sql.SQLQuery;
import org.jooq.Record;
import org.jooq.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HashTest {

    @Test
    void checkHashIsWorking() throws Exception{
        String pw = "testpassword";
        String hashedPw = Hash.generatePasswordHash(pw);
        Assertions.assertEquals(true, Hash.validatePassword(pw, hashedPw));
    }

    @Test
    void hashTestDatabase() throws Exception{
            String originalPassword;
            Result<Record> data = SQLQuery.getInstance().doCustomQuery("select Nachname as orPassword, passwort from Person, benutzer where benutzer.personid = person.personid");

            System.out.println(data);
            for (Record r :
                    data) {
                originalPassword = (String) r.get(0);
                boolean isCorrect = Hash.validatePassword(originalPassword, (String) r.get(1));
                Assertions.assertEquals(true, isCorrect);
            }
    }


}
