/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.rave.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Reads property from the default location unless a system property {@literal portal.override.properties} is set
 */
public class OverridablePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    private static final String PORTAL_OVERRIDE_PROPERTIES = "portal.override.properties";
    private static final String CLASSPATH = "classpath:";

    @Override
    public void setLocation(Resource location) {
        Resource locationResource;
        final String overrideProperty = System.getProperty(PORTAL_OVERRIDE_PROPERTIES);
        
        if (StringUtils.isBlank(overrideProperty)) {
            locationResource = location;
        } else if (overrideProperty.startsWith(CLASSPATH)) {
            locationResource = new ClassPathResource(overrideProperty.substring(CLASSPATH.length()));
        } else {
            locationResource = new FileSystemResource(overrideProperty);
        }
        super.setLocation(locationResource);
    }

}