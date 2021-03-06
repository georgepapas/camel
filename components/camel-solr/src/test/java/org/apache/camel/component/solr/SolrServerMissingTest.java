/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.solr;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;

public class SolrServerMissingTest extends CamelTestSupport {

    @Test
    public void indexSingleDocumentToNonexistentServer() throws Exception {
        Exchange exchange = createExchangeWithBody(null);
        exchange.getIn().setHeader(SolrConstants.OPERATION, SolrConstants.OPERATION_INSERT);
        exchange.getIn().setHeader("SolrField.id", "MA147LL/A");
        template.send("direct:start", exchange);
        assertEquals(SolrServerException.class, exchange.getException().getClass());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").to("solr://localhost:8999/missingSolr");
            }
        };
    }
}
