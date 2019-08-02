package cn.xiaosheng996.NettyProtobufTcpServer;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class ProtoDecoder extends ByteToMessageDecoder{
  private final int limit;

  public ProtoDecoder(int limit){
	  this.limit = limit;
  }

  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception{
	  if (in.readableBytes() < 7)
		  return;
	  in.markReaderIndex();
	  byte head = in.readByte();
	  short length = in.readShort();
	  if ((length <= 0) || (length > this.limit))
		  throw new IllegalArgumentException();
	  int cmd = in.readInt();
	  if (in.readableBytes() < length - 4) {
		  in.resetReaderIndex();
		  return;
	  }
	  byte[] bytes = new byte[length - 4];
	  in.readBytes(bytes);
	  out.add(new Packet(head, cmd, bytes));
  }
}
