/*
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License").  You may not use this file
 * except in compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://IdentityConnectors.dev.java.net/legal/license.txt
 * See the License for the specific language governing permissions and limitations
 * under the License.
 *
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at identityconnectors/legal/license.txt.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package org.identityconnectors.ldap;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

public class LdapEntryTests {

    @Test
    public void testEntryDNAttribute() throws Exception {
        final String NAME = "uid=admin";
        final String BASE = "dc=example,dc=com";
        final String ENTRY_DN = NAME + "," + BASE;
        final String OTHER_ENTRY_DN = "uid=nothing,dc=example,dc=com";

        BasicAttributes attrs = new BasicAttributes(false);
        attrs.put(new BasicAttribute("entryDN", OTHER_ENTRY_DN));
        attrs.put(new BasicAttribute("cn", "Common Name"));
        LdapEntry entry = LdapEntry.create(NAME + "," + BASE, attrs);

        assertEquals("Common Name", entry.getAttributes().get("cn").get());
        assertEquals(ENTRY_DN, entry.getAttributes().get("dn").get());
        assertEquals(ENTRY_DN, entry.getAttributes().get("entryDN").get());
    }

    @Test
    public void testSearchResultDn() throws Exception {
        // Test encoded LDAP URL
        SearchResult result = new SearchResult("ldap://example.com:389/cn=foo%5c,%20bar,ou=wren%20security,ou=org", null, null);
        result.setRelative(false);
        LdapEntry entry = LdapEntry.create(null, result);
        assertEquals(new LdapName("cn=foo\\, bar,ou=wren security,ou=org"), entry.getDN());

        // Test plain LDAP URL
        result = new SearchResult("ldap://example.com:389/cn=foobar,ou=wrensecurity,ou=org", null, null);
        result.setRelative(false);
        entry = LdapEntry.create(null, result);
        assertEquals(new LdapName("cn=foobar,ou=wrensecurity,ou=org"), entry.getDN());

        // Test relative DN
        result = new SearchResult("cn=foobar", null, null);
        result.setNameInNamespace("cn=foobar,ou=wrensecurity,ou=org");
        entry = LdapEntry.create(null, result);
        assertEquals(new LdapName("cn=foobar,ou=wrensecurity,ou=org"), entry.getDN());
    }

}
