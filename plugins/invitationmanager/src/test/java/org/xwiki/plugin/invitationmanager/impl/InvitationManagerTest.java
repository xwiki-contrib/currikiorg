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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.core.Invocation;
import org.jmock.core.stub.CustomStub;
import org.xwiki.plugin.invitationmanager.api.Invitation;
import org.xwiki.plugin.invitationmanager.api.InvitationManager;
import org.xwiki.plugin.invitationmanager.api.JoinRequestStatus;
import org.xwiki.plugin.invitationmanager.api.MembershipRequest;
import org.xwiki.plugin.spacemanager.api.SpaceManager;
import org.xwiki.plugin.spacemanager.api.SpaceManagerException;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiConfig;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.store.XWikiHibernateStore;
import com.xpn.xwiki.store.XWikiStoreInterface;

/**
 * Unit tests for classes implementing {@link InvitationManager} interface
 */
public abstract class InvitationManagerTest extends MockObjectTestCase
{
    protected InvitationManager invitationManager;

    protected SpaceManager spaceManager;

    protected XWikiContext context;

    protected XWiki xwiki;

    protected Mock mockXWikiStore;

    protected Map docs = new HashMap();

    protected static final String SPACE = "MySpace";

    protected static final String ADMIN = "MySpaceAdmin";

    protected String MEMBER = "MySpaceMember";

    protected String DEVELOPER_ROLE = "Developer";

    protected void setUp() throws Exception
    {
        super.setUp();

        context = new XWikiContext();
        xwiki = new XWiki(new XWikiConfig(), context);
        context.setWiki(xwiki);

        mockXWikiStore =
            mock(XWikiHibernateStore.class, new Class[] {XWiki.class, XWikiContext.class},
                new Object[] {xwiki, context});
        mockXWikiStore.stubs().method("loadXWikiDoc").will(
            new CustomStub("Implements XWikiStoreInterface.loadXWikiDoc")
            {
                public Object invoke(Invocation invocation) throws Throwable
                {
                    XWikiDocument shallowDoc = (XWikiDocument) invocation.parameterValues.get(0);

                    if (docs.containsKey(shallowDoc.getFullName())) {
                        return (XWikiDocument) docs.get(shallowDoc.getFullName());
                    } else {
                        return shallowDoc;
                    }
                }
            });
        this.mockXWikiStore.stubs().method("saveXWikiDoc").will(
            new CustomStub("Implements XWikiStoreInterface.saveXWikiDoc")
            {
                public Object invoke(Invocation invocation) throws Throwable
                {
                    XWikiDocument document = (XWikiDocument) invocation.parameterValues.get(0);
                    document.setNew(false);
                    document.setStore((XWikiStoreInterface) mockXWikiStore.proxy());
                    docs.put(document.getFullName(), document);
                    return null;
                }
            });
        this.mockXWikiStore.stubs().method("exists").will(
            new CustomStub("Implements XWikiStoreInterface.exists")
            {
                public Object invoke(Invocation invocation) throws Throwable
                {
                    XWikiDocument document = (XWikiDocument) invocation.parameterValues.get(0);
                    return (docs.get(document.getFullName()) == null) ? Boolean.FALSE
                        : Boolean.TRUE;
                }
            });

        xwiki.setStore((XWikiStoreInterface) mockXWikiStore.proxy());
    }

    public void testAcceptInvitation()
    {
        try {
            String nonMember = "testAcceptInvitation_nonMember";
            context.setUser(ADMIN);
            invitationManager.inviteUser(nonMember, SPACE, false, DEVELOPER_ROLE, context);

            assertFalse(spaceManager.userIsMember(SPACE, nonMember, context));
            assertFalse(spaceManager.getUsersForRole(SPACE, DEVELOPER_ROLE, context).contains(
                nonMember));

            context.setUser(nonMember);
            invitationManager.acceptInvitation(SPACE, context);

            assertTrue(spaceManager.userIsMember(SPACE, nonMember, context));
            assertTrue(spaceManager.getUsersForRole(SPACE, DEVELOPER_ROLE, context).contains(
                nonMember));
        } catch (XWikiException e) {
            assertTrue(false);
        }
    }

    public void testAcceptMembership()
    {
        try {
            String nonMember = "testAcceptMembership_nonMember";
            context.setUser(nonMember);
            invitationManager.requestMembership(SPACE, "I love you space", DEVELOPER_ROLE,
                context);

            assertFalse(spaceManager.userIsMember(SPACE, nonMember, context));
            assertFalse(spaceManager.getUsersForRole(SPACE, DEVELOPER_ROLE, context).contains(
                nonMember));

            context.setUser(ADMIN);
            invitationManager.acceptMembership(SPACE, nonMember, context);

            assertTrue(spaceManager.userIsMember(SPACE, nonMember, context));
            assertTrue(spaceManager.getUsersForRole(SPACE, DEVELOPER_ROLE, context).contains(
                nonMember));
        } catch (XWikiException e) {
            assertTrue(false);
        }
    }

