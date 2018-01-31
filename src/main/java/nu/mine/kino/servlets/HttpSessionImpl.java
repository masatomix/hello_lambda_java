package nu.mine.kino.servlets;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
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
    private String id;

    /**
     * 引数のidをsessionIdとする
     * 
     * sessionMapはnullでなかったら設定するが、nullの場合は空のMap
     * 
     * @param id
     * @param sessionMap
     */
    public HttpSessionImpl(@NonNull String id, Map<String, Object> sessionMap) {
        this.id = id;

        if (sessionMap != null) {
            this.sessionMap = sessionMap;
        } else {
            this.sessionMap = new HashMap<String, Object>();
        }

    }

    @Override
    public Object getAttribute(String name) {
        // log.debug("getAttribute start.");
        // log.debug("ID:{},key:{}", id, name);
        sessionMap = SessionStoreUtils.INSTANCE.searchOrCreate(this.id);
        Object result = sessionMap.get(name);
        // String result = (String) sessionMap.get(name);

        // Dateが格納できないっぽく、JSONにしたときにLongにしているので、
        // 逆に"authTime"だけはLongからDateに戻してあげる。
        if (name.equals("authTime") && result != null) {
            return new Date((Long) result); // Longで帰ってきてしまう
        }

        if (result != null) {
            log.debug("get key[{}]: {}", name, result.toString());
        } else {
            log.debug("get key[{}]: {}", name, null);
        }
        return result;
    }

    @Override
    public void setAttribute(String name, Object value) {
        // log.debug("setAttribute start.");
        // log.debug("ID:{},key[{}]:{}", id, name, value);

        sessionMap.put(name, value);
        SessionStoreUtils.INSTANCE
                .searchAndUpdate(new HttpSessionModel(this.id, sessionMap));

    }

    @Override
    public void removeAttribute(String name) {
        // log.debug("removeAttribute start.");
        sessionMap.remove(name);
        SessionStoreUtils.INSTANCE
                .searchAndUpdate(new HttpSessionModel(this.id, sessionMap));
    }

}
