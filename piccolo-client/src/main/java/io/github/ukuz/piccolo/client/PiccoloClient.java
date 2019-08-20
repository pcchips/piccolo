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
package io.github.ukuz.piccolo.client;

import io.github.ukuz.piccolo.api.PiccoloContext;
import io.github.ukuz.piccolo.api.cache.CacheManager;
import io.github.ukuz.piccolo.api.common.Monitor;
import io.github.ukuz.piccolo.api.common.threadpool.ExecutorFactory;
import io.github.ukuz.piccolo.api.config.Environment;
import io.github.ukuz.piccolo.api.config.Properties;
import io.github.ukuz.piccolo.api.mq.MQClient;
import io.github.ukuz.piccolo.api.service.discovery.ServiceDiscovery;
import io.github.ukuz.piccolo.api.service.registry.ServiceRegistry;
import io.github.ukuz.piccolo.api.spi.SpiLoader;
import io.github.ukuz.piccolo.client.gateway.connection.GatewayConnectionFactory;
import io.github.ukuz.piccolo.client.gateway.connection.GatewayTcpConnectionFactory;
import io.github.ukuz.piccolo.client.router.CachedRemoteRouterManager;
import io.github.ukuz.piccolo.client.threadpool.ClientExecutorFactory;
import io.github.ukuz.piccolo.registry.zookeeper.ZKServiceRegistryAndDiscovery;

/**
 * @author ukuz90
 */
public class PiccoloClient implements PiccoloContext {

    private final Environment environment;
    private final ExecutorFactory executorFactory;
    private final ZKServiceRegistryAndDiscovery srd;
    private final CacheManager cacheManager;
    private final CachedRemoteRouterManager remoteRouterManager;
    private final GatewayConnectionFactory gatewayConnectionFactory;


    public PiccoloClient() {
        //initialize config
        environment = SpiLoader.getLoader(Environment.class).getExtension();
        environment.scanAllProperties();
        environment.load("piccolo-client.properties");

        //initialize executor
        executorFactory = new ClientExecutorFactory();

        cacheManager = SpiLoader.getLoader(CacheManager.class).getExtension();
        remoteRouterManager = new CachedRemoteRouterManager(cacheManager);

        srd = (ZKServiceRegistryAndDiscovery) SpiLoader.getLoader(ServiceRegistry.class).getExtension("zk");

        gatewayConnectionFactory = new GatewayTcpConnectionFactory(this);

    }

    @Override
    public Monitor getMonitor() {
        return null;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return srd;
    }

    @Override
    public ServiceDiscovery getServiceDiscovery() {
        return srd;
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public MQClient getMQClient() {
        return null;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public <T extends Properties> T getProperties(Class<T> clazz) {
        return null;
    }

    @Override
    public ExecutorFactory getExecutorFactory() {
        return executorFactory;
    }

    public CachedRemoteRouterManager getRemoteRouterManager() {
        return remoteRouterManager;
    }

    public GatewayConnectionFactory getGatewayConnectionFactory() {
        return gatewayConnectionFactory;
    }
}