    public void testCancelInvitation()
    {
        try {
            String nonMember = "testCancelInvitation_nonMember";
            context.setUser(ADMIN);
            invitationManager.inviteUser(nonMember, SPACE, false, context);
            invitationManager.cancelInvitation(nonMember, SPACE, context);

            Invitation prototype = createInvitation(nonMember, SPACE);
            prototype.setInviter(ADMIN);
            prototype.setStatus(JoinRequestStatus.SENT);
            List invitations = invitationManager.getInvitations(prototype, context);
            assertEquals(0, invitations.size());

            prototype.setStatus(JoinRequestStatus.CANCELLED);
            invitations = invitationManager.getInvitations(prototype, context);
            assertEquals(1, invitations.size());

            assertFalse(spaceManager.userIsMember(SPACE, nonMember, context));
        } catch (XWikiException e) {
            assertTrue(false);
        }
    }

    public void testCancelMembershipRequest()
    {
        try {
            String nonMember = "testCancelMembershipRequest_nonMember";
            context.setUser(nonMember);
            invitationManager.requestMembership(SPACE, "I love you space", context);
            invitationManager.cancelMembershipRequest(SPACE, context);

            MembershipRequest prototype = createMembershipRequest(nonMember, SPACE);
            prototype.setStatus(JoinRequestStatus.SENT);
            List membershipRequests = invitationManager.getMembershipRequests(prototype, context);
            assertEquals(0, membershipRequests.size());

            prototype.setStatus(JoinRequestStatus.CANCELLED);
            membershipRequests = invitationManager.getMembershipRequests(prototype, context);
            assertEquals(1, membershipRequests.size());

            assertFalse(spaceManager.userIsMember(SPACE, nonMember, context));
        } catch (XWikiException e) {
            assertTrue(false);
        }
    }

    public void testGetInvitations()
    {
        String nonMember = "testGetInvitations_nonMember";
        context.setUser(ADMIN);
        invitationManager.inviteUser(nonMember, SPACE, false, context);

        List invitations =
            invitationManager.getInvitations(SPACE, JoinRequestStatus.SENT, context);
        Invitation invitationSent = null;
        for (int i = 0; i < invitations.size(); i++) {
            Invitation invitation = (Invitation) invitations.get(i);
            if (nonMember.equals(invitation.getInvitee())) {
                invitationSent = invitation;
            }
        }
        assertNotNull(invitationSent);

        context.setUser(nonMember);
        invitations = invitationManager.getInvitations(JoinRequestStatus.SENT, context);
        Invitation invitationReceived = null;
        for (int i = 0; i < invitations.size(); i++) {
            Invitation invitation = (Invitation) invitations.get(i);
            if (SPACE.equals(invitation.getSpace())) {
                invitationReceived = invitation;
            }
        }
        assertNotNull(invitationReceived);
    }

    public void testGetMembershipRequests()
    {
        String nonMember = "testGetMembershipRequests_nonMember";
        context.setUser(nonMember);
        invitationManager.requestMembership(SPACE, "I love you space", context);

        List requests = invitationManager.getMembershipRequests(JoinRequestStatus.SENT, context);
        MembershipRequest requestSent = null;
        for (int i = 0; i < requests.size(); i++) {
            MembershipRequest request = (MembershipRequest) requests.get(i);
            if (SPACE.equals(request.getSpace())) {
                requestSent = request;
            }
        }
        assertNotNull(requestSent);

        context.setUser(ADMIN);
        requests =
            invitationManager.getMembershipRequests(SPACE, JoinRequestStatus.SENT, context);
        MembershipRequest requestReceived = null;
        for (int i = 0; i < requests.size(); i++) {
            MembershipRequest request = (MembershipRequest) requests.get(i);
            if (nonMember.equals(request.getRequester())) {
                requestReceived = request;
            }
        }
        assertNotNull(requestReceived);
    }

