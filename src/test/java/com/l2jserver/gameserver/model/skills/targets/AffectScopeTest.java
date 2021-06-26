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

import static com.l2jserver.gameserver.model.skills.targets.AffectScope.BALAKAS_SCOPE;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.DEAD_PLEDGE;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.FAN;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.NONE;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.PARTY;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.PARTY_PLEDGE;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.PLEDGE;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.POINT_BLANK;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.RANGE;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.RANGE_SORT_BY_HP;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.RING_RANGE;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.SINGLE;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.SQUARE;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.SQUARE_PB;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.STATIC_OBJECT_SCOPE;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.WYVERN_SCOPE;
import static com.l2jserver.gameserver.model.zone.ZoneId.SIEGE;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.powermock.api.easymock.annotation.Mock;
import org.powermock.api.easymock.annotation.MockNice;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.Test;

import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.L2ClanMember;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2ServitorInstance;
import com.l2jserver.gameserver.model.actor.knownlist.NpcKnownList;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.test.AbstractTest;
import com.l2jserver.gameserver.util.Util;

/**
 * Affect Scope test.
 * @author Zoey76
 * @version 2.6.2.0
 */
@PrepareForTest({
	L2NpcTemplate.class,
	L2World.class,
	Util.class
})
public class AffectScopeTest extends AbstractTest {
	
	private static final int AFFECT_LIMIT = 5;
	
	private static final int AFFECT_RANGE = 1000;
	
	@Mock
	private L2Character caster;
	@Mock
	private L2Character target;
	@MockNice
	private Skill skill;
	@Mock
	private L2World world;
	@Mock
	private L2Object object1;
	@Mock
	private L2PcInstance player1;
	@Mock
	private L2PcInstance player2;
	@Mock
	private L2PcInstance player3;
	@Mock
	private L2PcInstance player4;
	@Mock
	private L2PcInstance player5;
	@Mock
	private L2PcInstance player6;
	@Mock
	private L2PcInstance player7;
	@Mock
	private L2PcInstance player8;
	@Mock
	private L2Summon summon;
	@Mock
	private L2ServitorInstance servitor;
	@Mock
	private AffectObject affectObject;
	@Mock
	private L2Party party1;
	@Mock
	private L2Party party2;
	@Mock
	private L2Clan clan;
	@Mock
	private L2ClanMember clanMember1;
	@Mock
	private L2ClanMember clanMember2;
	@Mock
	private L2ClanMember clanMember3;
	@Mock
	private L2ClanMember clanMember4;
	@Mock
	private L2Npc npc1;
	@Mock
	private L2Npc npc2;
	@Mock
	private L2Npc npc3;
	@Mock
	private L2NpcTemplate npcTemplate;
	@Mock
	private NpcKnownList npcKnownList;
	
	@Test
	public void test_balakas_scope() {
		assertEquals(BALAKAS_SCOPE.affectTargets(caster, target, skill), List.of());
	}
	
	@Test
	public void test_dead_pledge_scope_caster_not_playable() {
		expect(target.isPlayable()).andReturn(false);
		replayAll();
		
		assertEquals(DEAD_PLEDGE.affectTargets(caster, target, skill), List.of());
	}
	
	@Test
	public void test_dead_pledge_scope_player_not_in_clan() {
		expect(target.isPlayable()).andReturn(true);
		expect(target.getActingPlayer()).andReturn(player1);
		expect(player1.getClanId()).andReturn(0);
		replayAll();
		
		assertEquals(DEAD_PLEDGE.affectTargets(caster, target, skill), List.of());
	}
	
