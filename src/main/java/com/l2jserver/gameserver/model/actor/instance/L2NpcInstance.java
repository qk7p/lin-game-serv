/*
 * Copyright © 2004-2024 L2J Server
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
package com.l2jserver.gameserver.model.actor.instance;

import static com.l2jserver.gameserver.config.Configuration.general;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.status.FolkStatus;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.base.AcquireSkillType;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.AcquireSkillList;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.StringUtil;

public class L2NpcInstance extends L2Npc {
	private static final Logger LOG = LoggerFactory.getLogger(L2NpcInstance.class);
	
	public L2NpcInstance(L2NpcTemplate template) {
		super(template);
		setInstanceType(InstanceType.L2NpcInstance);
		setIsInvul(false);
	}
	
	@Override
	public FolkStatus getStatus() {
		return (FolkStatus) super.getStatus();
	}
	
	@Override
	public void initCharStatus() {
		setStatus(new FolkStatus(this));
	}
	
	public List<ClassId> getClassesToTeach() {
		return getTemplate().getTeachInfo();
	}
	
	/**
	 * Displays Skill Tree for a given player, npc and class Id.
	 * @param player the active character.
	 * @param npc the last folk.
	 * @param classId player's active class id.
	 */
	public static void showSkillList(L2PcInstance player, L2Npc npc, ClassId classId) {
		if (general().debug()) {
			LOG.debug("SkillList activated on: {}", npc.getObjectId());
		}
		
		if (!npc.getTemplate().canTeach(classId)) {
			npc.showNoTeachHtml(player);
			return;
		}
		
		if (((L2NpcInstance) npc).getClassesToTeach().isEmpty()) {
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			final String sb = StringUtil.concat("<html><body>I cannot teach you. My class list is empty.<br>Ask admin to fix it. Need add my npcid and classes to skill_learn.sql.<br>NpcId:", String.valueOf(npc.getId()), ", Your classId:", String.valueOf(player.getClassId().getId()), "</body></html>");
			html.setHtml(sb);
			player.sendPacket(html);
			return;
		}
		
		// Normal skills, No LearnedByFS, no AutoGet skills.
		final var skills = SkillTreesData.getInstance().getAvailableSkills(player, classId, false, false);
		final var asl = new AcquireSkillList(AcquireSkillType.CLASS);
		int count = 0;
		player.setLearningClass(classId);
		for (var s : skills) {
			if (SkillData.getInstance().getSkill(s.getSkillId(), s.getSkillLevel()) != null) {
				asl.addSkill(s.getSkillId(), s.getSkillLevel(), s.getSkillLevel(), s.getCalculatedLevelUpSp(player.getClassId(), classId), 0);
				count++;
			}
		}
		
		if (count == 0) {
			final var skillTree = SkillTreesData.getInstance().getCompleteClassSkillTree(classId);
			final int minLevel = SkillTreesData.getInstance().getMinLevelForNewSkill(player, skillTree);
			if (minLevel > 0) {
				final var sm = SystemMessage.getSystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1);
				sm.addInt(minLevel);
				player.sendPacket(sm);
			} else {
				if (player.getClassId().level() == 1) {
					final var sm = SystemMessage.getSystemMessage(SystemMessageId.NO_SKILLS_TO_LEARN_RETURN_AFTER_S1_CLASS_CHANGE);
					sm.addInt(2);
					player.sendPacket(sm);
				} else {
					player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
				}
			}
		} else {
			player.sendPacket(asl);
		}
	}
}
