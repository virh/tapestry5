// Copyright 2010 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.tapestry5.services.javascript;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.EnvironmentalShadowBuilder;

/**
 * An environmental that acts as a replacement for the {@link RenderSupport} environmental, renaming and streamlining
 * the the key methods. JavascriptSupport is very stateful, accumulating JavaScript stacks, libraries and initialization
 * code until the end of the main page render; it then updates the rendered DOM (adding &lt;script&gt; tags to the
 * &lt;head&gt; and &lt;body&gt;) before the document is streamed to the client.
 * <p>
 * JavascriptSupport is normally accessed within a component by using the {@link Environmental} annotation on a
 * component field. In addition, JavascriptSupport may also be accessed as a service (the service
 * {@linkplain EnvironmentalShadowBuilder internally delegates to the current environmental instance}), which is useful
 * for service-layer objects.
 * <p>
 * The term "import" is used on many methods to indicate that the indicated resource (stack, library or stylesheet) will
 * only be added to the final Document once.
 * <p>
 * The name is slightly a misnomer, since there's a side-line of
 * {@linkplain #importStylesheet(Asset, StylesheetOptions)
 * importing stylesheets} as well.
 * 
 * @since 5.2.0
 */
public interface JavascriptSupport
{
    /**
     * Allocates a unique id based on the component's id. In some cases, the return value will not precisely match the
     * input value (an underscore and a unique index value may be appended).
     * 
     * @param id
     *            the component id from which a unique id will be generated
     * @return a unique id for this rendering of the page
     * @see org.apache.tapestry5.ioc.internal.util.IdAllocator
     */
    String allocateClientId(String id);

    /**
     * As with {@link #allocateClientId(String)} but uses the id of the component extracted from the resources.
     * 
     * @param resources
     *            of the component which requires an id
     * @return a unique id for this rendering of the page
     */
    String allocateClientId(ComponentResources resources);

    /**
     * Adds initialization script at {@link InitializationPriority#NORMAL} priority.
     * 
     * @param format
     *            format string (as per {@link String#format(String, Object...)}
     * @param arguments
     *            arguments referenced by format specifiers
     */
    void addScript(String format, Object... arguments);

    /**
     * Adds initialization script at the specified priority.
     * 
     * @param priority
     *            priority to use when adding the script
     * @param format
     *            format string (as per {@link String#format(String, Object...)}
     * @param arguments
     *            arguments referenced by format specifiers
     */
    void addScript(InitializationPriority priority, String format, Object... arguments);

    /**
     * Adds a call to a client-side function inside the Tapestry.Initializer namespace. Calls to this
     * method are aggregated into a call to the Tapestry.init() function. Initialization occurs at
     * {@link InitializationPriority#NORMAL} priority.
     * 
     * @param functionName
     *            name of client-side function (within Tapestry.Initializer namespace) to execute
     * @param parameter
     *            object to pass to the client-side function
     */
    void addInitializerCall(String functionName, JSONObject parameter);

    /**
     * Adds a call to a client-side function inside the Tapestry.Initializer namespace. Calls to this
     * method are aggregated into a call to the Tapestry.init() function. Initialization occurs at
     * the specified priority.
     * 
     * @param priority
     *            priority to use when adding the script
     * @param functionName
     *            name of client-side function (within Tapestry.Initializer namespace) to execute
     * @param parameter
     *            object to pass to the client-side function
     */
    void addInitializerCall(InitializationPriority priority, String functionName, JSONObject parameter);

    /**
     * Adds a call to a client-side function inside the Tapestry.Initializer namespace. Calls to this
     * method are aggregated into a call to the Tapestry.init() function. Initialization occurs at
     * {@link InitializationPriority#NORMAL} priority.
     * 
     * @param functionName
     *            name of client-side function (within Tapestry.Initializer namespace) to execute
     * @param parameter
     *            string to pass to function (typically, a client id)
     */
    void addInitializerCall(String functionName, String parameter);

    /**
     * Adds a call to a client-side function inside the Tapestry.Initializer namespace. Calls to this
     * method are aggregated into a call to the Tapestry.init() function. Initialization occurs at
     * the specified priority.
     * 
     * @param priority
     *            priority to use when adding the script
     * @param functionName
     *            name of client-side function (within Tapestry.Initializer namespace) to execute
     * @param parameter
     *            string to pass to function (typically, a client id)
     */
    void addInitializerCall(InitializationPriority priority, String functionName, String parameter);

    /**
     * Imports a JavaScript library as part of the rendered page. Libraries are added in the order
     * they are first imported; duplicate imports are ignored.
     * 
     * @see IncludeJavaScriptLibrary
     */
    void importJavascriptLibrary(Asset asset);

    /**
     * A convenience method that wraps the Asset as a {@link StylesheetLink}.
     * 
     * @param stylesheet
     *            asset for the stylesheet
     * @see #importStylesheet(StylesheetLink)
     */
    void importStylesheet(Asset stylesheet);

    /**
     * Imports a Cascading Style Sheet file as part of the rendered page. Stylesheets are added in the
     * order they are first imported; duplicate imports are ignored.
     * 
     * @param stylesheetLink
     *            encapsulates the link URL plus any additional options
     */
    void importStylesheet(StylesheetLink stylesheetLink);

    /**
     * Imports a {@link JavascriptStack} by name, a related set of JavaScript libraries and stylesheets.
     * Stacks are contributions to the {@link JavascriptStackSource} service. When
     * {@linkplain SymbolConstants#COMBINE_SCRIPTS Javascript aggregation} in enabled, the stack will be represented by
     * a single virtual URL; otherwise the individual asset URLs of the stack
     * will be added to the document.
     * 
     * @param stackName
     *            the name of the stack (case is ignored); the stack must exist
     */
    void importStack(String stackName);

    /**
     * Import a Javascript library with an arbitrary URL.
     */
    void importJavascriptLibrary(String libraryURL);
}