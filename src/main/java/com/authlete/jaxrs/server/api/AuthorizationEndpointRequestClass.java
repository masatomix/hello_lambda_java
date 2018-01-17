/******************************************************************************
 * Copyright (c) 2014 Masatomi KINO and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *      Masatomi KINO - initial API and implementation
 * $Id$
 ******************************************************************************/
//作成日: 2017/11/21

package com.authlete.jaxrs.server.api;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Masatomi KINO
 * @version $Revision$
 */
@Getter
@Setter
@ToString
public class AuthorizationEndpointRequestClass implements IRequest {

    private String client_id = null;

    private String redirect_uri = null;

    private String state = null;

    private String nonce = null;

    private String response_type = null;

    private String scope = null;

}
