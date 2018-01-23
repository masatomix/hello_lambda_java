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
//作成日: 2018/01/22

package nu.mine.kino.servlets;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;

import lombok.extern.slf4j.Slf4j;

/**
 * Session管理のためのUtility。とりあえず実装はDynamoDBを使ってみたが、別の何かでもいい。
 * 
 * @author Masatomi KINO
 * @version $Revision$
 */
@Slf4j
public class SessionStoreUtils {

    private static final Regions REGION = Regions.AP_NORTHEAST_1;

    private static final String tableName = "HttpSession";

    private static final String SESSION_ID_NAME = "sessionId";

    private static final String SESSION_MAP_NAME = "sessionMap";

    private static DynamoDB dynamoDb;

    static {
        AmazonDynamoDB client = new AmazonDynamoDBClient();
        client.setRegion(Region.getRegion(REGION));
        dynamoDb = new DynamoDB(client);
    }

    /**
     * 指定したsessionId のSession情報(Sessionに格納したインスタンスを保持するオブジェクト)をストア先から探して返す。
     * 存在しない場合、該当sessionIdのSession情報を作成して、ストア先に保存する
     * 
     * @param sessionId
     * @return Sessionに格納したインスタンスを保持するオブジェクト
     */
    public static Map<String, Object> searchOrCreate(String sessionId) {
        Item item = searchItem(sessionId);

        if (item != null) {
            log.debug("該当するsessionIdが見つかりました。ID:{}", sessionId);
            Map<String, Object> sessionMap = item.getMap(SESSION_MAP_NAME);
            return sessionMap;
        }

        log.debug("該当するsessionIdが見つかりませんでした。ID:{}", sessionId);
        putItem(new Item().withPrimaryKey(SESSION_ID_NAME, sessionId)
                .withMap(SESSION_MAP_NAME, new HashMap<String, Object>()));
        return new HashMap<String, Object>();
    }

    /**
     * 指定したsessionId
     * のSession情報(Sessionに格納したインスタンスを保持するオブジェクト)をストア先から探して、指定したMapで更新する。
     * 指定したsessionIdが存在しない場合、該当sessionIdのSession情報を作成して、ストア先に保存する。その際引数のMapはムシする(Session情報だけ作成)
     * 
     * @param sessionId
     * @param sessionMap
     *            Sessionに格納されたインスタンスを更新するための情報
     */
    public static void searchAndUpdate(String sessionId,
            Map<String, Object> sessionMap) {
        Item item = searchItem(sessionId);

        if (item != null) {
            log.debug("sessionが見つかりました。ID:{}", sessionId);
            putItem(session2Item(sessionId, sessionMap));
        } else {
            log.debug("sessionが見つかりませんでした。ID:{}", sessionId);
            putItem(new Item().withPrimaryKey(SESSION_ID_NAME, sessionId));
        }
    }

    private static Item searchItem(String sessionId) {
        Table table = dynamoDb.getTable(tableName);
        return table.getItem(SESSION_ID_NAME, sessionId);
    }

    private static PutItemOutcome putItem(Item item) {
        Table table = dynamoDb.getTable(tableName);
        return table.putItem(new PutItemSpec().withItem(item));
    }

    private static Item session2Item(String sessionId,
            Map<String, Object> sessionMap) {
        return new Item().withPrimaryKey(SESSION_ID_NAME, sessionId)
                .withMap(SESSION_MAP_NAME, sessionMap);
    }

}
