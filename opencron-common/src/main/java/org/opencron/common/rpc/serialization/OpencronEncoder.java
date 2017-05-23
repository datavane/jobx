package org.opencron.common.rpc.serialization;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by th on 2017/5/23.
 */
public class OpencronEncoder <T> extends  MessageToByteEncoder {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final byte type = 0X00;
    private final byte flag = 0X0F;

    private Serializer serializer = new HessianSerializer();
    private Class<T> clazz;

    public OpencronEncoder(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg,ByteBuf out) throws Exception {
        try {
            out.writeByte(type);
            out.writeByte(flag);
            byte[] data = serializer.encode(msg);
            out.writeInt(data.length);
            out.writeBytes(data);
            //logger.info("write type:{}, flag:{}, length:{}", type, flag, data.length);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
