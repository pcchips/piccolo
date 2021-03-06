/*
 * Copyright 2019 ukuz90
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.ukuz.piccolo.common.route;

import io.github.ukuz.piccolo.api.PiccoloContext;
import io.github.ukuz.piccolo.api.configcenter.ConfigurationChangeType;
import io.github.ukuz.piccolo.api.configcenter.ConfigurationChangedEvent;
import io.github.ukuz.piccolo.api.configcenter.ConfigurationListener;
import io.github.ukuz.piccolo.api.configcenter.DynamicConfiguration;
import io.github.ukuz.piccolo.api.external.common.Assert;
import io.github.ukuz.piccolo.api.route.RouteLocator;
import io.github.ukuz.piccolo.api.service.AbstractService;
import io.github.ukuz.piccolo.api.service.ServiceException;
import io.github.ukuz.piccolo.common.json.Jsons;
import org.apache.kafka.common.utils.CopyOnWriteMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author ukuz90
 */
public class CacheRouteLocator extends AbstractService implements RouteLocator<String, String>, ConfigurationListener {

    private DynamicConfiguration configuration;
    private static final String key = "piccolo.routes";
    private CopyOnWriteMap<String, String> cacheRouteMap = new CopyOnWriteMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheRouteLocator.class);

    @Override
    public void init(PiccoloContext context) throws ServiceException {
        this.configuration = context.getDynamicConfiguration();
        String contentInfo = configuration.getProperty(key);
        LOGGER.info("CacheRouteLocator init key: {} contentInfo: {}", key, contentInfo);
        if (contentInfo != null) {
            Map<String, String> data = Jsons.fromJson(contentInfo, Map.class);
            cacheRouteMap.putAll(data);
        }
        //register listener
        this.configuration.addListener(key, this);
    }

    @Override
    public void route(String routeKey, String service) {
        Assert.notEmptyString(routeKey, "routeKey must not be empty");
        Assert.notEmptyString(service, "service must not be empty");
        if (cacheRouteMap.containsKey(routeKey) && cacheRouteMap.get(routeKey).equals(service)) {
            return;
        }
        LOGGER.info("CacheRouteLocator route key: {} service: {}", routeKey, service);
        cacheRouteMap.put(routeKey, service);
        configuration.setProperty(key, Jsons.toJson(cacheRouteMap));
    }

    @Override
    public String getRoute(String routeKey) {
        Assert.notEmptyString(routeKey, "routeKey must not be empty");
        return cacheRouteMap.get(routeKey);
    }

    @Override
    public void onConfigurationChanged(ConfigurationChangedEvent event) {
        LOGGER.info("CacheRouteLocator onConfigurationChanged event", event);
        if (event.getType() == ConfigurationChangeType.DELETED) {
            cacheRouteMap.clear();
        } else {
            Map<String, String> data = Jsons.fromJson(event.getValue(), Map.class);
            cacheRouteMap.clear();
            cacheRouteMap.putAll(data);
        }
    }
}
