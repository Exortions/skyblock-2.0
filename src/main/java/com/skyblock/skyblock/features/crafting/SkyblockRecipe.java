package com.skyblock.skyblock.features.crafting;

import com.skyblock.skyblock.features.collections.Collection;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.json.simple.JSONObject;

import java.util.*;

@Getter
public class SkyblockRecipe {

    private boolean customItems = false;
    private List<String> items;
    private ItemStack result;

    public SkyblockRecipe(ShapedRecipe shapedRecipe) {
        items = new ArrayList<>();
        result = shapedRecipe.getResult();

        Map<Character, ItemStack> map = shapedRecipe.getIngredientMap();

        String[] spots = new String[]{"abc", "def", "ghi"};

        for (int i = 0; i < spots.length; i++) {
            String s = spots[i];
            for (int j = 0; j < s.length(); j++) {
                StringBuilder line = new StringBuilder();
                char c = s.charAt(j);
                ItemStack item = map.get(c);

                if (item != null) {
                    line.append(item.getType().name()).append(":").append(item.getAmount()).append(":").append(item.getDurability());

                    if (line.toString().contains("32767")){
                        line = new StringBuilder(line.toString().replaceAll("32767", "0"));
                    }
                } else {
                    line.append("AIR");
                }
                items.add(line.toString());
            }
        }
        if (shapedRecipe.getResult().getType().equals(Material.WORKBENCH)) Bukkit.getConsoleSender().sendMessage(shapedRecipe.getIngredientMap() + "");
    }

    public SkyblockRecipe(ShapelessRecipe recipe) {
        items = new ArrayList<>();
        result = recipe.getResult();

        for (ItemStack item : recipe.getIngredientList()) {
            items.add(item.getType().name() + ":" + item.getAmount() + ":" + item.getDurability());
        }
        if (recipe.getResult().getType().equals(Material.WORKBENCH)) Bukkit.getConsoleSender().sendMessage(items + "");
    }

    public SkyblockRecipe(JSONObject neuRecipe, ItemStack result) {
        customItems = true;
        items = new ArrayList<>();
        this.result = result;

        for (int i = 1; i < 4; i++) {
            String a = neuRecipe.get("A" + i) + ":0";
            if (a.equals(":0")) a = "AIR";
            items.add(a);
        }

        for (int i = 1; i < 4; i++) {
            String b = neuRecipe.get("B" + i) + ":0";
            if (b.equals(":0")) b = "AIR";
            items.add(b);
        }

        for (int i = 1; i < 4; i++) {
            String c = neuRecipe.get("C" + i) + ":0";
            if (c.equals(":0")) c = "AIR";
            items.add(c);
        }
    }

    public List<Integer> getExcess(String s) {
        String[] sArr = s.substring(1, s.length() - 1).split(", ");
        String[] sArrClone = Arrays.copyOf(sArr, sArr.length + 1);
        sArrClone[sArrClone.length - 1] = "";
        List<String> list = Arrays.asList(sArrClone);

        List<Integer> ret = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            try {
                int amount = Integer.parseInt(list.get(i).split(":")[1]);
                int required = Integer.parseInt(items.get(i).split(":")[1]);

                if (amount - required < 0) return Collections.emptyList();

                ret.add(amount - required);
            } catch (ArrayIndexOutOfBoundsException e) {
                ret.add(0);
            }
        }

        return ret;
    }

    public String toString(boolean ignoreAmount) {
        StringBuilder string = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            String s = items.get(i);

            if (ignoreAmount) {
                try {
                    int amount = Integer.parseInt(items.get(i).split(":")[1]);
                    s = s.replaceFirst(":" + amount, "");
                } catch (ArrayIndexOutOfBoundsException e) { }
            }

            string.append(s);

            if (i != items.size() - 1) {
                string.append(", ");
            }
        }

        string.append("]");

        return string.toString();
    }

    public String toShapeless() {
        return items.toString();
    }
}
