/*
 * Copyright (C) 2016-2017 Authlete, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package com.authlete.jaxrs.server.api;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;

import com.amazonaws.services.lambda.runtime.Context;
import com.authlete.common.api.AuthleteApi;
import com.authlete.common.api.AuthleteApiFactory;
import com.authlete.common.conf.AuthleteConfiguration;
import com.authlete.common.types.User;
import com.authlete.jaxrs.AuthorizationDecisionHandler;
import com.authlete.jaxrs.server.db.UserDao;

import lombok.extern.slf4j.Slf4j;
import nu.mine.kino.AuthleteUtils;
import nu.mine.kino.servlets.HttpServletRequestImpl;

/**
 * The endpoint that receives a request from the form in the authorization page.
 *
 * @author Takahiko Kawasaki
 */
@Slf4j
public class LambdaAuthorizationDecisionEndpoint {

    public Response post(
            AuthorizationDecisionEndpointRequestClass gateWayRequest,
            Context context) {
        // public Response post(@Context HttpServletRequest request,
        // MultivaluedMap<String, String> parameters) {

        HttpServletRequest request = new HttpServletRequestImpl(gateWayRequest,
                gateWayRequest.getSessionId());

        MultivaluedMap<String, String> parameters = new MultivaluedStringMap();
        parameters.add("loginId", gateWayRequest.getLoginId());
        parameters.add("password", gateWayRequest.getPassword());
        parameters.add("authorized", gateWayRequest.getAuthorized()); // なんかこのフラグが必要。登りPOSTの電文に。

        // original code
        // Get the existing session.
        HttpSession session = getSession(request);

        // Retrieve some variables from the session. See the implementation
        // of AuthorizationRequestHandlerSpiImpl.getAuthorizationPage().
        String ticket = (String) takeAttribute(session, "ticket");
        log.debug("ticket: {}", ticket);
        // String ticket = gateWayRequest.getTicket(); // チケットパラメータが必要。
        String[] claimNames = (String[]) takeAttribute(session, "claimNames");
        String[] claimLocales = (String[]) takeAttribute(session,
                "claimLocales");
        User user = getUser(session, parameters);
        Date authTime = (Date) session.getAttribute("authTime");

        AuthleteConfiguration configuration = AuthleteUtils.createConfig();
        AuthleteApi api = AuthleteApiFactory.create(configuration);

        try {
            // Create a handler.
            AuthorizationDecisionHandler handler = new AuthorizationDecisionHandler(
                    api, new AuthorizationDecisionHandlerSpiImpl(parameters,
                            user, authTime));

            log.debug("user: {}", user.toString());
            log.debug("ticket: {}", ticket);

            // Delegate the task to the handler.
            return handler.handle(ticket, claimNames, claimLocales);
        } catch (WebApplicationException e) {
            // An error occurred in the handler.
            onError(e);

            // Convert the error to a Response.
            return e.getResponse();
        }

    }

    /**
     * Get the existing session.
     */
    private HttpSession getSession(HttpServletRequest request) {
        // Get the existing session.
        HttpSession session = request.getSession(false);

        // If there exists a session.
        if (session != null) {
            // OK.
            return session;
        }

        // A session does not exist. Make a response of "400 Bad Request".
        String message = "A session does not exist.";

        Response response = Response.status(Status.BAD_REQUEST).entity(message)
                .type(MediaType.TEXT_PLAIN).build();

        throw new WebApplicationException(message, response);
    }

    /**
     * Look up an end-user.
     */
    private static User getUser(HttpSession session,
            MultivaluedMap<String, String> parameters) {
        // Look up the user in the session to see if they're already logged in.
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser != null) {
            return sessionUser;
        }

        // Look up an end-user who has the login credentials.
        User loginUser = UserDao.getByCredentials(
                parameters.getFirst("loginId"),
                parameters.getFirst("password"));

        if (loginUser != null) {
            session.setAttribute("user", loginUser);
            session.setAttribute("authTime", new Date());
        }

        return loginUser;
    }

    /**
     * Get the value of an attribute from the given session and remove the
     * attribute from the session after the retrieval.
     */
    private Object takeAttribute(HttpSession session, String key) {
        // Retrieve the value from the session.
        Object value = session.getAttribute(key);

        // Remove the attribute from the session.
        session.removeAttribute(key);

        // Return the value of the attribute.
        return value;
    }

    protected void onError(WebApplicationException exception) {
        exception.printStackTrace();
    }

}
