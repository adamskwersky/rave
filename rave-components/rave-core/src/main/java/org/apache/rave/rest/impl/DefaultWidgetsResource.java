/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.rave.rest.impl;


import com.google.common.collect.Lists;
import com.google.inject.Inject;
import org.apache.rave.portal.model.impl.WidgetImpl;
import org.apache.rave.portal.service.WidgetService;
import org.apache.rave.rest.WidgetsResource;
import org.apache.rave.rest.exception.ResourceNotFoundException;
import org.apache.rave.rest.model.SearchResult;
import org.apache.rave.rest.model.Widget;

import java.util.List;

public class DefaultWidgetsResource implements WidgetsResource {

    private WidgetService widgetService;

    @Override
    public SearchResult<Widget> getWidgets() {
        SearchResult<org.apache.rave.model.Widget> fromDb = widgetService.getAll();
        List<Widget> widget = convert(fromDb.getResultSet());
        return new SearchResult<Widget>(widget, fromDb.getTotalResults());
    }

    @Override
    public Widget getWidget(String id) {
        org.apache.rave.model.Widget widget = getFromService(id);
        return new Widget(widget);
    }

    @Override
    public Widget updateWidget(String id, Widget widget) {
        org.apache.rave.model.Widget fromDb = getFromService(id);
        updateProperties(widget, fromDb);
        widgetService.updateWidget(fromDb);
        return new Widget(getFromService(id));
    }

    @Override
    public Widget createWidget(Widget widget) {
        WidgetImpl toDb = new WidgetImpl();
        toDb.setId(widget.getId());
        updateProperties(widget, toDb);
        return new Widget(widgetService.registerNewWidget(toDb));
    }

    @Inject
    public void setWidgetService(WidgetService widgetService) {
        this.widgetService = widgetService;
    }


    private List<Widget> convert(List<org.apache.rave.model.Widget> resultSet) {
        List<Widget> widgets = Lists.newArrayListWithExpectedSize(resultSet.size());
        for(org.apache.rave.model.Widget widget: resultSet) {
            widgets.add(new Widget(widget));
        }
        return widgets;
    }

    private org.apache.rave.model.Widget getFromService(String id) {
        org.apache.rave.model.Widget widget = widgetService.getWidget(id);
        if(widget == null) {
            throw new ResourceNotFoundException("Could not find widget with ID " + id);
        }
        return widget;
    }

    private void updateProperties(Widget widget, org.apache.rave.model.Widget fromDb) {
        fromDb.setTitle(widget.getTitle());
        fromDb.setTitleUrl(widget.getTitleUrl());
        fromDb.setUrl(widget.getUrl());
        fromDb.setThumbnailUrl(widget.getThumbnailUrl());
        fromDb.setScreenshotUrl(widget.getScreenshotUrl());
        fromDb.setType(widget.getType());
        fromDb.setAuthor(widget.getAuthor());
        fromDb.setAuthorEmail(widget.getAuthorEmail());
        fromDb.setDescription(widget.getDescription());
        fromDb.setWidgetStatus(widget.getStatus());
        fromDb.setDisableRendering(widget.isDisable());
        fromDb.setDisableRenderingMessage(widget.getDisabledMessage());
        fromDb.setFeatured(widget.isFeatured());
        fromDb.setProperties(widget.getProperties());
    }
}
