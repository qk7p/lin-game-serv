/*
 * Copyright Â© 2004-2021 L2J Server
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
package com.l2jserver.gameserver.model.entity;

import static com.l2jserver.gameserver.config.Configuration.hunting;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.AbnormalType;
import com.l2jserver.gameserver.model.skills.AbnormalVisualEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ExNevitAdventEffect;
import com.l2jserver.gameserver.network.serverpackets.ExNevitAdventPointInfoPacket;
import com.l2jserver.gameserver.network.serverpackets.ExNevitAdventTimeChange;

/**
 * @author Maneco2
 * @since 2.6.3.0
 */
public class HuntingSystem {
	
	private boolean _message45;
	private boolean _message50;
	private boolean _message75;
	
	private final L2PcInstance _activeChar;
	
	private ScheduledFuture<?> _huntingBonusTask;
	private ScheduledFuture<?> _nevitBlessingTimeTask;
	
	public HuntingSystem(L2PcInstance player) {
		_activeChar = player;
	}
	
	public void onPlayerLogin() {
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 6);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		// Reset Hunting System
		if ((getActiveChar().getLastAccess() < (cal.getTimeInMillis() / 1000L)) && (System.currentTimeMillis() > cal.getTimeInMillis())) {
			getActiveChar().setHuntingBonusTime(0);
		}
		
		// Send Hunting Bonus UI Packets
		getActiveChar().sendPacket(new ExNevitAdventPointInfoPacket(getActiveChar().getNevitBlessingPoints()));
		getActiveChar().sendPacket(new ExNevitAdventTimeChange(getActiveChar().getHuntingBonusTime(), true));
		
		checkNevitBlessingEffect(getActiveChar().getNevitBlessingTime());
		
