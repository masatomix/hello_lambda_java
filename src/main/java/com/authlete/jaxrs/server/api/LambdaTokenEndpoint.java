/*
 * Copyright (C) 2016 Authlete, Inc.
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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.authlete.common.api.AuthleteApi;
import com.authlete.common.api.AuthleteApiFactory;
import com.authlete.common.conf.AuthleteConfiguration;
import com.authlete.jaxrs.TokenRequestHandler;

import lombok.extern.slf4j.Slf4j;
import nu.mine.kino.AuthleteUtils;
import nu.mine.kino.servlets.HttpServletRequestImpl;

/**
 * An implementation of OAuth 2.0 token endpoint with OpenID Connect support.
 *
 * @see <a href="http://tools.ietf.org/html/rfc6749#section-3.2" >RFC 6749, 3.2.
 *      Token Endpoint</a>
 *
 * @see <a href=
 *      "http://openid.net/specs/openid-connect-core-1_0.html#HybridTokenEndpoint"
 *      >OpenID Connect Core 1.0, 3.3.3. Token Endpoint</a>
 *
 * @author Takahiko Kawasaki
 */
@Slf4j
public class LambdaTokenEndpoint {
    /**
     * The token endpoint for {@code POST} method.
     *
     * <p>
     * <a href="http://tools.ietf.org/html/rfc6749#section-3.2">RFC 6749, 3.2.
     * Token Endpoint</a> says:
     * </p>
     *
     * <blockquote> <i>The client MUST use the HTTP "POST" method when making
     * access token requests.</i> </blockquote>
     *
     * <p>
     * <a href="http://tools.ietf.org/html/rfc6749#section-2.3">RFC 6749, 2.3.
     * Client Authentication</a> mentions (1) HTTP Basic Authentication and (2)
     * {@code client_id} &amp; {@code client_secret} parameters in the request
     * body as the means of client authentication. This implementation supports
     * the both means.
     * </p>
     *
     * @see <a href="http://tools.ietf.org/html/rfc6749#section-3.2" >RFC 6749,
     *      3.2. Token Endpoint</a>
     */
    public Response post(TokenEndpointRequestClass gateWayRequest,
            Context context) {

        // public Response post(
        // @HeaderParam(HttpHeaders.AUTHORIZATION) String authorization,
        // MultivaluedMap<String, String> parameters) {

        // LambdaLogger logger = context.getLogger();
        // logger.log(context.toString());
        // logger.log("count:" + gateWayRequest.toString());

        HttpServletRequest request = new HttpServletRequestImpl(gateWayRequest);

        MultivaluedMap<String, String> parameters = new MultivaluedStringMap();
        parameters.add("redirect_uri", gateWayRequest.getRedirect_uri());
        parameters.add("grant_type", gateWayRequest.getGrant_type());
        parameters.add("client_id", gateWayRequest.getClient_id());
        parameters.add("client_secret", gateWayRequest.getClient_secret());
        parameters.add("code", gateWayRequest.getCode());

        // Handle the token request.
        // return handle(AuthleteApiFactory.getDefaultApi(),
        // new TokenRequestHandlerSpiImpl(), parameters, authorization);

        String authorization = gateWayRequest.getAuthorization(); // セットしないと

        try {
            AuthleteConfiguration configuration = AuthleteUtils.createConfig();
            AuthleteApi api = AuthleteApiFactory.create(configuration);

            // Create a handler.
            TokenRequestHandler handler = new TokenRequestHandler(api,
                    new TokenRequestHandlerSpiImpl());

            // Delegate the task to the handler.
            return handler.handle(parameters, authorization);
        } catch (WebApplicationException e) {
            // An error occurred in the handler.
            onError(e);

            // Convert the error to a Response.
            return e.getResponse();
        }

    }



    /**
     * Called when the internal request handler raises an exception. The default
     * implementation of this method calls {@code
     * printStackTrace()} of the given exception instance and does nothing else.
     * Override this method as necessary.
     *
     * @param exception
     *            An exception thrown by the internal request handler.
     */
    protected void onError(WebApplicationException exception) {
        exception.printStackTrace();
    }

}
