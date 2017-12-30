package com.wynndevs.modules.expansion.customhud;

import com.wynndevs.ConfigValues;
import com.wynndevs.core.Utils;
import com.wynndevs.modules.market.utils.MarketUtils;
import com.wynndevs.modules.richpresence.utils.RichUtils;
import com.wynndevs.webapi.WebManager;
import com.wynndevs.webapi.profiles.ItemProfile;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CInventoryGUI extends GuiInventory {

    public static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###");

    public CInventoryGUI(EntityPlayer player) {
        super(player);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        Slot slot = getSlotUnderMouse();
        if (mc.player.inventory.getItemStack().isEmpty() && slot != null && slot.getHasStack()) {
            drawHoverItem(slot.getStack());
        }
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if(!ConfigValues.inventoryConfig.allowEmeraldCount) {
            return;
        }

        int blocks = 0;
        int liquid = 0;
        int emeralds = 0;

        for(int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            ItemStack it = mc.player.inventory.getStackInSlot(i);
            if(it == null || it.isEmpty()) {
                continue;
            }

            if(it.getItem() == Items.EMERALD) {
                emeralds+= it.getCount();
                continue;
            }
            if(it.getItem() == Item.getItemFromBlock(Blocks.EMERALD_BLOCK)) {
                blocks+= it.getCount();
                continue;
            }
            if(it.getItem() == Items.EXPERIENCE_BOTTLE) {
                liquid+= it.getCount();
                continue;
            }
        }

        int money = (liquid * 4096) + (blocks * 64) + emeralds;

        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);

        if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            String value = "$" + decimalFormat.format(money);
            mc.fontRenderer.drawString(value, 90 + (80 - mc.fontRenderer.getStringWidth(value)), 72, 4210752);
        }else{

            int leAmount = (int)Math.floor(money / 4096);
            money-= leAmount * 4096;

            int blockAmount = (int)Math.floor(money / 64);
            money-= blockAmount * 64;

            String value = "$" + (leAmount > 0 ? leAmount + "LE " : "") + (blockAmount > 0 ? blockAmount + "EB " : "") + (money > 0 ? money + "E" : "");
            if(value.equalsIgnoreCase("$")) {
                value = "$0";
            }

            if(value.substring(value.length() - 1).equalsIgnoreCase(" ")) {
                value = value.substring(0, value.length() - 1);
            }

            mc.fontRenderer.drawString(value, 90 + (80 - mc.fontRenderer.getStringWidth(value)), 72, 4210752);
        }

        GlStateManager.enableLighting();
    }

    public void drawHoverItem(ItemStack stack) {
        if(!WebManager.getItems().containsKey(RichUtils.stripColor(stack.getDisplayName()))) {
            return;
        }
        ItemProfile wItem = WebManager.getItems().get(RichUtils.stripColor(stack.getDisplayName()));

        List<String> actualLore = MarketUtils.getLore(stack);
        List<String> newLore = new ArrayList<>();
        for(int i = 0; i < actualLore.size(); i++) {
            String lore = actualLore.get(i);
            String wColor = RichUtils.stripColor(lore);

            if(!wColor.startsWith("+") && !wColor.startsWith("-")) {
                newLore.add(lore);
                continue;
            }

            String[] values = wColor.split(" ");

            if(values.length < 2) {
                newLore.add(lore);
                continue;
            }

            String pField = StringUtils.join(Arrays.copyOfRange(values, 1, values.length), " ");

            if(pField == null) {
                newLore.add(lore);
                continue;
            }

            boolean raw = !lore.contains("%");

            try{
                int amount = Integer.valueOf(values[0].replace("*", "").replace("%", "").replace("/3s", "").replace("/4s", ""));

                String fieldName;
                if(raw) {
                    fieldName = Utils.getFieldName("raw" + pField);
                    if(fieldName == null) {
                        fieldName = Utils.getFieldName(pField);
                    }
                }else{
                    fieldName = Utils.getFieldName(pField);
                }

                if(fieldName == null) {
                    newLore.add(lore);
                    continue;
                }

                Field f = wItem.getClass().getField(fieldName);
                if(f == null) {
                    newLore.add(lore);
                    continue;
                }

                int max = (int)Math.round(Integer.valueOf(String.valueOf(f.get(wItem))) * 1.3d);

                int percent = Math.round((amount * 100) / max);

                String color = "§";

                if(percent >= 97) {
                    color += "b";
                }else if(percent >= 80) {
                    color += "a";
                }else if(percent >= 30) {
                    color += "e";
                }else if(percent < 30) {
                    color += "c";
                }

                newLore.add(lore + color + " [" + percent + "%]");

            }catch (Exception ex) { ex.printStackTrace(); newLore.add(lore); }
        }

        NBTTagCompound nbt = stack.getTagCompound();
        NBTTagCompound display = nbt.getCompoundTag("display");
        NBTTagList tag = new NBTTagList();

        newLore.forEach(s -> tag.appendTag(new NBTTagString(s)));

        display.setTag("Lore", tag);
        nbt.setTag("display", display);
        stack.setTagCompound(nbt);

    }

}
