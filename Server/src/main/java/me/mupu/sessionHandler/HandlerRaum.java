package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.*;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import jooq.tables.records.BenutzerRecord;
import org.jooq.Record;
import org.jooq.Result;
import org.json.JSONArray;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HandlerRaum implements RequestHandler {
    @Override
    public NanoHTTPD.Response handle(IHTTPSession session, BenutzerRecord userdata) {
        return NanoHTTPD.newFixedLengthResponse(
                Status.OK,
                HttpSessionHandler.CONTENT_TYPE,
                "raum");
    }
}