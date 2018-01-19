/******************************************************************************
 * Copyright (c) 2010 Masatomi KINO and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *      Masatomi KINO - initial API and implementation
 * $Id$
 ******************************************************************************/

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
import com.authlete.jaxrs.AuthorizationRequestHandler;

import lombok.extern.slf4j.Slf4j;
import nu.mine.kino.AuthleteUtils;
import nu.mine.kino.servlets.HttpServletRequestImpl;

/**
 * @author Masatomi KINO
 * @version $Revision$
 */
@Slf4j
public class LambdaAuthorizationEndpoint {
    // public String get2(AuthorizationEndpointRequestClass gateWayRequest,
    // Context context) {
    // LambdaLogger logger = context.getLogger();
    // logger.log(context.toString());
    // logger.log("request:" + gateWayRequest.toString());
    // return String.valueOf("Hello:" + gateWayRequest.toString());
    // }

    // http://docs.aws.amazon.com/ja_jp/lambda/latest/dg/java-handler-io-type-pojo.html
    public Response get(AuthorizationEndpointRequestClass gateWayRequest,
            Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log(context.toString());
        logger.log("count:" + gateWayRequest.toString());

        HttpServletRequest request = new HttpServletRequestImpl(gateWayRequest);

        MultivaluedMap<String, String> parameters = new MultivaluedStringMap();
        parameters.add("client_id", gateWayRequest.getClient_id());
        parameters.add("nonce", gateWayRequest.getNonce());
        parameters.add("redirect_uri", gateWayRequest.getRedirect_uri());
        parameters.add("response_type", gateWayRequest.getResponse_type());
        parameters.add("scope", gateWayRequest.getScope());
        parameters.add("state", gateWayRequest.getState());

        try {
            // Create a handler.
            AuthleteConfiguration configuration = AuthleteUtils.createConfig();
            AuthleteApi api = AuthleteApiFactory.create(configuration);
            // AuthleteApi api = new AuthleteApiImpl(configuration); //
            // 暫定.上のコードだとJaxRSのAuthleteApiImplが取れて、なぜかJerseyがLambda上でエラーを起こす
            AuthorizationRequestHandler handler = new AuthorizationRequestHandler(
                    api, new AuthorizationRequestHandlerSpiImpl(request));

            // Delegate the task to the handler.
            Response response = handler.handle(parameters);
            
            log.debug("sessionId:{}", request.getSession().getId());
            log.debug("Ticket:{}", request.getSession().getAttribute("ticket"));
            
            System.out.println("------ kino log start -------");
            // System.out.println(response);
            // System.out.println(JSONUtils.toPrettyStr(response));
            System.out.println("------ kino log end -------");
            return response;
        } catch (WebApplicationException e) {
            // An error occurred in the handler.
            onError(e);

            // Convert the error to a Response.
            return e.getResponse();
            // } catch (JsonProcessingException e) {
            // TODO 自動生成された catch ブロック
            // WebApplicationException exception = new
            // WebApplicationException(e);
            // onError(exception);
            // return exception.getResponse();
        }

    }

    protected void onError(WebApplicationException exception) {
        exception.printStackTrace();
    }
}
