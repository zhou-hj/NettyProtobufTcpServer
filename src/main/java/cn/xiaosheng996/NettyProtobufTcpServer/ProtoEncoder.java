package cn.xiaosheng996.NettyProtobufTcpServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProtoEncoder extends MessageToByteEncoder<Packet>{
  private static final Logger log = LoggerFactory.getLogger(ProtoEncoder.class);
  private final int limit;

  public ProtoEncoder(int limit){
	  this.limit = limit;
  }

  protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf buf) throws Exception{
	  if ((packet.getBytes().length > this.limit) && (log.isWarnEnabled()))
		  log.warn("packet size[{}] is over limit[{}]", packet.getBytes().length, this.limit);
	  
	  buf.writeByte(packet.getHead());
	  buf.writeShort(packet.getBytes().length + 4);
	  buf.writeInt(packet.getCmd());
	  buf.writeBytes(packet.getBytes());
  }
}
