package de.universallp.va.core.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import de.universallp.va.core.handler.ConfigHandler;
import de.universallp.va.core.item.VAItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.*;
import java.util.List;

/**
 * Created by universallp on 22.03.2016 14:33 16:31.
 * This file is part of VanillaAutomation which is licenced
 * under the MOZILLA PUBLIC LICENCE 2.0 - mozilla.org/en-US/MPL/2.0/
 * github.com/univrsal/VanillaAutomation
 */
public class Utils {

    public static void drawWrappedString(String s, int x, int y, int maxWidth, Color c, boolean shadow, FontRenderer f) {

        List<String> lines = Arrays.asList(s.split("\\\\n"));

        List<String> descriptionLinesWrapped = new ArrayList<String>();
        for (String descriptionLine : lines) {
            List<String> textLines = f.listFormattedStringToWidth(descriptionLine, maxWidth);
            descriptionLinesWrapped.addAll(textLines);
        }

        for (String line : descriptionLinesWrapped) {
            line = line.replaceAll("7n", "\\\\n");
            f.drawString(line, x, y, c.getRGB());
            y += f.FONT_HEIGHT + 2;
        }
    }

    public static List<String> readDescFromStack(ItemStack s) {
        if (s.hasTagCompound()) {
            List<String> l = new ArrayList<String>();
            NBTTagCompound tag = s.getTagCompound();

            if (tag.hasKey("display")) {
                NBTTagCompound display = tag.getCompoundTag("display");
                if (display.getTagId("Lore") == 9) {
                    NBTTagList nbttaglist3 = display.getTagList("Lore", 8);

                    if (!nbttaglist3.hasNoTags()) {
                        for (int l1 = 0; l1 < nbttaglist3.tagCount(); ++l1) {
                            l.add((nbttaglist3.getStringTagAt(l1)));
                        }
                    }
                }
                return l;
            }
        }
        return null;
    }

    public static ItemStack withDescription(ItemStack s, List<String> desc) {
        ItemStack copy = s.copy();

        if (desc == null && copy.hasTagCompound()) {
            NBTTagCompound tag = copy.getTagCompound();
            if (tag.hasKey("display")) {
                tag.getCompoundTag("display").removeTag("Lore");
            }
            copy.setTagCompound(tag);
        } else {
            NBTTagCompound tag;
            if (copy.hasTagCompound())
                tag = copy.getTagCompound();
            else
                tag = new NBTTagCompound();


            NBTTagList list = new NBTTagList();

            for (int i = 0; i < desc.size(); i++) {
                NBTTagString line = new NBTTagString(ChatFormatting.RESET + "" + ChatFormatting.GRAY + desc.get(i));
                list.appendTag(line);
            }

            NBTTagCompound display;
            if (tag.hasKey("display")) {
                display = tag.getCompoundTag("display");
                display.setTag("Lore", list);
            } else {
                display = new NBTTagCompound();
                display.setTag("Lore", list);
            }
            tag.setTag("display", display);
            copy.setTagCompound(tag);
        }
        return copy;
    }

    public static int getRedstoneAnyDirection(World w, BlockPos pos) {
        int s = 0;
        for (EnumFacing f : EnumFacing.VALUES) {
            int s2 = w.getStrongPower(pos, f);
            s = s2 > s ? s2 : s;
        }
        return s;
    }

    public static int getReach(ItemStack stack) {
        if (stack.hasDisplayName()) {
            String name = stack.getDisplayName();
            String[] s = name.split(": ");

            if (s.length == 2) {
                try {
                    int reach = Integer.valueOf(s[1]);
                    return reach > ConfigHandler.DISPENSER_REACH_MAX ? ConfigHandler.DISPENSER_REACH_MAX : reach;
                } catch (NumberFormatException e) {
                    return 1;
                }
            }

        }
        return 1;
    }

    public static Vec3i extend(Vec3i v, int i) {
        return new Vec3i(v.getX() * i, v.getY() * i, v.getZ() * i);
    }

    public static void drawWrappedString(String s, int x, int y, int maxWidth, FontRenderer f) {
        drawWrappedString(s, x, y, maxWidth, new Color(130, 130, 130), false, f);
    }


    public static boolean carriesItem(Item item, EntityPlayer p) {
        if (p == null || item == null)
            return false;

        if (!p.getHeldItemMainhand().isEmpty() && p.getHeldItemMainhand().getItem().equals(item))
            return true;

        if (!p.getHeldItemOffhand().isEmpty() && p.getHeldItemOffhand().getItem().equals(item))
            return true;

        return false;
    }

    public static ItemStack decreaseStack(ItemStack stack, int amount) {
        if (stack.getCount() > amount)
            stack.shrink(amount);
        else
            return ItemStack.EMPTY;
        return stack;
    }

    public static EnumFacing getNextFacing(EnumFacing f) {
        if (f == EnumFacing.EAST)
            return EnumFacing.DOWN;
        else
            return EnumFacing.values()[f.ordinal() + 1];
    }

    public static String getModName(ItemStack s) {
        if (s == null)
            return "nope";

        String modID = s.getItem().getRegistryName().getResourceDomain();
        return modID == null ? "Minecraft" : modID;
    }

    public static void setConfigValue(File f, String find, String replace) {
        try {
            BufferedReader file = new BufferedReader(new FileReader(f));
            String line;
            String input = "";

            while ((line = file.readLine()) != null) {
                if (line.contains(find))
                    line = find + replace;
                input += line + '\n';
            }

            file.close();

            FileOutputStream fileOut = new FileOutputStream(f);
            fileOut.write(input.getBytes());
            fileOut.close();

        } catch (Exception e) {
            LogHelper.logError("Problem writing config file.");
        }
    }

    public static boolean mouseInRect(int rectX, int rectY, int w, int h, int mouseX, int mouseY) {
        return mouseX > rectX && mouseX < rectX + w && mouseY > rectY && mouseY < rectY + h;
    }
}
