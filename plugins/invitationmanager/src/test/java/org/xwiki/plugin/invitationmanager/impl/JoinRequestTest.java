/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
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
 */
package org.xwiki.plugin.invitationmanager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xwiki.plugin.invitationmanager.api.JoinRequest;
import org.xwiki.plugin.invitationmanager.api.JoinRequestStatus;

/**
 * Unit tests for classes implementing {@link JoinRequest} interface
 */
public abstract class JoinRequestTest extends org.jmock.cglib.MockObjectTestCase
{
    protected JoinRequest joinRequest;

    /**
     * test for {@link JoinRequest#getMap()}
     */
    public void testMap()
    {
        Map map = new HashMap();
        map.put("allowMailNotifications", Boolean.TRUE);
        map.put("notifyChanges", Boolean.FALSE);
        joinRequest.setMap(map);
        assertEquals(map, joinRequest.getMap());
    }

    /**
     * test for {@link JoinRequest#getRequestDate()}
     */
    public void testRequestDate()
    {
        Date requestDate = new Date();
        joinRequest.setRequestDate(requestDate);
        assertEquals(requestDate, joinRequest.getRequestDate());
    }

    /**
     * test for {@link JoinRequest#getResponseDate()}
     */
    public void testResponseDate()
    {
        Date responseDate = new Date();
        joinRequest.setResponseDate(responseDate);
        assertEquals(responseDate, joinRequest.getResponseDate());
    }

    /**
     * test for {@link JoinRequest#getRoles()}
     */
    public void testRoles()
    {
        List roles = new ArrayList();
        roles.add("developer");
        roles.add("admin");
        joinRequest.setRoles(roles);
        assertEquals(roles, joinRequest.getRoles());
    }

    /**
     * test for {@link JoinRequest#getSpace()}
     */
    public void testSpace()
    {
        String space = "Blog";
        joinRequest.setSpace(space);
        assertEquals(space, joinRequest.getSpace());
    }

    /**
     * test for {@link JoinRequest#getStatus()}
     */
    public void testStatus()
    {
        int status = JoinRequestStatus.SENT;
        joinRequest.setStatus(status);
        assertEquals(status, joinRequest.getStatus());
    }

    /**
     * test for {@link JoinRequest#getText()}
     */
    public void testText()
    {
        String text = "I love your space!";
        joinRequest.setText(text);
        assertEquals(text, joinRequest.getText());
    }
}
