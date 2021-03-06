// Copyright 2011 The Apache Software Foundation
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

package org.apache.tapestry5.yuicompressor.itest

import org.apache.tapestry5.test.SeleniumTestCase
import org.testng.annotations.Test;

class YUICompressorIntegrationTests extends SeleniumTestCase
{
    @Test
    void basic_functionality() {

        openBaseURL()

        addSelection "languages-avail", "label=Clojure"
        click "languages-select"

        addSelection "languages-avail", "label=Java"
        click "languages-select"
        
        clickAndWait SUBMIT

        assertText "selected", "CLOJURE, JAVA"
    }

    @Test
    void bad_js_is_reported() {
        openLinks "Bad JavaScript Demo"

        // We still get there, no the exception page.

        assertTitle "Tapestry 5: Bad JavaScript Demo"
    }
}
