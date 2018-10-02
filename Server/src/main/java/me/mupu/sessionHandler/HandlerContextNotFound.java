package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import org.jooq.Record;
import org.jooq.Result;

public class HandlerContextNotFound implements RequestHandler {
    @Override
    public NanoHTTPD.Response handle(NanoHTTPD.IHTTPSession session, Result<Record> userData) {
        return response;
    }
}
