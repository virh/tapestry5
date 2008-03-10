// Copyright 2006, 2007, 2008 The Apache Software Foundation
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

package org.apache.tapestry.internal.services;

import org.apache.tapestry.ComponentEventCallback;
import org.apache.tapestry.EventContext;
import org.apache.tapestry.internal.structure.PageResources;
import org.apache.tapestry.internal.test.InternalBaseTestCase;
import org.apache.tapestry.ioc.services.TypeCoercer;
import org.apache.tapestry.runtime.ComponentEvent;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ComponentEventImplTest extends InternalBaseTestCase
{
    private TypeCoercer _coercer;

    @BeforeClass
    public void setup_coercer()
    {
        _coercer = getObject(TypeCoercer.class, null);
    }

    @AfterClass
    public void cleanup_coercer()
    {
        _coercer = null;
    }

    @Test
    public void matches_on_event_type()
    {
        ComponentEventCallback handler = mockComponentEventHandler();
        EventContext context = mockEventContext();

        train_getCount(context, 0);

        replay();

        ComponentEvent event = new ComponentEventImpl("eventType", "someId", context, handler, null);

        assertTrue(event.matches("eventType", "someId", 0));
        assertFalse(event.matches("foo", "someId", 0));

        verify();
    }

    @Test
    public void event_type_match_is_case_insensitive()
    {
        ComponentEventCallback handler = mockComponentEventHandler();
        EventContext context = mockEventContext();

        train_getCount(context, 0);

        replay();

        ComponentEvent event = new ComponentEventImpl("eventType", "someId", context, handler, null);

        assertTrue(event.matches("EVENTTYPE", "someid", 0));

        verify();
    }

    @Test
    public void matches_on_component_id()
    {
        ComponentEventCallback handler = mockComponentEventHandler();
        EventContext context = mockEventContext();

        train_getCount(context, 0);

        replay();

        ComponentEvent event = new ComponentEventImpl("eventType", "someId", context, handler, null);

        assertTrue(event.matches("eventType", "someId", 0));

        assertFalse(event.matches("eventtype", "bar", 0));

        verify();
    }

    @Test
    public void component_id_matches_are_case_insensitive()
    {
        ComponentEventCallback handler = mockComponentEventHandler();
        EventContext context = mockEventContext();

        train_getCount(context, 0);

        replay();

        ComponentEvent event = new ComponentEventImpl("eventType", "someId", context, handler, null);

        assertTrue(event.matches("eventtype", "SOMEID", 0));

        verify();
    }

    @Test
    public void coerce_context()
    {
        ComponentEventCallback handler = mockComponentEventHandler();
        PageResources resources = mockPageResources();
        EventContext context = mockEventContext();
        Integer value = new Integer(27);

        train_toClass(resources, "java.lang.Integer", Integer.class);

        train_getCount(context, 2);
        train_get(context, Integer.class, 0, value);

        replay();

        ComponentEvent event = new ComponentEventImpl("eventType", "someId", context, handler, resources);

        assertSame(event.coerceContext(0, "java.lang.Integer"), value);

        verify();
    }

    @Test
    public void coerce_when_not_enough_context()
    {
        ComponentEventCallback handler = mockComponentEventHandler();
        EventContext context = mockEventContext();

        train_getCount(context, 0);

        replay();

        ComponentEvent event = new ComponentEventImpl("eventType", "someId", context, handler, null);

        event.setMethodDescription("foo.Bar.baz()");

        try
        {
            event.coerceContext(1, "java.lang.Integer");
        }
        catch (IllegalArgumentException ex)
        {
            assertEquals(ex.getMessage(),
                         "Method foo.Bar.baz() has more parameters than there are context values for this component event.");
        }

        verify();
    }

    @Test
    public void unable_to_coerce()
    {
        ComponentEventCallback handler = mockComponentEventHandler();
        EventContext context = mockEventContext();
        PageResources resources = mockPageResources();

        train_toClass(resources, Integer.class.getName(), Integer.class);

        train_getCount(context, 1);

        expect(context.get(Integer.class, 0)).andThrow(new NumberFormatException("Not so easy, is it?"));

        replay();

        ComponentEvent event = new ComponentEventImpl("eventType", "someId", context, handler, resources);

        event.setMethodDescription("foo.Bar.baz()");

        try
        {
            event.coerceContext(0, "java.lang.Integer");
            unreachable();
        }
        catch (IllegalArgumentException ex)
        {
            // Different JVMs will report the conversion error slightly differently,
            // so we don't try to check that part of the error message.

            assertTrue(ex.getMessage().startsWith("Exception in method foo.Bar.baz(), parameter #1:"));
        }

        verify();
    }

    @Test
    public void store_result_and_abort()
    {
        Object result = new Object();
        String methodDescription = "foo.Bar.baz()";

        ComponentEventCallback handler = mockComponentEventHandler();

        train_handleResult(handler, result, true);

        replay();

        ComponentEvent event = new ComponentEventImpl("eventType", "someId", null, handler, null);

        event.setMethodDescription(methodDescription);

        assertFalse(event.isAborted());

        assertTrue(event.storeResult(result));

        assertTrue(event.isAborted());

        verify();
    }

    @Test
    public void store_result_and_continue()
    {
        Object result = new Object();
        String methodDescription = "foo.Bar.baz()";
        ComponentEventCallback handler = mockComponentEventHandler();

        train_handleResult(handler, result, false);

        replay();

        ComponentEvent event = new ComponentEventImpl("eventType", "someId", null, handler, null);

        event.setMethodDescription(methodDescription);

        assertFalse(event.storeResult(result));

        assertFalse(event.isAborted());

        verify();
    }

    @Test
    public void store_null_result_does_not_abort_or_invoke_handler()
    {
        ComponentEventCallback handler = mockComponentEventHandler();

        replay();

        ComponentEvent event = new ComponentEventImpl("eventType", "someId", null, handler, null);

        event.setMethodDescription("foo.Bar.baz()");

        assertFalse(event.storeResult(null));

        assertFalse(event.isAborted());

        verify();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void store_result_when_aborted_is_failure()
    {
        Object result = new Object();
        ComponentEventCallback handler = mockComponentEventHandler();

        expect(handler.handleResult(result)).andReturn(true);

        replay();

        ComponentEvent event = new ComponentEventImpl("eventType", "someId", null, handler, null);

        event.setMethodDescription("foo.Bar.baz()");
        event.storeResult(result);

        try
        {
            event.setMethodDescription("foo.Bar.biff()");
            event.storeResult(null);
            unreachable();
        }
        catch (IllegalStateException ex)
        {
            assertEquals(ex.getMessage(), ServicesMessages
                    .componentEventIsAborted("foo.Bar.biff()"));
        }

        verify();
    }
}
