package de.universallp.va.client;

import de.universallp.va.client.gui.guide.Entry;
import de.universallp.va.client.gui.guide.EnumEntry;
import de.universallp.va.client.handler.GuideHandler;
import de.universallp.va.core.CommonProxy;
import de.universallp.va.core.block.VABlocks;
import de.universallp.va.core.item.ItemVA;
import de.universallp.va.core.util.VAPlayerController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Created by universallp on 23.03.2016 19:16.
 */
public class ClientProxy extends CommonProxy {

    public static Entry lastEntry;
    public static EnumEntry hoveredEntry;
    public static int guiScale = 0;

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        ItemVA.registerModels();
        VABlocks.registerModels();
        MinecraftForge.EVENT_BUS.register(new GuideHandler());
        GuideHandler.initVanillaEntries();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    @Override
    public void setReach(EntityLivingBase entity, float reach) {
        super.setReach(entity, reach);
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (entity == player) {
            if (!(mc.playerController instanceof VAPlayerController)) {
                WorldSettings.GameType type = ReflectionHelper.getPrivateValue(PlayerControllerMP.class, mc.playerController, "currentGameType", "field_78779_k", "k");
                NetHandlerPlayClient net = ReflectionHelper.getPrivateValue(PlayerControllerMP.class, mc.playerController, "netClientHandler", "field_78774_b", "b");
                VAPlayerController controller = new VAPlayerController(mc, net);
                boolean isFlying = player.capabilities.isFlying;
                boolean allowFlying = player.capabilities.allowFlying;
                controller.setGameType(type);
                player.capabilities.isFlying = isFlying;
                player.capabilities.allowFlying = allowFlying;
                mc.playerController = controller;
            }

            ((VAPlayerController) mc.playerController).setReachDistance(reach);
        }
    }
}
