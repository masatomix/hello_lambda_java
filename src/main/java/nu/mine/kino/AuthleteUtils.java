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
//作成日: 2018/01/17

package nu.mine.kino;

import java.util.Enumeration;
import java.util.ResourceBundle;

import com.authlete.common.conf.AuthleteConfiguration;
import com.authlete.common.conf.AuthleteSimpleConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Masatomi KINO
 * @version $Revision$
 */
@Slf4j
public class AuthleteUtils {
    public static AuthleteConfiguration createConfig() {

        AuthleteSimpleConfiguration config = null;
        String propertyFile = "authlete";
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(propertyFile);
            Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                log.debug("key[{}]:{}", key, bundle.getString(key));
            }

            config = new AuthleteSimpleConfiguration();
            String base_url = bundle.getString("base_url");
            String api_key = bundle.getString("service.api_key");
            String api_secret = bundle.getString("service.api_secret");

            config.setBaseUrl(base_url);
            config.setServiceApiKey(api_key);
            config.setServiceApiSecret(api_secret);

        } catch (java.util.MissingResourceException e) {
            String message = "設定ファイルが存在しません。必要ならクラスパス上に {}.propertiesを配置してください。({})";
            log.warn(message, propertyFile, e.getMessage());
        }
        return config;

    }

}
