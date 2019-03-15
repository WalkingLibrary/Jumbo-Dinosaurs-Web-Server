package com.jumbodinosaurs.netty;

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


public class SecureSessionHandlerInitializer extends ChannelInitializer<SocketChannel> implements Runnable
{
    private String certificatePassword;
    public static boolean running = true;
    private Mapping<String, SslContext> mapping;

    public SecureSessionHandlerInitializer(String certificatePassword)
    {
        this.certificatePassword = certificatePassword;


        try
        {
            if(DataController.getCertificates().length > 0)
            {
                //DomainMapping Needs a Default SslContext
                KeyStore keyStoreDefault = KeyStore.getInstance("JKS");
                keyStoreDefault.load(new FileInputStream(DataController.getCertificates()[0]), this.certificatePassword.toCharArray());
                TrustManagerFactory trustManagerFactoryDefault = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                KeyStore temporaryKeyStoreDefault = null;
                trustManagerFactoryDefault.init(temporaryKeyStoreDefault);
                KeyManagerFactory keyManagerFactoryDefault = KeyManagerFactory.getInstance("SunX509");
                keyManagerFactoryDefault.init(keyStoreDefault, this.certificatePassword.toCharArray());
                DomainNameMapping domainmap = new DomainNameMapping(SslContextBuilder.forServer(keyManagerFactoryDefault).build());


                for (File certificateFile: DataController.getCertificates())
                {
                    KeyStore keyStore = KeyStore.getInstance("JKS");
                    keyStore.load(new FileInputStream(certificateFile), this.certificatePassword.toCharArray());
                    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    KeyStore temporaryKeyStore = null;
                    trustManagerFactory.init(temporaryKeyStore);
                    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
                    keyManagerFactory.init(keyStore, this.certificatePassword.toCharArray());
                    SslContext context = SslContextBuilder.forServer(keyManagerFactory).build();

                    //Certificates Should be JKS with .ks ending
                    domainmap.add(certificateFile.getName().substring(0,certificateFile.getName().length() - 3), context);
                }
                this.mapping = domainmap;
            }
            else
            {
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            OperatorConsole.printMessageFiltered("Error Creating SSL Context", false,true);
            e.printStackTrace();
        }
    }


    public void run()
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try
        {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(this);
            bootstrap.bind(443).sync().channel().closeFuture().sync();
            //443 ssl port
        }
        catch (Exception e)
        {
            e.printStackTrace();
            OperatorConsole.printMessageFiltered("Error Creating Server on port 443",false, true);
            this.running = false;
        }
        finally
        {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception
    {


        ChannelPipeline pipeline = channel.pipeline();


        //SSLEngine sslEngine = jumboContext.newEngine(channel.alloc());
        //sslEngine.setUseClientMode(false);
        //sslEngine.setEnabledProtocols(sslEngine.getSupportedProtocols());
        //sslEngine.setEnabledCipherSuites(sslEngine.getSupportedCipherSuites());
        //sslEngine.setEnableSessionCreation(true);
        //pipeline.addLast("ssl",new SslHandler(sslEngine));




        //SNI Handler Auto Replaces It's Self with SSLHandler
        pipeline.addLast("sni", new SniHandler(this.mapping));



        String delimiter = "\r\n\r\n";
        ByteBuf buffer = Unpooled.buffer(delimiter.getBytes().length);
        buffer.writeBytes(delimiter.getBytes());
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(10000000, buffer));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new ResponseEncoder());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast("handler", new SecureSessionHandler());

    }


}

