package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import jooq.tables.records.BenutzerRecord;

public class HandlerSettings implements RequestHandler {
    @Override
    public Response handle(IHTTPSession session, BenutzerRecord userdata) {
        Response response;

        switch (session.getMethod()) {
            case GET:
                response = get(session, userdata);
                break;

            case POST:
                response = post(session, userdata);
                break;

            default:
                response = NanoHTTPD.newFixedLengthResponse(
                        Status.METHOD_NOT_ALLOWED,
                        HttpSessionHandler.CONTENT_TYPE,
                        "Method not allowed!");
        }

        return response;
    }

    /**
     * APP: Provides the html pages to change the username/password
     *
     * DESKTOP: Tells client to change username/password
     */
    private Response get(IHTTPSession session, BenutzerRecord userdata) {
        Response response = null;

        if (userdata.getAccountstatus().intValue() == 1) {
            // user wants to change data
            if (session.getCookies().read(HttpSessionHandler.COOKIE_CONNECTION_TYPE).equals("desktop")) {
                // uses desktop
                // client app shouldnt use this site. it should only use post
            } else {
                // uses web
                //todo display input box for password and name. ok button sends post request to change data
            }
        } else {
            // first login user has to change data
            if (session.getCookies().read(HttpSessionHandler.COOKIE_CONNECTION_TYPE).equals("desktop")) {
                // uses desktop
                //todo tell clinet to open password and username change window.
                //todo on completing it it should send post request
            } else {
                // uses web
                //todo same window as for change user stat but with an extra lable
                // todo that says you have to change your username and login
            }
        }

        return response;
    }

    /**
     * APP: Will update userdata in database and sets new cookies
     *      also provides a 'succesfully changed data' data html page
     *
     * DESKTOP: Will update userdata in database and sets new cookies
     */
    private Response post(IHTTPSession session, BenutzerRecord userdata) {
        Response response = null;

        if (userdata.getAccountstatus().intValue() == 1) {
            // user wants to change data
            if (session.getCookies().read(HttpSessionHandler.COOKIE_CONNECTION_TYPE).equals("desktop")) {
                // uses desktop
            } else {
                // uses web
            }
        } else {
            // first login user has to change data
            if (session.getCookies().read(HttpSessionHandler.COOKIE_CONNECTION_TYPE).equals("desktop")) {
                // uses desktop
            } else {
                // uses web
            }
        }

        return response;
    }
}