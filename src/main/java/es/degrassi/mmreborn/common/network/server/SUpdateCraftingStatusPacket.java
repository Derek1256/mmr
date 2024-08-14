package es.degrassi.mmreborn.common.network.server;

import es.degrassi.mmreborn.ModularMachineryReborn;
import es.degrassi.mmreborn.common.entity.MachineControllerEntity;
import es.degrassi.mmreborn.common.machine.DynamicMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SUpdateCraftingStatusPacket(MachineControllerEntity.CraftingStatus status, BlockPos pos) implements CustomPacketPayload {

  public static final Type<SUpdateCraftingStatusPacket> TYPE = new Type<>(ModularMachineryReborn.rl("update_crafting_status"));

  public SUpdateCraftingStatusPacket(String type, String message, BlockPos pos) {
    this(MachineControllerEntity.CraftingStatus.of(type, message), pos);
  }

  @Override
  public Type<SUpdateCraftingStatusPacket> type() {
    return TYPE;
  }

  public SUpdateCraftingStatusPacket(FriendlyByteBuf friendlyByteBuf) {
    this(friendlyByteBuf.readUtf(), friendlyByteBuf.readUtf(), friendlyByteBuf.readBlockPos());
  }

  public static final StreamCodec<RegistryFriendlyByteBuf, SUpdateCraftingStatusPacket> CODEC = StreamCodec.composite(
    StreamCodec.composite(
      StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        StringRepresentable::getSerializedName,
        MachineControllerEntity.Type::fromString
      ),
      MachineControllerEntity.CraftingStatus::getStatus,
      ByteBufCodecs.STRING_UTF8,
      MachineControllerEntity.CraftingStatus::getUnlocMessage,
      MachineControllerEntity.CraftingStatus::of
    ),
    SUpdateCraftingStatusPacket::status,
    BlockPos.STREAM_CODEC,
    SUpdateCraftingStatusPacket::pos,
    SUpdateCraftingStatusPacket::new
  );

  public static void handle(SUpdateCraftingStatusPacket packet, IPayloadContext context) {
    if (context.flow().isClientbound())
      context.enqueueWork(() -> {
        if (context.player().level().getBlockEntity(packet.pos) instanceof MachineControllerEntity entity) {
          entity.setCraftingStatus(packet.status);
        }
      });
  }
}
