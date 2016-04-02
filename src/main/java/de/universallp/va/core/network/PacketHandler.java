package de.universallp.va.core.network;

import de.universallp.va.core.network.messages.MessagePlaySound;
import de.universallp.va.core.network.messages.MessageSetFieldClient;
import de.universallp.va.core.network.messages.MessageSetFieldServer;
import de.universallp.va.core.util.libs.LibNames;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by universallp on 20.03.2016 15:45.
 */
public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LibNames.MOD_ID);
    private static int ID = 0;

    public static void register() {
        INSTANCE.registerMessage(MessageSetFieldClient.class, MessageSetFieldClient.class, ID++, Side.CLIENT);
        INSTANCE.registerMessage(MessageSetFieldServer.class, MessageSetFieldServer.class, ID++, Side.SERVER);
        INSTANCE.registerMessage(MessagePlaySound.class, MessagePlaySound.class, ID++, Side.CLIENT);
    }

    public static void sendTo(IMessage m, EntityPlayerMP p) {
        INSTANCE.sendTo(m, p);
    }

    /**
     * Syncs all field values from a TileEntity with the server
     * TileEntity must implement IInventory/ILockableContainer
     *
     * @param pl
     * @param te
     * @param startField
     * @param endField
     */
    public static void syncFields(EntityPlayer pl, TileEntity te, int startField, int endField) {
        if (!(te instanceof ILockableContainer))
            return;

        byte[] values = new byte[endField - startField];
        int[] fields = new int[endField - startField];

        int index = 0;
        for (int i = startField; i <= endField; i++) {
            fields[index] = i;
            values[index] = (byte) ((IInventory) te).getField(i);
        }

        sendTo(new MessageSetFieldClient(fields, values, te.getPos()), (EntityPlayerMP) pl);
    }

    public static void sendToServer(IMessage msg) {
        INSTANCE.sendToServer(msg);
    }

    public static void writeBlockPos(ByteBuf to, BlockPos pos) {
        to.writeInt(pos.getX());
        to.writeInt(pos.getY());
        to.writeInt(pos.getZ());
    }

    public static BlockPos readBlockPos(ByteBuf from) {
        return new BlockPos(from.readInt(), from.readInt(), from.readInt());
    }
}