	@Test
	public void test_dead_pledge_scope_player() {
		expect(target.isPlayable()).andReturn(true);
		expect(target.getActingPlayer()).andReturn(player1);
		expect(player1.getClanId()).andReturn(1);
		expect(player1.isInDuel()).andReturn(false);
		expect(skill.getAffectLimit()).andReturn(AFFECT_LIMIT);
		expect(skill.getAffectRange()).andReturn(AFFECT_RANGE);
		expect(skill.getAffectObject()).andReturn(affectObject);
		mockStatic(L2World.class);
		expect(L2World.getInstance()).andReturn(world);
		expect(world.getVisibleObjects(target, AFFECT_RANGE)).andReturn(List.of(object1, servitor, player2, player3, player4, player5, player6, player7, player8));
		expect(object1.isPlayable()).andReturn(false);
		expect(servitor.isPlayable()).andReturn(true);
		expect(servitor.getActingPlayer()).andReturn(null);
		expect(player2.isPlayable()).andReturn(true);
		expect(player2.getActingPlayer()).andReturn(player2);
		expect(player2.getClanId()).andReturn(0);
		expect(player3.isPlayable()).andReturn(true);
		expect(player3.getActingPlayer()).andReturn(player3);
		expect(player3.getClanId()).andReturn(1);
		expect(player1.checkPvpSkill(player3, skill)).andReturn(true);
		expect(player1.isInOlympiadMode()).andReturn(false);
		expect(player3.isInsideZone(SIEGE)).andReturn(false);
		expect(affectObject.affectObject(player1, player3)).andReturn(false);
		
		expect(player4.isPlayable()).andReturn(true);
		expect(player4.getActingPlayer()).andReturn(player4);
		expect(player4.getClanId()).andReturn(1);
		expect(player1.checkPvpSkill(player4, skill)).andReturn(true);
		expect(player1.isInOlympiadMode()).andReturn(false);
		expect(player4.isInsideZone(SIEGE)).andReturn(false);
		expect(affectObject.affectObject(player1, player4)).andReturn(true);
		
		expect(player5.isPlayable()).andReturn(true);
		expect(player5.getActingPlayer()).andReturn(player5);
		expect(player5.getClanId()).andReturn(1);
		expect(player1.checkPvpSkill(player5, skill)).andReturn(true);
		expect(player1.isInOlympiadMode()).andReturn(false);
		expect(player5.isInsideZone(SIEGE)).andReturn(false);
		expect(affectObject.affectObject(player1, player5)).andReturn(true);
		
		expect(player6.isPlayable()).andReturn(true);
		expect(player6.getActingPlayer()).andReturn(player6);
		expect(player6.getClanId()).andReturn(1);
		expect(player1.checkPvpSkill(player6, skill)).andReturn(true);
		expect(player1.isInOlympiadMode()).andReturn(false);
		expect(player6.isInsideZone(SIEGE)).andReturn(false);
		expect(affectObject.affectObject(player1, player6)).andReturn(true);
		
		expect(player7.isPlayable()).andReturn(true);
		expect(player7.getActingPlayer()).andReturn(player7);
		expect(player7.getClanId()).andReturn(1);
		expect(player1.checkPvpSkill(player7, skill)).andReturn(true);
		expect(player1.isInOlympiadMode()).andReturn(false);
		expect(player7.isInsideZone(SIEGE)).andReturn(false);
		expect(affectObject.affectObject(player1, player7)).andReturn(true);
		
		expect(player8.isPlayable()).andReturn(true);
		expect(player8.getActingPlayer()).andReturn(player8);
		expect(player8.getClanId()).andReturn(1);
		expect(player1.checkPvpSkill(player8, skill)).andReturn(true);
		expect(player1.isInOlympiadMode()).andReturn(false);
		expect(player8.isInsideZone(SIEGE)).andReturn(false);
		expect(affectObject.affectObject(player1, player8)).andReturn(true);
		replayAll();
		
		assertEquals(DEAD_PLEDGE.affectTargets(caster, target, skill), List.of(player4, player5, player6, player7, player8));
	}
	
	@Test
	public void test_fan_scope() {
		assertEquals(FAN.affectTargets(caster, target, skill), List.of());
	}
	
	@Test
	public void test_none_scope() {
		assertEquals(NONE.affectTargets(caster, target, skill), List.of());
	}
	
	@Test
	public void test_party_scope_target_in_party() {
		expect(skill.getAffectRange()).andReturn(AFFECT_RANGE);
		expect(target.isCharacter()).andReturn(true);
		expect(target.isInParty()).andReturn(true);
		expect(target.getParty()).andReturn(party1);
		expect(party1.getMembers()).andReturn(List.of(player1, player2, player3, player4));
		
		mockStatic(Util.class);
		
		// Party member with summon, both close enough.
		expect(Util.checkIfInRange(AFFECT_RANGE, target, player1, true)).andReturn(true);
		expect(player1.hasSummon()).andReturn(true);
		expect(player1.getSummon()).andReturn(summon);
		expect(Util.checkIfInRange(AFFECT_RANGE, target, summon, true)).andReturn(true);
		
		// Party member close enough without summon.
		expect(Util.checkIfInRange(AFFECT_RANGE, target, player2, true)).andReturn(true);
		expect(player2.hasSummon()).andReturn(false);
		
		// Party member's summon not close enough to target.
		expect(Util.checkIfInRange(AFFECT_RANGE, target, player3, true)).andReturn(true);
		expect(player3.hasSummon()).andReturn(true);
		expect(player3.getSummon()).andReturn(servitor);
		expect(Util.checkIfInRange(AFFECT_RANGE, target, servitor, true)).andReturn(false);
		
		// Party member not close enough to target.
		expect(Util.checkIfInRange(AFFECT_RANGE, target, player4, true)).andReturn(false);
		
		replayAll();
		
		assertEquals(PARTY.affectTargets(caster, target, skill), List.of(player1, summon, player2, player3));
	}
	
