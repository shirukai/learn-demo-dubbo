package org.apache.dubbo.registry;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.common.utils.UrlUtils;

import org.apache.dubbo.rpc.RpcContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * SimpleRegistryService
 */
public class SimpleRegistryService extends AbstractRegistryService {

    private final static Logger logger = LoggerFactory.getLogger(SimpleRegistryService.class);
    private final ConcurrentMap<String, ConcurrentMap<String, URL>> remoteRegistered = new ConcurrentHashMap<String, ConcurrentMap<String, URL>>();
    private final ConcurrentMap<String, ConcurrentMap<String, NotifyListener>> remoteListeners = new ConcurrentHashMap<String, ConcurrentMap<String, NotifyListener>>();
    private List<String> registries;

    @Override
    public void register(String service, URL url) {
        super.register(service, url);
        String client = RpcContext.getContext().getRemoteAddressString();
        Map<String, URL> urls = remoteRegistered.get(client);
        if (urls == null) {
            remoteRegistered.putIfAbsent(client, new ConcurrentHashMap<String, URL>());
            urls = remoteRegistered.get(client);
        }
        urls.put(service, url);
        notify(service, getRegistered().get(service));
    }

    @Override
    public void unregister(String service, URL url) {
        super.unregister(service, url);
        String client = RpcContext.getContext().getRemoteAddressString();
        Map<String, URL> urls = remoteRegistered.get(client);
        if (urls != null && urls.size() > 0) {
            urls.remove(service);
        }
        notify(service, getRegistered().get(service));
    }

    @Override
    public void subscribe(String service, URL url, NotifyListener listener) {
        String client = RpcContext.getContext().getRemoteAddressString();
        if (logger.isInfoEnabled()) {
            logger.info("[subscribe] service: " + service + ",client:" + client);
        }
        List<URL> urls = getRegistered().get(service);
        if ((RegistryService.class.getName() + ":0.0.0").equals(service)
                && (urls == null || urls.size() == 0)) {
            register(service, new URL("dubbo",
                    NetUtils.getLocalHost(),
                    RpcContext.getContext().getLocalPort(),
                    org.apache.dubbo.registry.RegistryService.class.getName(),
                    url.getParameters()));
            List<String> rs = registries;
            if (rs != null && rs.size() > 0) {
                for (String registry : rs) {
                    register(service, UrlUtils.parseURL(registry, url.getParameters()));
                }
            }
        }
        super.subscribe(service, url, listener);

        Map<String, NotifyListener> listeners = remoteListeners.get(client);
        if (listeners == null) {
            remoteListeners.putIfAbsent(client, new ConcurrentHashMap<String, NotifyListener>());
            listeners = remoteListeners.get(client);
        }
        listeners.put(service, listener);
        urls = getRegistered().get(service);
        if (urls != null && urls.size() > 0) {
            listener.notify(urls);
        }

    }

    public void disconnect(){

    }
}
