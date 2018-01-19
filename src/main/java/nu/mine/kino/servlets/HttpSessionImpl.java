package nu.mine.kino.servlets;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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

/**
 * @author Masatomi KINO
 * @version $Revision$
 */
@Slf4j
public class HttpSessionImpl extends HttpSessionAdaptor {
    private Map<String, Object> sessionMap = null;

    @Getter
    @Setter
    private String id;

    /**
     * 引数のidがnullとか""なら、ランダム値を設定する。そうでなければ引数のidをsessionIdとする
     * 
     * @param id
     */
    public HttpSessionImpl(String id) {
        this.id = id;
        if (StringUtils.isEmpty(id)) {
            this.id = RandomStringUtils.randomAlphanumeric(40);
        }
        sessionMap = new HashMap<String, Object>();
    }

    public HttpSessionImpl() {
        this(null);
    }

    @Override
    public Object getAttribute(String name) {
        // log.debug("getAttribute start.");
        // log.debug("ID:{},key:{}", id, name);
        return sessionMap.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        // log.debug("setAttribute start.");
        // log.debug("ID:{},key[{}]:{}", id, name, value);
        sessionMap.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        // log.debug("removeAttribute start.");
        sessionMap.remove(name);
    }

}
