package nu.mine.kino.servlets;

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

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;

import com.authlete.jaxrs.server.api.IRequest;

/**
 * @author Masatomi KINO
 * @version $Revision$
 */
public class HttpServletRequestImpl extends HttpServletRequestAdaptor {

    private final IRequest request;

    private String sessionId;

    public HttpServletRequestImpl(IRequest request, String sessionId) {
        super();
        this.request = request;
        this.sessionId = sessionId;
    }

    public HttpServletRequestImpl(IRequest request) {
        this(request, null);
        this.sessionId = RandomStringUtils.randomAlphanumeric(40);
    }

    @Override
    public HttpSession getSession(boolean create) {
        HttpSessionImpl httpSessionImpl = new HttpSessionImpl(sessionId,
                SessionStoreUtils.searchOrCreate(sessionId));
        return httpSessionImpl;
    }

    @Override
    public HttpSession getSession() {
        return this.getSession(true);
    }

}
