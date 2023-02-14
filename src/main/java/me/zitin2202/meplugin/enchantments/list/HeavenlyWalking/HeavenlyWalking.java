package me.zitin2202.meplugin.enchantments.list.HeavenlyWalking;

import me.zitin2202.meplugin.enchantments.CustomEnchantmentWrapper;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class HeavenlyWalking extends CustomEnchantmentWrapper{

    public HeavenlyWalking(String key) {
        super(key);
    }

    @Override
    public String getName() {
        return "Небесная ходьба";
    }

    @Override
    public int getMaxLevel() {
        return 7;
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
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return true;
    }



}

