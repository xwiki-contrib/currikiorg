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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.xwiki.plugin.invitationmanager.api.MembershipRequest;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

/**
 * The default implementation of {@link MembershipRequest}
 */
public class MembershipRequestImpl extends JoinRequestImpl implements MembershipRequest
{
    public static interface MembershipRequestFields extends JoinRequestFields
    {
        String REQUESTER = "requester";

        String RESPONDER = "responder";
    }

    public MembershipRequestImpl(String requester, String space, boolean create,
        InvitationManagerImpl manager, XWikiContext context) throws XWikiException
    {
        this(requester, space, "", Collections.EMPTY_LIST, Collections.EMPTY_MAP, create,
            manager, context);
    }

    public MembershipRequestImpl(String requester, String space, String text, List roles,
        Map map, boolean create, InvitationManagerImpl manager, XWikiContext context)
        throws XWikiException
    {
        super(manager, context);
        // we initialize the request document
        initRequestDoc(space, requester);
        // if we are asked to create the request
        if (create) {
            // we created it if it does not yet exist, otherwise throw exception
            if (isNew()) {
                createRequestDoc(requester, space, text, roles, map);
            } else {
                throw new XWikiException(-1, -1, "MembershipRequest already exists");
            }
        }
    }

    /**
     * Allows to load an invitation from a wiki document
     * @param fullName
     * @param manager
     * @param context
     * @throws XWikiException
     */
    public MembershipRequestImpl(String fullName, InvitationManagerImpl manager, XWikiContext context)
        throws XWikiException
    {
        super(fullName, manager, context);
    }


    protected String getClassName() {
       return manager.getJoinRequestClassName("Invitation");
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see MembershipRequest#getRequester()
     */
    public String getRequester()
    {
        return doc.getObject(getClassName()).getStringValue(MembershipRequestFields.REQUESTER);
    }

    /**
     * {@inheritDoc}
     * 
     * @see MembershipRequest#getResponder()
     */
    public String getResponder()
    {
        return doc.getObject(getClassName()).getStringValue(MembershipRequestFields.RESPONDER);
    }

    /**
     * {@inheritDoc}
     * 
     * @see MembershipRequest#setRequester(String)
     */
    public void setRequester(String requester)
    {
        getDoc().getObject(getClassName()).setStringValue(MembershipRequestFields.REQUESTER,
            requester);
    }

    /**
     * {@inheritDoc}
     * 
     * @see MembershipRequest#setResponder(String)
     */
    public void setResponder(String responder)
    {
        getDoc().getObject(getClassName()).setStringValue(MembershipRequestFields.RESPONDER,
            responder);
    }

    protected void initRequestDoc(String space, String requester) throws XWikiException
    {
        String docName =
            manager.getJoinRequestDocumentName("MembershipRequest", space, requester, context);
        doc = context.getWiki().getDocument(docName, context);
    }

    protected void createRequestDoc(String requester, String space, String text, List roles,
        Map map)
    {
        XWikiDocument requestDoc = getDoc();
        String className = getClassName();
        BaseObject requestObj = new BaseObject();
        requestObj.setName(doc.getFullName());
        requestObj.setClassName(className);
        requestDoc.addObject(className, requestObj);

        setRequester(requester);
        setSpace(space);
        setText(text);
        setRoles(roles);
        setMap(map);
    }
}
