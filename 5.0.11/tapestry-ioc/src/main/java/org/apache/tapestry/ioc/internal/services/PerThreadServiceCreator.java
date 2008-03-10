// Copyright 2006, 2008 The Apache Software Foundation
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

/**
 *
 */
package org.apache.tapestry.ioc.internal.services;

import org.apache.tapestry.ioc.ObjectCreator;
import org.apache.tapestry.ioc.services.PerthreadManager;

/**
 * Provides per-thread implementations of services.
 */
public class PerThreadServiceCreator implements ObjectCreator
{
    private final PerthreadManager _perthreadManager;

    private final ObjectCreator _delegate;

    public PerThreadServiceCreator(PerthreadManager perthreadManager, ObjectCreator delegate)
    {
        _perthreadManager = perthreadManager;
        _delegate = delegate;
    }

    /**
     * For each thread, the first call will use the delegate
     * {@link org.apache.tapestry.ioc.ObjectCreator} to create an instance,
     * and later calls will reuse the same per-thread instance. The instance
     * is stored in the {@link org.apache.tapestry.ioc.services.PerthreadManager} and will
     * be released at the end of the request.
     */
    public Object createObject()
    {
        // Use the ObjectCreator instance as the key.  it will be unique.

        Object perthreadInstance = _perthreadManager.get(_delegate);

        if (perthreadInstance == null)
        {
            perthreadInstance = _delegate.createObject();
            _perthreadManager.put(_delegate, perthreadInstance);
        }

        return perthreadInstance;
    }
}