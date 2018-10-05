package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import jooq.tables.records.BenutzerRecord;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HandlerLogin implements RequestHandler {
    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session, BenutzerRecord userdata) {
        String s = "didnt work";
        try {
            byte[] encoded = Files.readAllBytes(Paths.get("src/main/resources/me/mupu/sessionHandler/html/Login.html"));
            s = new String(encoded, StandardCharsets.UTF_8);
        } catch (Exception ignored) {}
        return NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.OK,
                HttpSessionHandler.CONTENT_TYPE,
                s);
    }
}
