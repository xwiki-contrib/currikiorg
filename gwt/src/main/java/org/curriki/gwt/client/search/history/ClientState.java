/*
 * Copyright 2007, The Global Education and Learning Community,
 * and individual contributors as indicated by the contributors.txt.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * @author dward
 *
 */
package org.curriki.gwt.client.search.history;

import java.util.HashMap;

/***
 * Class, that transform currect history token into state (hashmap)
 */
public class ClientState
{
    private HashMap paramsMap = new HashMap();

    /**
     * Constucts new state from specified token
     */
    public ClientState(String historyToken)
    {
        InitFromToken(historyToken);
    }

    /**
     * Constructs new empty state
     */
    public ClientState()
    {
    }

    /**
     * Initialize state from token
     */
    public void InitFromToken(String historyToken)
    {
        paramsMap.clear();
        AddFromToken(historyToken);
    }

    /**
     * Add state from token
     */
    public void AddFromToken(String historyToken)
    {
        if (historyToken != null && historyToken.length() > 1) {
            String[] kvPairs = historyToken.split("&");
            for (int i = 0; i < kvPairs.length; i++) {
                String[] kv = kvPairs[i].split("=");
                if (kv.length > 1) {
                    paramsMap.put(kv[0], kv[1]);
                } else {
                    paramsMap.put(kv[0], "");
                }
            }
        }
    }

    /**
     * Returns state's value with specified key
     *
     * @return state's value
     */
    public String getValue(String key)
    {
        if (paramsMap.containsKey(key))
            return (String) paramsMap.get(key);
        else return "";
    }

    /**
     * Set state's value with specified key
     */
    public void setValue(String key, String value)
    {
        paramsMap.put(key, value);
    }

    /**
     * Constructs token using current state
     *
     * @return History Token
     */
    public String getHistoryToken()
    {
        String result = "";
        Object[] keys = paramsMap.keySet().toArray();
        for (int c = 0; c < keys.length; c++) {
            String key = (String) keys[c];
            if (result != "") result += "&";
            result += key + "=" + (String) paramsMap.get(key);
        }
        return result;
    }
}