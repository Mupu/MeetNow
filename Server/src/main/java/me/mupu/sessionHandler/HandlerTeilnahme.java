package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import me.mupu.sql.SQLQuery;

public class HandlerTeilnahme implements RequestHandler {
    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session) {

        return NanoHTTPD.newFixedLengthResponse("teilnahme");
    }
}