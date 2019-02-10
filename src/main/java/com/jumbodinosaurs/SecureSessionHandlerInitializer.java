package com.jumbodinosaurs;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;



public class SecureSessionHandlerInitializer extends ChannelInitializer<SocketChannel> implements Runnable
{
    private String certificatePassword;
    public static boolean running= true;

    public SecureSessionHandlerInitializer(String certificatePassword)
    {
        this.certificatePassword = certificatePassword;
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
            running = false;
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


        File keystore = new File("/etc/letsencrypt/live/www.jumbodinosaurs.com/jumbodinosaurs.ks");

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(keystore), this.certificatePassword.toCharArray());

        TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore tmpKS = null;
        tmFactory.init(tmpKS);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, this.certificatePassword.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");

        sslContext.init(kmf.getKeyManagers(), tmFactory.getTrustManagers(), null);

        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(false);
        sslEngine.setEnabledProtocols(sslEngine.getSupportedProtocols());
        sslEngine.setEnabledCipherSuites(sslEngine.getSupportedCipherSuites());
        sslEngine.setEnableSessionCreation(true);
        pipeline.addLast("ssl", new SslHandler(sslEngine));


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

