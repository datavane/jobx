package com.jobxhub.rpc.netty.idle;

import com.jobxhub.rpc.netty.idle.domain.IdleRequest;
import com.jobxhub.rpc.netty.idle.domain.IdleResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Autorun
 */
@ChannelHandler.Sharable
public class IdleClientHandler extends SimpleChannelInboundHandler<IdleResponse> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IdleResponse msg) throws Exception {
		System.out.println(msg);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (!(evt instanceof IdleStateEvent)) {
			super.userEventTriggered(ctx, evt);
			return;
		}
		IdleStateEvent event = (IdleStateEvent) evt;
		String side = "REQUEST";
		String remoteAddress = ctx.channel().remoteAddress().toString();
		Date currentDate = new Date();
		logger.debug("[JobX] Idle request, requestType: [{}], remoteInfo: [{}], eventType: [{}]",
				side,
				remoteAddress,
				event.state()
		);
		ctx.writeAndFlush(new IdleRequest().setEvent(event).setRemoteAddr(remoteAddress).setSide(side).setTime(currentDate));
	}
}
