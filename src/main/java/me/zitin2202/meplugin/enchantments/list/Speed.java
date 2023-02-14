package me.zitin2202.meplugin.enchantments.list;

import me.zitin2202.meplugin.enchantments.CustomEnchantmentWrapper;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class Speed extends CustomEnchantmentWrapper {

    public Speed(String key) {
        super(key);
    }

    @Override
    public String getName() {
        return "Скорость";
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR_FEET;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        if (enchantment == enchantment.SOUL_SPEED){
            return true;
        }
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return true;
    }
}
