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

import org.apache.commons.lang3.StringUtils;

import com.authlete.jaxrs.server.api.IRequest;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Masatomi KINO
 * @version $Revision$
 */
public class HttpServletRequestImpl extends HttpServletRequestAdaptor {

    private final IRequest request;

    private String sessionId;

    private HttpSession session = null;

    public HttpServletRequestImpl(IRequest request, String sessionId) {
        super();
        this.request = request;
        this.sessionId = sessionId;
    }

    public HttpServletRequestImpl(IRequest request) {
        super();
        this.request = request;
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (session == null) {
            session = internalGetSession(sessionId);
        }
        return session;
    }

    @Override
    public HttpSession getSession() {
        return this.getSession(true);
    }

    private HttpSessionImpl internalGetSession(String sessionId) {
        // 引数のsessionIdでセッションを作成する。
        // ホントはsessionIdの値でどこかストア先を検索するべきだが、とりあえず今のところ毎回作ってしまう
        return new HttpSessionImpl(sessionId);
    }

}