		checkSystemMessageSend();
	}
	
	public void onPlayerLogout() {
		stopNevitBlessingEffectTask(true);
		stopHuntingBonusTask(false);
	}
	
	public void addPoints(int val) {
		if (_huntingBonusTask != null) {
			getActiveChar().setNevitBlessingPoints(getActiveChar().getNevitBlessingPoints() + val);
		}
		
		if (getActiveChar().getNevitBlessingPoints() > hunting().getNevitBlessingMaxPoints()) {
			getActiveChar().setNevitBlessingPoints(0);
			checkNevitBlessingEffect(hunting().getNevitBlessingEffetcTime());
		}
		
		checkSystemMessageSend();
	}
	
	public void startHuntingSystemTask() {
		if ((_huntingBonusTask == null) && (getActiveChar().getHuntingBonusTime() < hunting().getHuntingBonusMaxTime() || !hunting().getHuntingBonusLimit())) {
			_huntingBonusTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new HuntingBonusTask(), hunting().getNevitBonusRefreshRate() * 1000L, hunting().getNevitBonusRefreshRate() * 1000L);
			if (hunting().getHuntingBonusLimit()) {
				getActiveChar().sendPacket(new ExNevitAdventTimeChange(getActiveChar().getHuntingBonusTime(), false));
			}
		}
	}
	
	public class HuntingBonusTask implements Runnable {
		@Override
		public void run() {
			getActiveChar().setHuntingBonusTime(getActiveChar().getHuntingBonusTime() + hunting().getNevitBonusRefreshRate());
			if (getActiveChar().getHuntingBonusTime() >= hunting().getHuntingBonusMaxTime() && hunting().getHuntingBonusLimit()) {
				getActiveChar().setHuntingBonusTime(hunting().getHuntingBonusMaxTime());
				stopHuntingBonusTask(true);
				return;
			}
			
			if (hunting().getHuntingBonusLimit()) {
				getActiveChar().sendPacket(new ExNevitAdventTimeChange(getActiveChar().getHuntingBonusTime(), false));
			}
			
			addPoints(hunting().getNevitBonusPointsOnRefresh());
			BuffInfo hourglass = getActiveChar().getEffectList().getBuffInfoByAbnormalType(AbnormalType.VOTE);
			if (hourglass != null) {
				addPoints(hunting().getNevitRegularPoints2());
			} else {
				if (getActiveChar().getHuntingBonusTime() < hunting().getHuntingBonusMaxTime()) {
					addPoints(hunting().getNevitRegularPoints());
				}
			}
		}
	}
	
	public class NevitEffectEnd implements Runnable {
		@Override
		public void run() {
			getActiveChar().setNevitBlessingTime(0);
			getActiveChar().sendPacket(new ExNevitAdventEffect(0));
			getActiveChar().sendPacket(new ExNevitAdventPointInfoPacket(getActiveChar().getNevitBlessingPoints()));
			getActiveChar().sendPacket(SystemMessageId.NEVITS_ADVENT_BLESSING_HAS_ENDED);
			getActiveChar().stopAbnormalVisualEffect(true, AbnormalVisualEffect.NEVIT_ADVENT);
			stopNevitBlessingEffectTask(false);
		}
	}
	
	private void checkNevitBlessingEffect(int value) {
		if (getActiveChar().getNevitBlessingTime() > 0) {
			stopNevitBlessingEffectTask(false);
			value = getActiveChar().getNevitBlessingTime();
		}
		
		if ((getActiveChar().getHuntingBonusTime() < hunting().getHuntingBonusMaxTime() || !hunting().getHuntingBonusLimit()) && (value > 0)) {
			final int percent = calcPercent(getActiveChar().getNevitBlessingPoints());
			if (percent < 45) {
				_message45 = false;
				_message50 = false;
				_message75 = false;
			}
			getActiveChar().setNevitBlessingTime(value);
			getActiveChar().sendPacket(new ExNevitAdventEffect(value));
			getActiveChar().sendPacket(SystemMessageId.THE_ANGEL_NEVIT_HAS_BLESSED_YOU_FROM_ABOVE);
			getActiveChar().startAbnormalVisualEffect(true, AbnormalVisualEffect.NEVIT_ADVENT);
			_nevitBlessingTimeTask = ThreadPoolManager.getInstance().scheduleGeneral(new NevitEffectEnd(), value * 1000L);
		}
	}
	
	public void stopHuntingBonusTask(boolean sendPacket) {
		if (_huntingBonusTask != null) {
			_huntingBonusTask.cancel(true);
			_huntingBonusTask = null;
		}
		
		if (sendPacket) {
			getActiveChar().sendPacket(new ExNevitAdventTimeChange(getActiveChar().getHuntingBonusTime(), true));
		}
	}
	
	public void stopNevitBlessingEffectTask(boolean value) {
		if (_nevitBlessingTimeTask != null) {
			if (value) {
				int time = (int) _nevitBlessingTimeTask.getDelay(TimeUnit.SECONDS);
				if (time > 0) {
					getActiveChar().setNevitBlessingTime(time);
				} else {
					getActiveChar().setNevitBlessingTime(0);
				}
			}
			_nevitBlessingTimeTask.cancel(true);
			_nevitBlessingTimeTask = null;
		}
	}
	
	public void checkSystemMessageSend() {
		final int percent = calcPercent(getActiveChar().getNevitBlessingPoints());
		if (percent >= 75) {
			if (!_message75) {
				_message75 = true;
				getActiveChar().sendPacket(SystemMessageId.NEVITS_ADVENT_BLESSING_SHINES_STRONGLY_FROM_ABOVE);
			}
		} else if (percent >= 50) {
			if (!_message50) {
				_message50 = true;
				getActiveChar().sendPacket(SystemMessageId.YOU_ARE_FURTHER_INFUSED_WITH_THE_BLESSINGS_OF_NEVIT);
			}
		} else if (percent >= 45) {
			if (!_message45) {
				_message45 = true;
				getActiveChar().sendPacket(SystemMessageId.YOU_ARE_STARTING_TO_FEEL_THE_EFFECTS_OF_NEVITS_ADVENT_BLESSING);
			}
		}
	}
	
	public boolean isNevitBlessingActive() {
		return ((_nevitBlessingTimeTask != null) && (_nevitBlessingTimeTask.getDelay(TimeUnit.SECONDS) > 0));
	}
	
	public boolean isHuntingBonusTaskActive() {
		return (_nevitBlessingTimeTask != null);
	}
	
	public static int calcPercent(int points) {
		return (int) ((100.0 / hunting().getNevitBlessingMaxPoints()) * points);
	}
	
	public L2PcInstance getActiveChar() {
		return _activeChar;
	}
}