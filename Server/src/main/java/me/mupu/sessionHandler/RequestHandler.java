package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;

public interface RequestHandler {
    Response response = NanoHTTPD.newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "File not Found!");

    default Response handle(NanoHTTPD.IHTTPSession session) {
        response.setStatus(Response.Status.NOT_FOUND);
        return response;
    }


}
