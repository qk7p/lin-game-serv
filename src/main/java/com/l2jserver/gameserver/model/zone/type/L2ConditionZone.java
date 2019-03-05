/*
 * Copyright © 2004-2019 L2J Server
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
package com.l2jserver.gameserver.model.zone.type;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.model.zone.ZoneId;

/**
 * @author UnAfraid
 */
public class L2ConditionZone extends L2ZoneType {
	private boolean NO_ITEM_DROP = false;
	private boolean NO_BOOKMARK = false;
	
	public L2ConditionZone(int id) {
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value) {
		if (name.equalsIgnoreCase("NoBookmark")) {
			NO_BOOKMARK = Boolean.parseBoolean(value);
		} else if (name.equalsIgnoreCase("NoItemDrop")) {
			NO_ITEM_DROP = Boolean.parseBoolean(value);
		} else {
			super.setParameter(name, value);
		}
	}
	
	@Override
	protected void onEnter(L2Character character) {
		if (character.isPlayer()) {
			if (NO_BOOKMARK) {
				character.setInsideZone(ZoneId.NO_BOOKMARK, true);
			}
			if (NO_ITEM_DROP) {
				character.setInsideZone(ZoneId.NO_ITEM_DROP, true);
			}
		}
	}
	
	@Override
	protected void onExit(L2Character character) {
		if (character.isPlayer()) {
			if (NO_BOOKMARK) {
				character.setInsideZone(ZoneId.NO_BOOKMARK, false);
			}
			if (NO_ITEM_DROP) {
				character.setInsideZone(ZoneId.NO_ITEM_DROP, false);
			}
		}
	}
}
