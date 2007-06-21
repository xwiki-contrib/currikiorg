package org.curriki.gwt;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * Copyright 2006,XpertNet SARL,and individual contributors as indicated
 * by the contributors.txt.
 *
 * This is free software;you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation;either version2.1of
 * the License,or(at your option)any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software;if not,write to the Free
 * Software Foundation,Inc.,51 Franklin St,Fifth Floor,Boston,MA
 * 02110-1301 USA,or see the FSF site:http://www.fsf.org.
 *
 * @author ldubost
 */

public class ChooseTemplateDialogTest extends GWTTestCase {

    /*
    * Specifies a module to use when running this test case. The returned
    * module must cause the source for this class to be included.
    *
    * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
    */
    public String getModuleName() {
        return "org.curriki.gwt.Main";
    }

    public void setUp() throws Exception {
       super.setUp();
    }

    public void testStuff() {
        assertTrue(2 + 2 == 4);
    }
}

