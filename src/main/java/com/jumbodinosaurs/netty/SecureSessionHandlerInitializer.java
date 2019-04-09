package com.jumbodinosaurs.netty;

import com.jumbodinosaurs.ServerControl;
import com.jumbodinosaurs.objects.Domain;
import com.jumbodinosaurs.util.DataController;
import com.jumbodinosaurs.util.OperatorConsole;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.ssl.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.DomainNameMapping;
import io.netty.util.Mapping;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.ArrayList;


public class SecureSessionHandlerInitializer extends ChannelInitializer<SocketChannel> implements Runnable
{
    private Mapping<String, SslContext> domainToContextMap = null;
    
    public SecureSessionHandlerInitializer()
    {
        ArrayList<Domain> domains = new ArrayList<Domain>();
        
        if(ServerControl.getArguments() != null && ServerControl.getArguments().getDomains() != null)
        {
            domains = ServerControl.getArguments().getDomains();
        }
        
        File[] certificates = DataController.listFilesRecursive(DataController.certificateDirectory);
        
        for(Domain domain : domains)
        {
            for(File certificate : certificates)
            {
                //Certificates Should be JKS with .ks ending
                //Example www.jumbodinosaurs.com.ks -> www.jumbodinosaurs.com
                String certificateFileDomainName = DataController.getTypelessName(certificate);
                
                if(certificateFileDomainName.equals(domain.getDomain()))
                {
                    domain.setCertificateFile(certificate);
                }
            }
            if(domain.getCertificateFile() == null)
            {
                domains.remove(domain);
            }
        }
        
        try
        {
            if(domains.size() > 0)
            {
                Domain defaultDomain = domains.get(0);
                
                //DomainMapping Needs a Default SslContext
                KeyStore keyStoreDefault = KeyStore.getInstance("JKS");
                keyStoreDefault.load(new FileInputStream(defaultDomain.getCertificateFile()),
                                     defaultDomain.getCertificatePassword().toCharArray());
                TrustManagerFactory trustManagerFactoryDefault = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                KeyStore temporaryKeyStoreDefault = null;
                trustManagerFactoryDefault.init(temporaryKeyStoreDefault);
                KeyManagerFactory keyManagerFactoryDefault = KeyManagerFactory.getInstance("SunX509");
                keyManagerFactoryDefault.init(keyStoreDefault, defaultDomain.getCertificatePassword().toCharArray());
                
                
                DomainNameMapping domainMap = new DomainNameMapping(SslContextBuilder.forServer(keyManagerFactoryDefault).build());
                
                for(Domain domain : domains)
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
                    domainMap.add(domain.getDomain(), context);
                }
                this.domainToContextMap = domainMap;
                
                
            }
        }
        catch(Exception e)
        {
            OperatorConsole.printMessageFiltered("Error Creating SSL Context", false, true);
            e.printStackTrace();
        }
    }
    
    
    public void run()
    {
        if(domainToContextMap != null)
        {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try
            {
                OperatorConsole.sslThreadRunning = true;
                OperatorConsole.redirectToSSL = true;
                ServerBootstrap bootstrap = new ServerBootstrap().group(bossGroup, workerGroup).channel(
                        NioServerSocketChannel.class).childHandler(this);
                bootstrap.bind(443).sync().channel().closeFuture().sync();
                //443 ssl port
            }
            catch(InterruptedException e)
            {
                OperatorConsole.printMessageFiltered("Error Creating Server on port 443", false, true);
                e.printStackTrace();
                OperatorConsole.redirectToSSL = false;
                OperatorConsole.sslThreadRunning = false;
            }
            finally
            {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
        else
        {
            OperatorConsole.redirectToSSL = false;
            OperatorConsole.sslThreadRunning = false;
        }
    }
    
    @Override
    protected void initChannel(SocketChannel channel) throws Exception
    {
        
        
        ChannelPipeline pipeline = channel.pipeline();
        
        
        //Individual SSL Handler
        //SSLEngine sslEngine = jumboContext.newEngine(channel.alloc());
        //sslEngine.setUseClientMode(false);
        //sslEngine.setEnabledProtocols(sslEngine.getSupportedProtocols());
        //sslEngine.setEnabledCipherSuites(sslEngine.getSupportedCipherSuites());
        //sslEngine.setEnableSessionCreation(true);
        //pipeline.addLast("ssl",new SslHandler(sslEngine));
        //OutBound Messages Start From End of Pipeline and Work their way back
        //Inbound Messages Start From the Beginning of the pipeline and work their way out.
        //SNI Handler Auto Replaces It's Self with SSLHandler
        
        String delimiter = "\r\n\r\n";
        ByteBuf buffer = Unpooled.buffer(delimiter.getBytes().length);
        buffer.writeBytes(delimiter.getBytes());
        pipeline.addLast("sni", new SniHandler(this.domainToContextMap));
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(10000000, buffer));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new ResponseEncoder());
        pipeline.addLast("streamer", new ChunkedWriteHandler());
        pipeline.addLast("handler", new SecureSessionHandler());
        
        
    }
    
    
}

