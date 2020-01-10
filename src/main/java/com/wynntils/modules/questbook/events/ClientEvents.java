/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.questbook.events;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GameEvent;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynnClassChangeEvent;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.enums.AnalysePosition;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.managers.QuestManager;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientEvents implements Listener {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChat(GameEvent e)  {
        AnalysePosition position = AnalysePosition.QUESTS;
        boolean fullRead = false;

        if (e instanceof GameEvent.LevelUp)
            fullRead = true;
        else if (e instanceof GameEvent.QuestCompleted.MiniQuest) {
            QuestManager.completeQuest(((GameEvent.QuestCompleted.MiniQuest) e).getQuestName(), true);
            return;
        }
        else if (e instanceof GameEvent.QuestCompleted) {
            QuestManager.completeQuest(((GameEvent.QuestCompleted) e).getQuestName(), false);
            return;
        }
        else if (e instanceof GameEvent.QuestStarted.MiniQuest)
            position = AnalysePosition.MINIQUESTS;
        else if (e instanceof GameEvent.DiscoveryFound)
            position = AnalysePosition.DISCOVERIES;

        QuestManager.readQuestBook(position, fullRead);
    }


    @SubscribeEvent
    public void onClassChange(WynnClassChangeEvent e) {
        if (e.getCurrentClass() == ClassType.NONE) return;

        QuestManager.clearData();
    }

    @SubscribeEvent
    public void startReading(WynnWorldEvent.Leave e) {
        QuestManager.clearData();
    }

    boolean openQuestBook = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void clickOnQuestBookItem(PacketEvent<CPacketPlayerTryUseItem> e) {
        if (!QuestBookConfig.INSTANCE.allowCustomQuestbook
                || !Reference.onWorld || Reference.onNether || Reference.onWars
                || Minecraft.getMinecraft().player.inventory.currentItem != 7) return;

        openQuestBook = true;
        e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void clickOnQuestBookItemOnBlock(PacketEvent<CPacketPlayerTryUseItemOnBlock> e) {
        if (!QuestBookConfig.INSTANCE.allowCustomQuestbook
                || !Reference.onWorld || Reference.onNether || Reference.onWars
                || Minecraft.getMinecraft().player.inventory.currentItem != 7) return;

        openQuestBook = true;
        e.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void clickOnQuestBookEntity(PacketEvent<CPacketUseEntity> e) {
        if (!QuestBookConfig.INSTANCE.allowCustomQuestbook
                || !Reference.onWorld || Reference.onNether || Reference.onWars
                || Minecraft.getMinecraft().player.inventory.currentItem != 7) return;

        openQuestBook = true;
        e.setCanceled(true);
    }

    @SubscribeEvent
    public void updateQuestBook(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.START || !Reference.onWorld || Reference.onNether || Reference.onWars || Minecraft.getMinecraft().player.inventory == null) return;
        if (Minecraft.getMinecraft().player.inventory.getStackInSlot(7).isEmpty() || Minecraft.getMinecraft().player.inventory.getStackInSlot(7).getItem() != Items.WRITTEN_BOOK) return;

        if (!openQuestBook) return;
        openQuestBook = false;

        QuestBookPages.MAIN.getPage().open(true);

        if (!QuestManager.shouldRead()) return;
        if (QuestManager.hasInterrupted()) {
            QuestManager.readLastPage();
            return;
        }

        QuestManager.readQuestBook(AnalysePosition.QUESTS);
    }

}
