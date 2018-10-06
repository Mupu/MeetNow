package me.mupu.sessionHandler;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Cookie;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import jooq.tables.records.BenutzerRecord;
import lombok.NonNull;
import me.mupu.Hash;
import me.mupu.sql.SQLQuery;
import org.jooq.types.UByte;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static me.mupu.sessionHandler.HttpSessionHandler.COOKIE_PASSWORD;
import static me.mupu.sessionHandler.HttpSessionHandler.COOKIE_USERNAME;

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
     * <p>
     * DESKTOP: Tells client to show "change username/password" window
     */
    private Response get(IHTTPSession session, BenutzerRecord userdata) {
        Response response;

        if (userdata.getAccountstatus().intValue() == 1) {
            // user wants to change data
            if (session.getCookies().read(HttpSessionHandler.COOKIE_CONNECTION_TYPE).equals("desktop")) {
                // uses desktop
                // client app shouldnt use this site. it should only use put
            } else {
                // uses web
                String s = "didnt work";
                try {
                    byte[] encoded = Files.readAllBytes(Paths.get("src/main/resources/me/mupu/sessionHandler/html/Settings.html"));
                    s = new String(encoded, StandardCharsets.UTF_8);
                } catch (Exception ignored) {
                }
                return NanoHTTPD.newFixedLengthResponse(
                        NanoHTTPD.Response.Status.OK,
                        HttpSessionHandler.CONTENT_TYPE,
                        s);
            }
        } else {
            // first login user has to change data
            if (session.getCookies().read(HttpSessionHandler.COOKIE_CONNECTION_TYPE).equals("desktop")) {
                // uses desktop
                //todo tell clinet to open password and username change window.
                //todo on completing it it should send put request
            } else {
                // uses web
                String s = "didnt work";
                try {
                    byte[] encoded = Files.readAllBytes(Paths.get("src/main/resources/me/mupu/sessionHandler/html/Login.html"));
                    s = new String(encoded, StandardCharsets.UTF_8);
                } catch (Exception ignored) {
                }
                return NanoHTTPD.newFixedLengthResponse(
                        NanoHTTPD.Response.Status.OK,
                        HttpSessionHandler.CONTENT_TYPE,
                        s);
            }
        }

        response = NanoHTTPD.newFixedLengthResponse(Status.OK, HttpSessionHandler.CONTENT_TYPE, userdata.toString());

        return response;
    }

    /**
     * APP: Will update userdata in database.
     * Also provides a 'succesfully changed data' data html page
     * <p>
     * DESKTOP: Will update userdata in database
     */
    private Response put(IHTTPSession session, BenutzerRecord userdata) {
        Response response;

        try {
            if (session.getCookies().read(COOKIE_RESET_USERNAME) != null
                    && session.getCookies().read(COOKIE_RESET_USERNAME) != null) {

                userdata.setBenutzername(session.getCookies().read(COOKIE_RESET_USERNAME));
                userdata.setPasswort(Hash.generatePasswordHash(session.getCookies().read(COOKIE_RESET_PASSWORD)));
                userdata.setAccountstatus(UByte.valueOf(1));
                if (SQLQuery.changeUserdata(userdata)) {
                    session.getCookies().set(new Cookie(COOKIE_USERNAME,
                                    session.getCookies().read(COOKIE_RESET_USERNAME), 60 * 60 * 24 * 2)
                                    .setSecure(true)
                                    .setSameSite("Strict"));
                    session.getCookies().set(new Cookie(COOKIE_PASSWORD,
                                    session.getCookies().read(COOKIE_RESET_PASSWORD), 60 * 60 * 24 * 2)
                                    .setSecure(true)
                                    .setSameSite("Strict"));

                    session.getCookies().delete(COOKIE_RESET_USERNAME);
                    session.getCookies().delete(COOKIE_RESET_PASSWORD);

                    Response r = NanoHTTPD.newFixedLengthResponse(
                            Status.OK,
                            HttpSessionHandler.CONTENT_TYPE,
                            userdata.toString());
                    session.getCookies().unloadQueue(r);

                    return r;
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