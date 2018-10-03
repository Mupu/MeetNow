package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.*;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import jooq.tables.records.BenutzerRecord;
import jooq.tables.records.TeilnahmeRecord;
import me.mupu.sql.SQLQuery;
import org.jooq.Record;
import org.jooq.Result;

import static jooq.Tables.*;

public class HandlerTeilnahme implements RequestHandler {
    @Override
    public Response handle(IHTTPSession session, BenutzerRecord userData) {
        Response response;

        switch (session.getMethod()) {
            case GET:
                response = get(userData);
                break;

            case POST:
                response = post();
                break;

            case DELETE:
                response = delete();
                break;

            default:
                response = NanoHTTPD.newFixedLengthResponse(
                        Status.METHOD_NOT_ALLOWED,
                        HttpSessionHandler.CONTENT_TYPE,
                        "Method not allowed!");
        }

        return response;
    }

    private Response get(BenutzerRecord userData) {
        Result<Record> termin = SQLQuery.getTermine(userData.getBenutzerid().intValue());
        System.out.println(termin.into(TEILNAHME));
        System.out.println(termin.into(BESPRECHUNG));

        return NanoHTTPD.newFixedLengthResponse(
                Status.OK,
                HttpSessionHandler.CONTENT_TYPE,
                termin.toString()
        );
    }

    private Response post() {
        return null;
    }

    private Response delete() {
        return null;
    }
}