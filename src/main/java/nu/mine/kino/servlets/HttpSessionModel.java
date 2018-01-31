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
//作成日: 2018/01/30

package nu.mine.kino.servlets;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Masatomi KINO
 * @version $Revision$
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HttpSessionModel {
    private String sessionId;

    private Map<String, Object> sessionMap;
}
