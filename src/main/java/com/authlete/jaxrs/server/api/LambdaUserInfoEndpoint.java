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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.amazonaws.services.lambda.runtime.Context;
import com.authlete.common.api.AuthleteApi;
import com.authlete.common.api.AuthleteApiFactory;
import com.authlete.common.conf.AuthleteConfiguration;

import nu.mine.kino.AuthleteUtils;

public class LambdaUserInfoEndpoint {

    public Response get(String accessToken, Context context) {

        try {
            AuthleteConfiguration configuration = AuthleteUtils.createConfig();
            AuthleteApi api = AuthleteApiFactory.create(configuration);
            // Create a handler.
            AnotherUserInfoRequestHandler handler = new AnotherUserInfoRequestHandler(
                    api);
            // Delegate the task to the handler.
            return handler.handle(accessToken);
        } catch (WebApplicationException e) {
            // An error occurred in the handler.
            onError(e);

            // Convert the error to a Response.
            return e.getResponse();
        }
    }

    private void onError(WebApplicationException exception) {
        exception.printStackTrace();
    }

}
