package com.skyblock.skyblock.features.trades;

import com.skyblock.skyblock.utilities.Util;
import com.skyblock.skyblock.utilities.item.ItemBuilder;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class Trade {

    private final String item;
    private final int amount;

    private final String costType;
    private final String costItem;
    private final int costAmount;

    public ItemStack toItem() {
        ItemStack item = Util.getItem(this.item).clone();

        ItemBuilder builder = new ItemBuilder(item).setAmount(this.amount);

        if (amount > 1) {
            String current = builder.toItemStack().getItemMeta().getDisplayName();

            if (!current.endsWith("x" + amount)) builder.setDisplayName(current + " &8x" + amount);
        }

        builder.addLore(Util.buildLore("\n&7Cost"));
        if (costType.equals("coin")) {
            builder.addLore(Util.buildLore("&6" + Util.formatInt(costAmount) + " Coins"));
        } else {
            ItemBuilder costItemBuilder = new ItemBuilder(Util.getItem(this.costItem).clone());

            if (costAmount > 1) costItemBuilder.setDisplayName(costItemBuilder.toItemStack().getItemMeta().getDisplayName() + " &8x" + costAmount);

            builder.addLore(Util.buildLore(costItemBuilder.toItemStack().getItemMeta().getDisplayName()));
        }

        builder.addLore(Util.buildLore("\n&eClick to trade!"));

        builder.addNBT("trade.is_trade", true);
        builder.addNBT("trade.item", this.item);
        builder.addNBT("trade.amount", this.amount);
        builder.addNBT("trade.cost_type", this.costType);
        builder.addNBT("trade.cost_item", this.costItem == null ? "NONE" : this.costItem);
        builder.addNBT("trade.cost_amount", this.costAmount);

        return builder.toItemStack();
    }

}