	@Test
	public void test_party_scope_target_without_summon_not_in_party() {
		expect(target.isCharacter()).andReturn(true);
		expect(skill.getAffectRange()).andReturn(AFFECT_RANGE);
		expect(target.isInParty()).andReturn(false);
		expect(target.getActingPlayer()).andReturn(player1);
		expect(player1.hasSummon()).andReturn(false);
		
		replayAll();
		
		assertEquals(PARTY.affectTargets(caster, target, skill), List.of(player1));
	}
	
	@Test
	public void test_party_scope_target_with_summon_not_in_party() {
		expect(target.isCharacter()).andReturn(true);
		expect(skill.getAffectRange()).andReturn(AFFECT_RANGE);
		expect(target.isInParty()).andReturn(false);
		expect(target.getActingPlayer()).andReturn(player1);
		expect(player1.hasSummon()).andReturn(true);
		expect(player1.getSummon()).andReturn(summon);
		mockStatic(Util.class);
		expect(Util.checkIfInRange(AFFECT_RANGE, target, summon, true)).andReturn(true);
		
		replayAll();
		
		assertEquals(PARTY.affectTargets(caster, target, skill), List.of(player1, summon));
	}
	
	@Test
	public void test_party_scope_target_with_summon_too_far_not_in_party() {
		expect(target.isCharacter()).andReturn(true);
		expect(skill.getAffectRange()).andReturn(AFFECT_RANGE);
		expect(target.isInParty()).andReturn(false);
		expect(target.getActingPlayer()).andReturn(player1);
		expect(player1.hasSummon()).andReturn(true);
		expect(player1.getSummon()).andReturn(summon);
		mockStatic(Util.class);
		expect(Util.checkIfInRange(AFFECT_RANGE, target, summon, true)).andReturn(false);
		
		replayAll();
		
		assertEquals(PARTY.affectTargets(caster, target, skill), List.of(player1));
	}
	
	@Test(enabled = false)
	public void test_party_pledge_scope() {
		assertEquals(PARTY_PLEDGE.affectTargets(caster, target, skill), List.of(target, summon, player2));
	}
	
	@Test
	public void test_pledge_scope_caster_is_player_in_clan() {
		expect(skill.getAffectRange()).andReturn(AFFECT_RANGE);
		expect(skill.getAffectLimit()).andReturn(AFFECT_LIMIT);
		expect(target.isPlayer()).andReturn(true);
		expect(target.getActingPlayer()).andReturn(player1);
		expect(player1.getClan()).andReturn(clan);
		expect(clan.getMembers()).andReturn(new L2ClanMember[] {
			clanMember1, //
			clanMember2, //
			clanMember3, //
			clanMember4
		});
		
		// Player, not in duel nor Olympiad, with summon.
		expect(clanMember1.getPlayerInstance()).andReturn(player1);
		expect(player1.isInDuel()).andReturn(false);
		expect(player1.checkPvpSkill(player1, skill)).andReturn(true);
		expect(player1.isInOlympiadMode()).andReturn(false);
		mockStatic(Util.class);
		expect(Util.checkIfInRange(AFFECT_RANGE, player1, player1, true)).andReturn(true);
		expect(player1.hasSummon()).andReturn(true);
		expect(player1.getSummon()).andReturn(summon);
		expect(Util.checkIfInRange(AFFECT_RANGE, player1, summon, true)).andReturn(true);
		
		// Player in duel party, but different than target.
		expect(clanMember2.getPlayerInstance()).andReturn(player2);
		expect(player1.isInDuel()).andReturn(true);
		expect(player1.getDuelId()).andReturn(1);
		expect(player2.getDuelId()).andReturn(1);
		expect(player1.isInParty()).andReturn(true);
		expect(player2.isInParty()).andReturn(true);
		expect(player1.getParty()).andReturn(party1);
		expect(party1.getLeaderObjectId()).andReturn(1000);
		expect(player2.getParty()).andReturn(party2);
		expect(party2.getLeaderObjectId()).andReturn(2000);
		
		expect(clanMember3.getPlayerInstance()).andReturn(null);
		
		expect(clanMember4.getPlayerInstance()).andReturn(player4);
		expect(player1.isInDuel()).andReturn(false);
		expect(player1.checkPvpSkill(player4, skill)).andReturn(true);
		expect(player1.isInOlympiadMode()).andReturn(true);
		expect(player1.getOlympiadGameId()).andReturn(1);
		expect(player4.getOlympiadGameId()).andReturn(1);
		expect(player1.getOlympiadSide()).andReturn(1);
		expect(player4.getOlympiadSide()).andReturn(2);
		
		replayAll();
		assertEquals(PLEDGE.affectTargets(caster, target, skill), List.of(player1, summon));
	}
	
