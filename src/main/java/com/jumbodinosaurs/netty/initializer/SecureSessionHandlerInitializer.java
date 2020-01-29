package com.jumbodinosaurs.netty.initializer;

import com.jumbodinosaurs.commands.OperatorConsole;
import com.jumbodinosaurs.domain.DomainManager;
import com.jumbodinosaurs.domain.util.Domain;
import com.jumbodinosaurs.domain.util.SecureDomain;
import com.jumbodinosaurs.netty.ResponseEncoder;
import com.jumbodinosaurs.netty.exceptions.MissingCertificateException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
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


public class SecureSessionHandlerInitializer extends SessionHandlerInitializer
{
    private Mapping<String, SslContext> domainToContextMap = null;
    
    public SecureSessionHandlerInitializer(int port,
                                           SimpleChannelInboundHandler<String> handler,
                                           Mapping<String, SslContext> domainToContextMap)
    {
        super(port, handler);
        this.domainToContextMap = domainToContextMap;
        initMapping();
    }
    
    public void run()
    {
        if(domainToContextMap == null)
        {
            OperatorConsole.redirectToSSL = false;
            OperatorConsole.sslThreadRunning = false;
            return;
        }
        super.run();
    }
    
    @Override
    protected void initChannel(SocketChannel channel) throws Exception
    {
        
        
        ChannelPipeline pipeline = channel.pipeline();
        String delimiter = "\r\n\r\n";
        ByteBuf buffer = Unpooled.buffer(delimiter.getBytes().length);
        buffer.writeBytes(delimiter.getBytes());
        pipeline.addLast("sni", new SniHandler(this.domainToContextMap));
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(10000000, buffer));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new ResponseEncoder());
        pipeline.addLast("streamer", new ChunkedWriteHandler());
        pipeline.addLast("handler", this.handler);
        
        
    }
    
    
    
    public void initMapping()
    {
        try
        {
            DomainNameMapping domainMap = null;
            for(Domain domain : DomainManager.getDomains())
            {
                if(domain instanceof SecureDomain)
                {
                    try
                    {
                        SslContext domainContext = getDomainContext((SecureDomain) domain);
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

