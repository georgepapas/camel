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
package org.apache.camel.management.mbean;

import org.apache.camel.CamelContext;
import org.apache.camel.ManagementStatisticsLevel;
import org.apache.camel.Processor;
import org.apache.camel.Route;
import org.apache.camel.ServiceStatus;
import org.apache.camel.StatefulService;
import org.apache.camel.api.management.ManagedInstance;
import org.apache.camel.api.management.ManagedResource;
import org.apache.camel.api.management.mbean.ManagedProcessorMBean;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.util.ServiceHelper;

/**
 * @version 
 */
@ManagedResource(description = "Managed Processor")
public class ManagedProcessor extends ManagedPerformanceCounter implements ManagedInstance, ManagedProcessorMBean {

    private final CamelContext context;
    private final Processor processor;
    private final ProcessorDefinition<?> definition;
    private final String id;
    private Route route;

    public ManagedProcessor(CamelContext context, Processor processor, ProcessorDefinition<?> definition) {
        this.context = context;
        this.processor = processor;
        this.definition = definition;
        this.id = definition.idOrCreate(context.getNodeIdFactory());

        boolean enabled = context.getManagementStrategy().getStatisticsLevel() == ManagementStatisticsLevel.All;
        setStatisticsEnabled(enabled);
    }

    public CamelContext getContext() {
        return context;
    }

    public Processor getProcessor() {
        return processor;
    }

    public ProcessorDefinition<?> getDefinition() {
        return definition;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getState() {
        // must use String type to be sure remote JMX can read the attribute without requiring Camel classes.
        if (processor instanceof StatefulService) {
            ServiceStatus status = ((StatefulService) processor).getStatus();
            return status.name();
        }

        // assume started if not a ServiceSupport instance
        return ServiceStatus.Started.name();
    }

    public String getCamelId() {
        return context.getName();
    }

    public String getRouteId() {
        if (route != null) {
            return route.getId();
        }
        return null;
    }

    public String getProcessorId() {
        return id;
    }

    public void start() throws Exception {
        if (!context.getStatus().isStarted()) {
            throw new IllegalArgumentException("CamelContext is not started");
        }
        ServiceHelper.startService(getProcessor());
    }

    public void stop() throws Exception {
        if (!context.getStatus().isStarted()) {
            throw new IllegalArgumentException("CamelContext is not started");
        }
        ServiceHelper.stopService(getProcessor());
    }

    public Object getInstance() {
        return processor;
    }
}
