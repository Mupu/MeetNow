package me.mupu.server;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.generated.tables.records.AusstattungsgegenstandRecord;
import org.jooq.types.UInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.jooq.generated.Tables.AUSSTATTUNGSGEGENSTAND;

/**
 *  TESTING
 */
@Repository
public class AusstattungsgegenstandRepository {

    @Autowired
    private DSLContext dslContext;


    public Result<AusstattungsgegenstandRecord> getAll() {
        return dslContext
                .selectFrom(AUSSTATTUNGSGEGENSTAND)
                .fetch();
    }

    public int saveGegenstand(String name, int anzahl) {
        return dslContext
                .insertInto(AUSSTATTUNGSGEGENSTAND)
                .columns(AUSSTATTUNGSGEGENSTAND.NAME, AUSSTATTUNGSGEGENSTAND.ANZAHL)
                .values(name, UInteger.valueOf(anzahl))
                .returning(AUSSTATTUNGSGEGENSTAND.AUSSTATTUNGSGEGENSTANDID)
                .execute();

    }

}