	@Test
	public void test_pledge_scope_caster_is_player_not_in_clan() {
		expect(skill.getAffectRange()).andReturn(AFFECT_RANGE);
		expect(skill.getAffectLimit()).andReturn(AFFECT_LIMIT);
		expect(target.isPlayer()).andReturn(true);
		expect(target.getActingPlayer()).andReturn(player1);
		expect(player1.getClan()).andReturn(null);
		
		mockStatic(Util.class);
		expect(Util.checkIfInRange(AFFECT_RANGE, player1, player1, true)).andReturn(true);
		expect(player1.hasSummon()).andReturn(true);
		expect(player1.getSummon()).andReturn(summon);
		expect(Util.checkIfInRange(AFFECT_RANGE, player1, summon, true)).andReturn(true);
		
		replayAll();
		assertEquals(PLEDGE.affectTargets(caster, target, skill), List.of(player1, summon));
	}
	
	@Test
	public void test_pledge_scope_caster_is_npc_in_clan() {
		expect(skill.getAffectRange()).andReturn(AFFECT_RANGE);
		expect(skill.getAffectLimit()).andReturn(AFFECT_LIMIT);
		expect(npc1.isPlayer()).andReturn(false);
		expect(npc1.isNpc()).andReturn(true);
		expect(npc1.getTemplate()).andReturn(npcTemplate);
		expect(npcTemplate.getClans()).andReturn(Set.of(1, 2));
		expect(npc1.getKnownList()).andReturn(npcKnownList);
		expect(npcKnownList.getKnownCharactersInRadius(AFFECT_RANGE)).andReturn(List.of(player2, npc2, npc3, summon));
		
		expect(player2.isNpc()).andReturn(false);
		
		expect(npc2.isNpc()).andReturn(true);
		expect(npc1.isInMyClan(npc2)).andReturn(true);
		
		expect(npc3.isNpc()).andReturn(true);
		expect(npc1.isInMyClan(npc3)).andReturn(false);
		
		expect(summon.isNpc()).andReturn(false);
		
		replayAll();
		assertEquals(PLEDGE.affectTargets(npc1, npc1, skill), List.of(npc1, npc2));
	}
	
	@Test
	public void test_point_blank_scope() {
		expect(caster.isCharacter()).andReturn(true);
		expect(skill.getAffectLimit()).andReturn(AFFECT_LIMIT);
		expect(skill.getAffectObject()).andReturn(affectObject);
		expect(skill.getAffectRange()).andReturn(AFFECT_RANGE);
		
		mockStatic(L2World.class);
		expect(L2World.getInstance()).andReturn(world);
		expect(world.getVisibleObjects(caster, AFFECT_RANGE)) //
			.andReturn(List.of(caster, npc2, npc3, summon));
		
		expect(affectObject.affectObject(caster, caster)).andReturn(true);
		expect(affectObject.affectObject(caster, npc2)).andReturn(false);
		expect(affectObject.affectObject(caster, npc3)).andReturn(false);
		expect(affectObject.affectObject(caster, summon)).andReturn(true);
		
		replayAll();
		
		assertEquals(POINT_BLANK.affectTargets(caster, caster, skill), List.of(caster, summon));
	}
	
