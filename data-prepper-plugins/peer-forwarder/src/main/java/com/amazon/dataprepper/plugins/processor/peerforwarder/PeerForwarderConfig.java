package com.amazon.dataprepper.plugins.processor.peerforwarder;

import com.amazon.dataprepper.model.configuration.PluginSetting;
import com.amazon.dataprepper.plugins.processor.peerforwarder.discovery.PeerListProvider;
import com.amazon.dataprepper.plugins.processor.peerforwarder.discovery.PeerListProviderFactory;

import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;

public class PeerForwarderConfig {
    public static final String TIME_OUT = "time_out";
    public static final String MAX_NUM_SPANS_PER_REQUEST = "span_agg_count";
    public static final int NUM_VIRTUAL_NODES = 10;
    public static final String DISCOVERY_MODE = "discovery_mode";
    public static final String HOSTNAME_FOR_DNS_LOOKUP = "hostname_for_dns_lookup";
    public static final String STATIC_ENDPOINTS = "static_endpoints";
    public static final String SSL = "ssl";
    public static final String SSL_KEY_CERT_FILE = "sslKeyCertChainFile";
    private static final boolean DEFAULT_SSL = false;

    private final HashRing hashRing;
    private final PeerClientPool peerClientPool;
    private final int timeOut;
    private final int maxNumSpansPerRequest;

    private PeerForwarderConfig(final PeerClientPool peerClientPool,
                                final HashRing hashRing,
                                final int timeOut,
                                final int maxNumSpansPerRequest) {
        checkNotNull(peerClientPool);
        checkNotNull(hashRing);

        this.peerClientPool = peerClientPool;
        this.hashRing = hashRing;
        this.timeOut = timeOut;
        this.maxNumSpansPerRequest = maxNumSpansPerRequest;
    }

    public static PeerForwarderConfig buildConfig(final PluginSetting pluginSetting) {
        final PeerListProvider peerListProvider = new PeerListProviderFactory().createProvider(pluginSetting);
        final HashRing hashRing = new HashRing(peerListProvider, NUM_VIRTUAL_NODES);
        final PeerClientPool peerClientPool = PeerClientPool.getInstance();
        peerClientPool.setClientTimeoutSeconds(3);
        final boolean ssl = pluginSetting.getBooleanOrDefault(SSL, DEFAULT_SSL);
        final String sslKeyCertChainFilePath = pluginSetting.getStringOrDefault(SSL_KEY_CERT_FILE, null);
        final File sslKeyCertChainFile;
        if (ssl) {
            try {
                sslKeyCertChainFile = new File(sslKeyCertChainFilePath);
            } catch (Exception e) {
                throw new IllegalArgumentException(String.format("%s is enable, %s is invalid file path", SSL, SSL_KEY_CERT_FILE));
            }
        } else {
            sslKeyCertChainFile = null;
        }
        peerClientPool.setIsSsl(ssl);
        peerClientPool.setSslKeyCertChainFile(sslKeyCertChainFile);

        return new PeerForwarderConfig(
                peerClientPool,
                hashRing,
                pluginSetting.getIntegerOrDefault(TIME_OUT, 3),
                pluginSetting.getIntegerOrDefault(MAX_NUM_SPANS_PER_REQUEST, 48));
    }

    public HashRing getHashRing() {
        return hashRing;
    }

    public PeerClientPool getPeerClientPool() {
        return peerClientPool;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public int getMaxNumSpansPerRequest() {
        return maxNumSpansPerRequest;
    }
}
