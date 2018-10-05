package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import jooq.tables.records.BenutzerRecord;
import lombok.NonNull;
import me.mupu.Hash;
import me.mupu.sql.SQLQuery;

public class HandlerSettings implements RequestHandler {
    private static String COOKIE_RESET_USERNAME = "new_username";
    private static String COOKIE_RESET_PASSWORD = "new_password";

    @Override
    public Response handle(IHTTPSession session, BenutzerRecord userdata) {
        Response response;

        switch (session.getMethod()) {
            case GET:
                response = get(session, userdata);
                break;

            case PUT:
                response = put(session, userdata);
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
     * DESKTOP: Tells client to show "change username/password" window
     */
    private Response get(IHTTPSession session, BenutzerRecord userdata) {
        Response response = null;

        if (userdata.getAccountstatus().intValue() == 1) {
            // user wants to change data
            if (session.getCookies().read(HttpSessionHandler.COOKIE_CONNECTION_TYPE).equals("desktop")) {
                // uses desktop
                // client app shouldnt use this site. it should only use put
            } else {
                // uses web
                //todo display input box for password and name. ok button sends put request to change data
            }
        } else {
            // first login user has to change data
            if (session.getCookies().read(HttpSessionHandler.COOKIE_CONNECTION_TYPE).equals("desktop")) {
                // uses desktop
                //todo tell clinet to open password and username change window.
                //todo on completing it it should send put request
            } else {
                // uses web
                //todo same window as for change user stat but with an extra lable
                // todo that says you have to change your username and login
            }
        }

        response = NanoHTTPD.newFixedLengthResponse(Status.OK, HttpSessionHandler.CONTENT_TYPE, userdata.toString());

        return response;
    }

    /**
     * APP: Will update userdata in database.
     * Also provides a 'succesfully changed data' data html page
     *
     * DESKTOP: Will update userdata in database
     */
    private Response put(IHTTPSession session, BenutzerRecord userdata) {
        Response response;

        try {
            if (session.getCookies().read(COOKIE_RESET_USERNAME) != null
                    && session.getCookies().read(COOKIE_RESET_USERNAME) != null) {

                userdata.setBenutzername(session.getCookies().read(COOKIE_RESET_USERNAME));
                userdata.setPasswort(Hash.generatePasswordHash(session.getCookies().read(COOKIE_RESET_USERNAME)));
                if (SQLQuery.changeUserdata(userdata)) {
                    session.getCookies().delete(COOKIE_RESET_USERNAME);
                    session.getCookies().delete(COOKIE_RESET_PASSWORD);

                    return NanoHTTPD.newFixedLengthResponse(
                            Status.OK,
                            HttpSessionHandler.CONTENT_TYPE,
                            userdata.toString()
                    );

                }
            }
        } catch (Exception ignored) {
        }

        userdata.reset();

        response = NanoHTTPD.newFixedLengthResponse(
                Status.BAD_REQUEST,
                HttpSessionHandler.CONTENT_TYPE,
                userdata.toString()
        );
        return response;
    }
}