	@Test
	public void test_range_scope() {
		expect(skill.getAffectLimit()).andReturn(AFFECT_LIMIT);
		expect(skill.getAffectRange()).andReturn(AFFECT_RANGE);
		
		mockStatic(L2World.class);
		expect(L2World.getInstance()).andReturn(world);
		expect(world.getVisibleObjects(target, AFFECT_RANGE)).andReturn(List.of(object1, servitor, player2, player3, player4, player5, player6, player7, player8));
		
		expect(object1.isCharacter()).andReturn(false);
		expect(servitor.isCharacter()).andReturn(true);
		expect(servitor.isDead()).andReturn(true);
		
		expect(player2.isCharacter()).andReturn(true);
		expect(player2.isDead()).andReturn(false);
		
		expect(player3.isCharacter()).andReturn(true);
		expect(player3.isDead()).andReturn(false);
		
		expect(player4.isCharacter()).andReturn(true);
		expect(player4.isDead()).andReturn(false);
		
		expect(player5.isCharacter()).andReturn(true);
		expect(player5.isDead()).andReturn(false);
		
		expect(player6.isCharacter()).andReturn(true);
		expect(player6.isDead()).andReturn(false);
		
		expect(player7.isCharacter()).andReturn(true);
		expect(player7.isDead()).andReturn(false);
		
		replayAll();
		
		assertEquals(RANGE.affectTargets(caster, target, skill), List.of(player2, player3, player4, player5, player6));
	}
	
	@Test
	public void test_range_sort_by_hp_scope() {
		expect(skill.getAffectLimit()).andReturn(AFFECT_LIMIT);
		expect(skill.getAffectRange()).andReturn(AFFECT_RANGE);
		mockStatic(L2World.class);
		expect(L2World.getInstance()).andReturn(world);
		expect(world.getVisibleObjects(caster, target, AFFECT_RANGE)).andReturn(List.of(target, object1, servitor, player2, player3));
		
		expect(object1.isCharacter()).andReturn(false);
		
		expect(servitor.isCharacter()).andReturn(true);
		expect(servitor.isDead()).andReturn(true);
		
		expect(player2.isCharacter()).andReturn(true);
		expect(player2.isDead()).andReturn(false);
		expect(player2.getCurrentHp()).andReturn(1000.0).times(3);
		expect(player2.getMaxHp()).andReturn(1000).times(3);
		
		expect(player3.isCharacter()).andReturn(true);
		expect(player3.isDead()).andReturn(false);
		expect(player3.getCurrentHp()).andReturn(1900.0).times(3);
		expect(player3.getMaxHp()).andReturn(2000).times(3);
		
		expect(target.isCharacter()).andReturn(true);
		expect(target.isDead()).andReturn(false);
		expect(target.getCurrentHp()).andReturn(500.0).times(3);
		expect(target.getMaxHp()).andReturn(1000).times(3);
		
		replayAll();
		
		assertEquals(RANGE_SORT_BY_HP.affectTargets(caster, target, skill), List.of(target, player3, player2));
	}
	
	@Test
	public void test_ring_range_scope() {
		assertEquals(RING_RANGE.affectTargets(caster, target, skill), List.of());
	}
	
	@Test
	public void test_single_scope_object_no_affected() {
		expect(skill.getAffectObject()).andReturn(affectObject);
		expect(affectObject.affectObject(caster, target)).andReturn(false);
		replayAll();
		
		assertEquals(SINGLE.affectTargets(caster, target, skill), List.of());
	}
	
	@Test
	public void test_single_scope() {
		expect(skill.getAffectObject()).andReturn(affectObject);
		expect(affectObject.affectObject(caster, target)).andReturn(true);
		replayAll();
		
		assertEquals(SINGLE.affectTargets(caster, target, skill), List.of(target));
	}
	
	@Test
	public void test_square_scope() {
		assertEquals(SQUARE.affectTargets(caster, target, skill), List.of());
	}
	
	@Test
	public void test_square_pb_scope() {
		assertEquals(SQUARE_PB.affectTargets(caster, target, skill), List.of());
	}
	
	@Test
	public void test_static_object_scope() {
		assertEquals(STATIC_OBJECT_SCOPE.affectTargets(caster, target, skill), List.of());
	}
	
	@Test
	public void test_wyvern_scope() {
		assertEquals(WYVERN_SCOPE.affectTargets(caster, target, skill), List.of());
	}
}
