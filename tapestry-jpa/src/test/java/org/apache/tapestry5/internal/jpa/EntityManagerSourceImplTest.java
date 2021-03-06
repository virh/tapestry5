// Copyright 2011 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.internal.jpa;

import org.apache.tapestry5.ioc.internal.util.ClasspathResource;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.jpa.PersistenceUnitConfigurer;
import org.apache.tapestry5.test.TapestryTestCase;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

public class EntityManagerSourceImplTest extends TapestryTestCase
{
    @Test
    public void multiple_persistence_units_include_unlisted_classes()
    {
        Exception exception = null;

        try
        {
            new EntityManagerSourceImpl(
                    LoggerFactory.getLogger(EntityManagerSourceImplTest.class),
                    new ClasspathResource("multiple-persistence-units-include-unlisted-classes.xml"),
                    null,
                    CollectionFactory.<String, PersistenceUnitConfigurer>newMap());

            fail("Exception expected");

        } catch (Exception e)
        {
            exception = e;
        }

        assertNotNull(exception);

        assertEquals(exception.getMessage(), "Persistence units 'TestUnit, TestUnit2' are configured to include managed classes that have not been explicitly listed. This is forbidden when multiple persistence units are used in the same application. Please configure persistence units to exclude unlisted managed classes (e.g. by removing <exclude-unlisted-classes> element) and include them explicitly.");

    }
}
