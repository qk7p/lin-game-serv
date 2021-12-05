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
package com.l2jserver.gameserver.taskmanager.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExBrExtraUserInfo;
import com.l2jserver.gameserver.network.serverpackets.ExVoteSystemInfo;
import com.l2jserver.gameserver.network.serverpackets.UserInfo;
import com.l2jserver.gameserver.taskmanager.Task;
import com.l2jserver.gameserver.taskmanager.TaskManager;
import com.l2jserver.gameserver.taskmanager.TaskManager.ExecutedTask;
import com.l2jserver.gameserver.taskmanager.TaskTypes;

/**
 * @author Layane
 * @author Maneco2
 */
public class TaskRecom extends Task {
	
	private static final Logger LOG = LoggerFactory.getLogger(TaskRecom.class);
	
	private static final String NAME = "recommendations";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(ExecutedTask task) {
		final String UPDATE_CHARACTERS_RECO = "UPDATE character_reco_bonus SET rec_have=?, rec_left=?, time_left=? WHERE charId=?";
		for (L2PcInstance player : L2World.getInstance().getPlayers()) {
			try (var con = ConnectionFactory.getInstance().getConnection();
				var ps = con.prepareStatement(UPDATE_CHARACTERS_RECO)) {
				ps.setInt(1, player.getRecomHave());
				ps.setInt(2, player.getRecomLeft());
				ps.setInt(3, player.getRecomBonusTime());
				ps.setInt(4, player.getObjectId());
				ps.executeUpdate();
			} catch (Exception e) {
				LOG.warn("{}: Recommendations System not reseted!", getClass().getSimpleName(), e);
			}
			
			if ((player != null)) {
				player.setRecomHave(player.getRecomHave() - 2);
				player.setRecomLeft(0);
				player.setRecomBonusTime(3600);
				if (player.isOnline()) {
					player.sendPacket(new UserInfo(player));
					player.sendPacket(new ExBrExtraUserInfo(player));
					player.sendPacket(new ExVoteSystemInfo(player));
				}
			}
		}
		LOG.info("Recommendations System reseted.");
	}
	
	@Override
	public void initializate() {
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "06:30:00", "");
	}
}
