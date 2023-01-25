/*
 * Copyright © 2004-2023 L2J Server
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2ChestInstance;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * Target Type test.
 * @author Noé Caratini aka Kita
 */
@ExtendWith(MockitoExtension.class)
public class TargetTypeTest {

    @Mock
    private Skill skill;
    @Mock
    private L2Character caster;
    @Mock
    private L2Object target;

    @Test
    public void doorTreasureShouldReturnNullIfTargetIsNull() {
        L2Object result = TargetType.DOOR_TREASURE.getTarget(skill, caster, null);

        assertThat(result).isNull();
    }

    @Test
    public void doorTreasureShouldReturnNullIfTargetIsNotADoorOrChest() {
        when(target.isDoor()).thenReturn(false);

        L2Object result = TargetType.DOOR_TREASURE.getTarget(skill, caster, target);

        assertThat(result).isNull();
    }

    @Test
    public void doorTreasureShouldReturnTargetIfDoor() {
        when(target.isDoor()).thenReturn(true);

        L2Object result = TargetType.DOOR_TREASURE.getTarget(skill, caster, target);

        assertThat(result).isSameAs(target);
    }

    @Test
    public void doorTreasureShouldReturnTargetIfChest() {
        target = mock(L2ChestInstance.class);

        L2Object result = TargetType.DOOR_TREASURE.getTarget(skill, caster, target);

        assertThat(result).isSameAs(target);
    }
}