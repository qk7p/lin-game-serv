/*
 * Copyright © 2004-2021 L2J Server
 *
 * This file is part of L2J Server.
 *
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.model.skills.targets;

import static com.l2jserver.gameserver.model.skills.targets.AffectObject.ALL;
import static com.l2jserver.gameserver.model.skills.targets.AffectObject.CLAN;
import static com.l2jserver.gameserver.model.skills.targets.AffectObject.FRIEND;
import static com.l2jserver.gameserver.model.skills.targets.AffectObject.HIDDEN_PLACE;
import static com.l2jserver.gameserver.model.skills.targets.AffectObject.INVISIBLE;
import static com.l2jserver.gameserver.model.skills.targets.AffectObject.NONE;
import static com.l2jserver.gameserver.model.skills.targets.AffectObject.NOT_FRIEND;
import static com.l2jserver.gameserver.model.skills.targets.AffectObject.OBJECT_DEAD_NPC_BODY;
import static com.l2jserver.gameserver.model.skills.targets.AffectObject.UNDEAD_REAL_ENEMY;
import static com.l2jserver.gameserver.model.skills.targets.AffectObject.WYVERN_OBJECT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;

/**
 * Affect Object test.
 * @author Zoey76
 * @version 2.6.3.0
 */
@ExtendWith(MockitoExtension.class)
public class AffectObjectTest {
	
	@Mock
	private L2Character caster;
	@Mock
	private L2Object object;
	@Mock
	private L2Character creature;
	@Mock
	private L2Npc npc;
	
	@Test
	public void test_affect_object_all() {
		assertTrue(ALL.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_clan_player_not_in_clan() {
		when(caster.isPlayable()).thenReturn(true);
		when(caster.getClanId()).thenReturn(0);
		
		assertFalse(CLAN.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_clan_player_in_clan_object_not_playable() {
		when(caster.isPlayable()).thenReturn(true);
		when(caster.getClanId()).thenReturn(1);
		when(object.isPlayable()).thenReturn(false);
		
		assertFalse(CLAN.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_clan_player_in_clan_object_in_other_clan() {
		when(caster.isPlayable()).thenReturn(true);
		when(caster.getClanId()).thenReturn(1);
		when(creature.isPlayable()).thenReturn(true);
		when(creature.getClanId()).thenReturn(2);
		
		assertFalse(CLAN.affectObject(caster, creature));
	}
	
	@Test
	public void test_affect_object_clan_player_in_clan_with_object() {
		when(caster.isPlayable()).thenReturn(true);
		when(caster.getClanId()).thenReturn(1);
		when(creature.isPlayable()).thenReturn(true);
		when(creature.getClanId()).thenReturn(1);
		
		assertTrue(CLAN.affectObject(caster, creature));
	}
	
	@Test
	public void test_affect_object_friend_target_is_autoattackable() {
		when(object.isAutoAttackable(caster)).thenReturn(true);
		
		assertFalse(FRIEND.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_friend_target_is_not_autoattackable() {
		when(object.isAutoAttackable(caster)).thenReturn(false);
		
		assertTrue(FRIEND.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_hidden_place() {
		// TODO(Zoey76): Implement.
		assertFalse(HIDDEN_PLACE.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_invisible_visible_object() {
		when(object.isInvisible()).thenReturn(false);
		
		assertFalse(INVISIBLE.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_invisible_invisible_object() {
		when(object.isInvisible()).thenReturn(true);
		
		assertTrue(INVISIBLE.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_none() {
		assertFalse(NONE.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_not_friend_target_is_not_autoattackable() {
		when(object.isAutoAttackable(caster)).thenReturn(false);
		
		assertFalse(NOT_FRIEND.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_not_friend_target_is_autoattackable() {
		when(object.isAutoAttackable(caster)).thenReturn(true);
		
		assertTrue(NOT_FRIEND.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_object_dead_npc_body_not_npc() {
		when(object.isNpc()).thenReturn(false);
		
		assertFalse(OBJECT_DEAD_NPC_BODY.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_object_dead_npc_body_not_dead() {
		when(npc.isNpc()).thenReturn(true);
		when(npc.isDead()).thenReturn(false);
		
		assertFalse(OBJECT_DEAD_NPC_BODY.affectObject(caster, npc));
	}
	
	@Test
	public void test_affect_object_object_dead_npc_body_dead() {
		when(npc.isNpc()).thenReturn(true);
		when(npc.isDead()).thenReturn(true);
		
		assertTrue(OBJECT_DEAD_NPC_BODY.affectObject(caster, npc));
	}
	
	@Test
	public void test_affect_object_undead_real_enemy_not_npc() {
		when(object.isNpc()).thenReturn(false);
		
		assertFalse(UNDEAD_REAL_ENEMY.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_undead_real_enemy_not_undead() {
		when(npc.isNpc()).thenReturn(true);
		when(npc.isUndead()).thenReturn(false);
		
		assertFalse(UNDEAD_REAL_ENEMY.affectObject(caster, npc));
	}
	
	@Test
	public void test_affect_object_undead_real_enemy_undead() {
		when(npc.isNpc()).thenReturn(true);
		when(npc.isUndead()).thenReturn(true);
		
		assertTrue(UNDEAD_REAL_ENEMY.affectObject(caster, npc));
	}
	
	@Test
	public void test_affect_object_wyvern_object_not_npc() {
		when(object.isNpc()).thenReturn(false);
		
		assertFalse(WYVERN_OBJECT.affectObject(caster, object));
	}
	
	@Test
	public void test_affect_object_wyvern_object_not_wyvern() {
		when(npc.isNpc()).thenReturn(true);
		when(npc.getId()).thenReturn(1);
		
		assertFalse(WYVERN_OBJECT.affectObject(caster, npc));
	}
	
	@Test
	public void test_affect_wyvern_object_wyvern() {
		when(npc.isNpc()).thenReturn(true);
		when(npc.getId()).thenReturn(12621);
		
		assertTrue(WYVERN_OBJECT.affectObject(caster, npc));
	}
}
