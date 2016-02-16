/**
 * $Revision$
 * $Date$
 *
 * Copyright (C) 2005-2008 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vidmt.of.plugin.sub.extdb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.admin.AdminManager;
import org.jivesoftware.openfire.admin.AdminProvider;
import org.jivesoftware.util.JiveGlobals;
import org.jivesoftware.util.PropertyEventDispatcher;
import org.jivesoftware.util.PropertyEventListener;
import org.jivesoftware.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

/**
 * Handles default management of admin users, which stores the list if accounts as a system property.
 *
 * @author Daniel Henninger
 */
public class VAdminProvider implements AdminProvider {

	private static final Logger Log = LoggerFactory.getLogger(VAdminProvider.class);

    /**
     * Constructs a new DefaultAdminProvider
     */
    public VAdminProvider() {

        // Convert old openfire.xml style to new provider style, if necessary.
        Log.debug("DefaultAdminProvider: Convert XML to provider.");
        convertXMLToProvider();

        // Detect when the list of admin users is changed.
        PropertyEventListener propListener = new PropertyEventListener() {
            public void propertySet(String property, Map params) {
                Log.debug("DefaultAdminProvider: Property was set: "+property);
                if ("admin.authorizedJIDs".equals(property)) {
                    AdminManager.getInstance().refreshAdminAccounts();
                }
            }

            public void propertyDeleted(String property, Map params) {
                Log.debug("DefaultAdminProvider: Property was deleted: "+property);
                if ("admin.authorizedJIDs".equals(property)) {
                    AdminManager.getInstance().refreshAdminAccounts();
                }
            }

            public void xmlPropertySet(String property, Map params) {
                Log.debug("DefaultAdminProvider: XML Property was set: "+property);
                //Ignore
            }

            public void xmlPropertyDeleted(String property, Map params) {
                Log.debug("DefaultAdminProvider: XML Property was deleted: "+property);
                //Ignore
            }
        };
        PropertyEventDispatcher.addListener(propListener);

    }

    /**
     * The default provider retrieves the comma separated list from the system property
     * <tt>admin.authorizedJIDs</tt>
     * @see org.jivesoftware.openfire.admin.AdminProvider#getAdmins()
     */
    public List<JID> getAdmins() {
        List<JID> adminList = new ArrayList<JID>();

        // Add bare JIDs of users that are admins (may include remote users), primarily used to override/add to list of admin users
        String jids = JiveGlobals.getProperty("admin.authorizedJIDs");
        jids = (jids == null || jids.trim().length() == 0) ? "" : jids;
        StringTokenizer tokenizer = new StringTokenizer(jids, ",");
        while (tokenizer.hasMoreTokens()) {
            String jid = tokenizer.nextToken().toLowerCase().trim();
            try {
                adminList.add(new JID(jid));
            }
            catch (IllegalArgumentException e) {
                Log.warn("Invalid JID found in admin.authorizedJIDs system property: " + jid, e);
            }
        }

        if (adminList.isEmpty()) {
            // Add default admin account when none was specified
            adminList.add(new JID("admin", XMPPServer.getInstance().getServerInfo().getXMPPDomain(), null, true));
            adminList.add(new JID("0", XMPPServer.getInstance().getServerInfo().getXMPPDomain(), null, true));
            adminList.add(new JID("uid\\3a0", XMPPServer.getInstance().getServerInfo().getXMPPDomain(), null, true));
            adminList.add(new JID("name\\3aadmin", XMPPServer.getInstance().getServerInfo().getXMPPDomain(), null, true));

        }

        return adminList;
    }

    /**
     * The default provider sets a comma separated list as the system property
     * <tt>admin.authorizedJIDs</tt>
     * @see org.jivesoftware.openfire.admin.AdminProvider#setAdmins(java.util.List)
     */
    public void setAdmins(List<JID> admins) {
        Collection<String> adminList = new ArrayList<String>();
        for (JID admin : admins) {
            adminList.add(admin.toBareJID());
        }
        JiveGlobals.setProperty("admin.authorizedJIDs", StringUtils.collectionToString(adminList));
    }

    /**
     * The default provider is not read only
     * @see org.jivesoftware.openfire.admin.AdminProvider#isReadOnly()
     */
    public boolean isReadOnly() {
        return false;
    }

    /**
     * Converts the old openfire.xml style admin list to use the new provider mechanism.
     */
    private void convertXMLToProvider() {
        if (JiveGlobals.getXMLProperty("admin.authorizedJIDs") == null &&
                JiveGlobals.getXMLProperty("admin.authorizedUsernames") == null &&
                JiveGlobals.getXMLProperty("adminConsole.authorizedUsernames") == null) {
            // No settings in openfire.xml.
            return;
        }

        List<JID> adminList = new ArrayList<JID>();

        // Add bare JIDs of users that are admins (may include remote users), primarily used to override/add to list of admin users
        String jids = JiveGlobals.getXMLProperty("admin.authorizedJIDs");
        jids = (jids == null || jids.trim().length() == 0) ? "" : jids;
        StringTokenizer tokenizer = new StringTokenizer(jids, ",");
        while (tokenizer.hasMoreTokens()) {
            String jid = tokenizer.nextToken().toLowerCase().trim();
            try {
                adminList.add(new JID(jid));
            }
            catch (IllegalArgumentException e) {
                Log.warn("Invalid JID found in authorizedJIDs at openfire.xml: " + jid, e);
            }
        }

        // Add the JIDs of the local users that are admins, primarily used to override/add to list of admin users
        String usernames = JiveGlobals.getXMLProperty("admin.authorizedUsernames");
        if (usernames == null) {
            // Fall back to old method for defining admins (i.e. using adminConsole prefix
            usernames = JiveGlobals.getXMLProperty("adminConsole.authorizedUsernames");
        }
        // Add default of admin user if no other users were listed as admins.
        usernames = (usernames == null || usernames.trim().length() == 0) ? (adminList.size() == 0 ? "admin" : "") : usernames;
        tokenizer = new StringTokenizer(usernames, ",");
        while (tokenizer.hasMoreTokens()) {
            String username = tokenizer.nextToken();
            try {
                adminList.add(XMPPServer.getInstance().createJID(username.toLowerCase().trim(), null));
            }
            catch (IllegalArgumentException e) {
                // Ignore usernames that when appended @server.com result in an invalid JID
                Log.warn("Invalid username found in authorizedUsernames at openfire.xml: " +
                        username, e);
            }
        }
        setAdmins(adminList);

        // Clear out old XML property settings
        JiveGlobals.deleteXMLProperty("admin.authorizedJIDs");
        JiveGlobals.deleteXMLProperty("admin.authorizedUsernames");
        JiveGlobals.deleteXMLProperty("adminConsole.authorizedUsernames");
    }

}