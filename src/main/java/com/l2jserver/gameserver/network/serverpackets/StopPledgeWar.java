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
package com.l2jserver.gameserver.network.serverpackets;

public final class StopPledgeWar extends L2GameServerPacket {
	private final String _pledgeName;
	private final String _playerName;
	
	public StopPledgeWar(String pledge, String charName) {
		_pledgeName = pledge;
		_playerName = charName;
	}
	
	@Override
	protected void writeImpl() {
		writeC(0x65);
		writeS(_pledgeName);
		writeS(_playerName);
	}
}