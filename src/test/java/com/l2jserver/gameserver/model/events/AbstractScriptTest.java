package com.l2jserver.gameserver.model.events;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.QuestItemChanceHolder;
import com.l2jserver.gameserver.model.itemcontainer.PcInventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AbstractScriptTest {

    @Mock
    private L2PcInstance player;

    @Mock
    private PcInventory inventory;

    @Test
    public void shouldReturnTrueIfQuestItemsAtLimit() {
        QuestItemChanceHolder questItem = new QuestItemChanceHolder(1, 10L);

        when(player.getInventory()).thenReturn(inventory);
        when(inventory.getInventoryItemCount(eq(1), anyInt())).thenReturn(10L);

        boolean result = AbstractScript.hasItemsAtLimit(player, questItem);

        assertThat(result).isTrue();
    }

    @Test
    public void shouldReturnFalseIfQuestItemsNotAtLimit() {
        QuestItemChanceHolder questItem = new QuestItemChanceHolder(1, 10L);

        when(player.getInventory()).thenReturn(inventory);
        when(inventory.getInventoryItemCount(eq(1), anyInt())).thenReturn(5L);

        boolean result = AbstractScript.hasItemsAtLimit(player, questItem);

        assertThat(result).isFalse();
    }
}