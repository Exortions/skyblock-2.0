package com.skyblock.skyblock.enums;

import com.skyblock.skyblock.Skyblock;
import com.skyblock.skyblock.features.reforge.ReforgeStat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public enum Reforge {

    NONE,
    GENTLE,
    ODD,
    FAST,
    FAIR,
    EPIC,
    SHARP,
    HEROIC,
    SPICY,
    LEGENDARY,
    DIRTY,
    FABLED,
    SUSPICIOUS,
    GILDED,
    WARPED,
    WITHERED,
    SALTY,
    TREACHEROUS,
    DEADLY,
    FINE,
    GRAND,
    HASTY,
    NEAT,
    RAPID,
    UNREAL,
    AWKWARD,
    RICH,
    PRECISE,
    SPIRITUAL,
    HEADSTRONG,
    CLEAN,
    FIERCE,
    HEAVY,
    LIGHT,
    MYTHIC,
    PURE,
    SMART,
    TITANIC,
    WISE,
    PERFECT,
    NECROTIC,
    ANCIENT,
    SPIKED,
    RENOWNED,
    CUBIC,
    RIENFORCED,
    LOVING,
    RIDICULOUS,
    GIANT,
    SUBMERGED,
    BIZARRE,
    ITCHY,
    OMINOUS,
    PLEASANT,
    PRETTY,
    SHINY,
    SIMPLE,
    STRANGE,
    VIVID,
    GODLY,
    DEMONIC,
    FORCEFUL,
    HURTFUL,
    KEEN,
    STRONG,
    SUPERIOR,
    UNPLEASANT,
    ZEALOUS,
    SILKY,
    BLOODY,
    SHADED,
    SWEET,
    FRUITFUL,
    MAGNETIC,
    REFINED,
    BLESSED,
    MOIL,
    TOIL,
    FLEET,
    STELLAR,
    MITHRAIC,
    AUSPICIOUS;

    public static Reforge getReforge(String reforgeType)  {
        return valueOf(ChatColor.stripColor(reforgeType).toUpperCase().replace(" ", "_"));
    }

    public ReforgeStat getReforgeData(Rarity rarity) {
        return Skyblock.getPlugin(Skyblock.class).getReforgeHandler().getReforge(this).getStats().get(rarity);
    }

}
