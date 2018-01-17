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

import com.authlete.jaxrs.server.api.IRequest;

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
public class HttpServletRequestImpl extends HttpServletRequestAdaptor {

    private IRequest request = null;

    public HttpServletRequestImpl(IRequest request) {
        super();
        this.request = request;
    }

    private static HttpSession session = null;

    @Override
    public HttpSession getSession(boolean create) {
        if (session == null) {
            session = new HttpSessionImpl();
        }
        return session;
    }

    @Override
    public HttpSession getSession() {
        return this.getSession(true);
    }

}