    public void testInviteUser()
    {
        try {
            String nonMember = "testInviteUser_nonMember";
            context.setUser(ADMIN);
            invitationManager.inviteUser(nonMember, SPACE, false, context);
            invitationManager.inviteUser(nonMember, SPACE, false, context);

            Invitation prototype = createInvitation(nonMember, SPACE);
            prototype.setInviter(ADMIN);
            prototype.setStatus(JoinRequestStatus.ANY);
            List invitations = invitationManager.getInvitations(prototype, context);
            assertEquals(1, invitations.size());
            assertEquals(JoinRequestStatus.SENT, ((Invitation) invitations.get(0)).getStatus());

            context.setUser(nonMember);
            invitationManager.acceptInvitation(SPACE, context);

            try {
                assertTrue(spaceManager.userIsMember(SPACE, nonMember, context));
            } catch (SpaceManagerException e) {
                assertTrue(false);
            }

            context.setUser(ADMIN);
            invitationManager.inviteUser(nonMember, SPACE, false, context);

            prototype.setStatus(JoinRequestStatus.ANY);
            invitations = invitationManager.getInvitations(prototype, context);
            assertEquals(1, invitations.size());
            assertEquals(JoinRequestStatus.ACCEPTED, ((Invitation) invitations.get(0))
                .getStatus());
        } catch (XWikiException e) {
            assertTrue(false);
        }
    }

    public void testRejectInvitation()
    {
        try {
            String nonMember = "testRejectInvitation_nonMember";
            context.setUser(ADMIN);
            invitationManager.inviteUser(nonMember, SPACE, false, context);

            Invitation prototype = createInvitation(nonMember, SPACE);
            prototype.setInviter(ADMIN);
            prototype.setStatus(JoinRequestStatus.SENT);
            List invitations = invitationManager.getInvitations(prototype, context);
            assertEquals(1, invitations.size());

            context.setUser(nonMember);
            invitationManager.rejectInvitation(SPACE, context);

            invitations = invitationManager.getInvitations(prototype, context);
            assertEquals(0, invitations.size());

            prototype.setStatus(JoinRequestStatus.REFUSED);
            invitations = invitationManager.getInvitations(prototype, context);
            assertEquals(1, invitations.size());

            assertFalse(spaceManager.userIsMember(SPACE, nonMember, context));
        } catch (XWikiException e) {
            assertTrue(false);
        }
    }

    public void testRejectMembership()
    {
        try {
            String nonMember = "testRejectMembership_nonMember";
            context.setUser(nonMember);
            invitationManager.requestMembership(SPACE, "I love you space", context);

            MembershipRequest prototype = createMembershipRequest(nonMember, SPACE);
            prototype.setStatus(JoinRequestStatus.SENT);
            List membershipRequests = invitationManager.getMembershipRequests(prototype, context);
            assertEquals(1, membershipRequests.size());

            context.setUser(ADMIN);
            invitationManager.rejectMembership(SPACE, nonMember, context);

            membershipRequests = invitationManager.getMembershipRequests(prototype, context);
            assertEquals(0, membershipRequests.size());

            prototype.setStatus(JoinRequestStatus.REFUSED);
            membershipRequests = invitationManager.getMembershipRequests(prototype, context);
            assertEquals(1, membershipRequests.size());

            assertFalse(spaceManager.userIsMember(SPACE, nonMember, context));
        } catch (XWikiException e) {
            assertTrue(false);
        }
    }

    public void testRequestMembership()
    {
        try {
            String nonMember = "testRequestMembership_nonMember";
            context.setUser(nonMember);
            invitationManager.requestMembership(SPACE, "I love you space", context);
            invitationManager.requestMembership(SPACE, "I really love you space", context);

            MembershipRequest prototype = createMembershipRequest(nonMember, SPACE);
            prototype.setStatus(JoinRequestStatus.ANY);
            List membershipRequests = invitationManager.getMembershipRequests(prototype, context);
            assertEquals(1, membershipRequests.size());
            assertEquals(JoinRequestStatus.SENT, ((MembershipRequest) membershipRequests.get(0))
                .getStatus());

            context.setUser(ADMIN);
            invitationManager.acceptMembership(SPACE, nonMember, context);

            assertTrue(spaceManager.userIsMember(SPACE, nonMember, context));

            context.setUser(nonMember);
            invitationManager.requestMembership(SPACE, "I really really love you space", context);

            prototype.setStatus(JoinRequestStatus.ANY);
            membershipRequests = invitationManager.getMembershipRequests(prototype, context);
            assertEquals(1, membershipRequests.size());
            assertEquals(JoinRequestStatus.ACCEPTED, ((MembershipRequest) membershipRequests
                .get(0)).getStatus());
        } catch (XWikiException e) {
            assertTrue(false);
        }
    }

    protected abstract Invitation getInvitation(String invitee, String space)
        throws XWikiException;

    protected abstract Invitation createInvitation(String invitee, String space)
        throws XWikiException;

    protected abstract MembershipRequest getMembershipRequest(String requester, String space)
        throws XWikiException;

    protected abstract MembershipRequest createMembershipRequest(String requester, String space)
        throws XWikiException;
}
