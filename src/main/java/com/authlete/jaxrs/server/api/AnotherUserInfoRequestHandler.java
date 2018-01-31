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
//作成日: 2018/02/01

package com.authlete.jaxrs.server.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.authlete.common.api.AuthleteApi;
import com.authlete.jaxrs.AccessTokenInfo;
import com.authlete.jaxrs.AccessTokenValidator;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Masatomi KINO
 * @version $Revision$
 */
@Slf4j
public class AnotherUserInfoRequestHandler {
    private AuthleteApi api;

    public AnotherUserInfoRequestHandler(AuthleteApi api) {
        this.api = api;
    }

    public Response handle(String accessToken) throws WebApplicationException {

        AccessTokenInfo tokenInfo = validateAccessToken(api, accessToken);

        String subject = tokenInfo.getSubject();
        log.debug("Subject:[{}]", subject);

        // Return the requested resource.
        // String json = getResource(code);

        // Create a response of "200 OK".
        String json = String.format("{\"name\":\"%s\"}", subject);
        return Response.ok(json, "application/json;charset=UTF-8").build();

    }

    private AccessTokenInfo validateAccessToken(AuthleteApi api,
            String accessToken) throws WebApplicationException {
        try {
            // Validate the access token and obtain the information about it.
            return new AccessTokenValidator(api).validate(accessToken, null,
                    null);
        } catch (WebApplicationException e) {
            // The access token is invalid. (Or an network error, or some
            // others.)
            onError(e);

            throw e;
        }
    }

    private void onError(WebApplicationException exception) {
        exception.printStackTrace();
    }
}
