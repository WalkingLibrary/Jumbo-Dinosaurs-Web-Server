package com.jumbodinosaurs.netty.initializer;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.netty.exceptions.MissingCertificateException;
import com.jumbodinosaurs.netty.handler.IHandlerHolder;
import com.jumbodinosaurs.netty.pipline.HTTPResponseEncoder;
import com.jumbodinosaurs.netty.pipline.SessionDecoder;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.ssl.SniHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.stream.ChunkedWriteHandler;
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
        
        
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("sni", new SniHandler(this.domainToContextMap));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("sessionDecoder", new SessionDecoder());
        pipeline.addLast("encoder", new HTTPResponseEncoder());
        pipeline.addLast("streamer", new ChunkedWriteHandler());
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
                        e.printStackTrace();
                    }
                }
                
            }
            this.domainToContextMap = domainMap;
            
        }
        catch(Exception e)
        {
            OperatorConsole.printMessageFiltered("Error Creating SSL Context", false, true);
            e.printStackTrace();
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

