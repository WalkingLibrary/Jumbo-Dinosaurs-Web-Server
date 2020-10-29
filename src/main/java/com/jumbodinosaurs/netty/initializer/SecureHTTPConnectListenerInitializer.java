package com.jumbodinosaurs.netty.initializer;

import com.jumbodinosaurs.devlib.netty.ConnectionListenerInitializer;
import com.jumbodinosaurs.devlib.netty.handler.IHandlerHolder;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.log.LogManager;
import com.jumbodinosaurs.netty.exceptions.MissingCertificateException;
import com.jumbodinosaurs.netty.pipline.HTTPMessageFramer;
import com.jumbodinosaurs.netty.pipline.HTTPResponseEncoder;
import com.jumbodinosaurs.netty.pipline.SessionDecoder;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SniHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.DomainNameMapping;
import io.netty.util.Mapping;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;


public class SecureHTTPConnectListenerInitializer extends ConnectionListenerInitializer
{
    private Mapping<String, SslContext> domainToContextMap = null;
    
    public SecureHTTPConnectListenerInitializer(int port,
                                                IHandlerHolder iHandlerHolder)
    {
        super(port, iHandlerHolder);
        initMapping();
    }
    
    public void run()
    {
        if(domainToContextMap == null)
        {
            return;
        }
        super.run();
    }
    
    @Override
    protected void initChannel(SocketChannel channel) throws Exception
    {
    
        channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(500000));
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("sni", new SniHandler(this.domainToContextMap));
        pipeline.addLast("readTimeoutHandler", new ReadTimeoutHandler(15));
        pipeline.addLast("framer", new HTTPMessageFramer());
        pipeline.addLast("sessionDecoder", new SessionDecoder());
        pipeline.addLast("encoder", new HTTPResponseEncoder());
        pipeline.addLast("handler", this.handlerHolder.getInstance());
    
    
    }
    
    
    
    public void initMapping()
    {
        try
        {
            DomainNameMapping domainMap = null;
            for(SecureDomain domain : DomainManager.getDomains())
            {
                if(domain.hasCertificateFile())
                {
                    try
                    {
                        SslContext domainContext = getDomainContext((SecureDomain)domain);
                        if(domainMap == null)
                        {
                            domainMap = new DomainNameMapping(domainContext);
                        }
        
                        domainMap.add(domain.getDomain(), domainContext);
        
                    }
                    catch(MissingCertificateException e)
                    {
                        LogManager.consoleLogger.error(e.getMessage(), e);
                    }
                }
                
            }
            this.domainToContextMap = domainMap;
            
        }
        catch(Exception e)
        {
            LogManager.consoleLogger.error("Error Creating SSL Context", e);
        }
    }
    
    
    public SslContext getDomainContext(SecureDomain domain) throws Exception
    {
        try
        {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(domain.getCertificateFile()),
                          domain.getCertificatePassword().toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore temporaryKeyStore = null;
            trustManagerFactory.init(temporaryKeyStore);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, domain.getCertificatePassword().toCharArray());
            SslContext context = SslContextBuilder.forServer(keyManagerFactory).build();
            return context;
        }
        catch(FileNotFoundException e)
        {
            throw new MissingCertificateException(e);
        }
        catch(Exception e)
        {
            throw e;
        }
        
    }
    
    
}

