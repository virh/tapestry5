// Copyright 2006 The Apache Software Foundation
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

package org.apache.tapestry.internal.parser;

import org.apache.tapestry.ioc.BaseLocatable;
import org.apache.tapestry.ioc.Location;

/**
 * Base class for tokens parsed out of a template. The set of classes rooted here are effectively object encapsulations
 * of the events generated by a SAX parser.
 */
public abstract class TemplateToken extends BaseLocatable
{
    private final TokenType _tokenType;

    protected TemplateToken(TokenType tokenType, Location location)
    {
        super(location);
        _tokenType = tokenType;
    }

    public TokenType getTokenType()
    {
        return _tokenType;
    }
}
