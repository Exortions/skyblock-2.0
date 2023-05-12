package com.skyblock.skyblock.events;

import com.skyblock.skyblock.SkyblockPlayer;
import com.skyblock.skyblock.features.skills.Skill;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkyblockPlayerSkillXPChangeEvent extends SkyblockEvent {

    private Skill skill;
    private SkyblockPlayer player;
    private double xp;

}
