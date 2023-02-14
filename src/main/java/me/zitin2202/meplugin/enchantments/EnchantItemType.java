package me.zitin2202.meplugin.enchantments;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.Material.*;
import static org.bukkit.Material.LEATHER_BOOTS;

public class EnchantItemType implements Serializable {

    public enum EnchantType implements Serializable{
        HEAD(NETHERITE_HELMET, DIAMOND_HELMET, GOLDEN_HELMET, IRON_HELMET, CHAINMAIL_HELMET, LEATHER_HELMET),
        CHEST(NETHERITE_CHESTPLATE, DIAMOND_CHESTPLATE, GOLDEN_CHESTPLATE, IRON_CHESTPLATE, CHAINMAIL_CHESTPLATE, LEATHER_CHESTPLATE),
        LEGS(NETHERITE_LEGGINGS, DIAMOND_LEGGINGS, GOLDEN_LEGGINGS, IRON_LEGGINGS, CHAINMAIL_LEGGINGS, LEATHER_LEGGINGS),
        FEET(NETHERITE_BOOTS, DIAMOND_BOOTS, GOLDEN_BOOTS, IRON_BOOTS, CHAINMAIL_BOOTS, LEATHER_BOOTS),
        ANY(BOOK),
        PICKAXE(WOODEN_PICKAXE,STONE_PICKAXE,GOLDEN_PICKAXE,IRON_PICKAXE,DIAMOND_PICKAXE,NETHERITE_PICKAXE);

        private final Set<Material> mutableTypes = new HashSet<>();
        private Set<Material> immutableTypes;

        EnchantType(Material ...types) {
            this.mutableTypes.addAll(Arrays.asList(types));

        }


        public Set<Material> getTypes() {
            if (immutableTypes == null) {
                immutableTypes = Collections.unmodifiableSet(mutableTypes);
            }
            return immutableTypes;
        }

        static public EnchantItemType.EnchantType CheckType(ItemStack item){
            for (EnchantItemType.EnchantType type: EnchantItemType.EnchantType.values()){
                if (type.getTypes().contains(item.getType())){
                    return type;
                }
            }

            return  null;
        }



    }

}
