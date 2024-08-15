package es.degrassi.mmreborn.common.network.server;

import es.degrassi.mmreborn.ModularMachineryReborn;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SOpenFilePacket(String path) implements CustomPacketPayload {

  public static final Type<SOpenFilePacket> TYPE = new Type<>(ModularMachineryReborn.rl("open_file"));

  public static final StreamCodec<ByteBuf, SOpenFilePacket> CODEC = ByteBufCodecs.STRING_UTF8.map(SOpenFilePacket::new, SOpenFilePacket::path);

  @Override
  public Type<SOpenFilePacket> type() {
    return TYPE;
  }

  public static void handle(SOpenFilePacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> Util.getPlatform().openUri(packet.path));
  }
}
