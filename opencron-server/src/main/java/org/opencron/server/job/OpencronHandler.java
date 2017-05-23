package org.opencron.server.job;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.opencron.common.rpc.model.Response;

/**
 * Created by benjobs on 2017/5/20.
 */

public abstract class OpencronHandler extends SimpleChannelInboundHandler<Response> {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)  throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
