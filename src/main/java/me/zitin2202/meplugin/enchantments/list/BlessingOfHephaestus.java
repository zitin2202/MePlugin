package me.zitin2202.meplugin.enchantments.list;

import me.zitin2202.meplugin.enchantments.CustomEnchantmentWrapper;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlessingOfHephaestus extends CustomEnchantmentWrapper {


    public BlessingOfHephaestus(String key) {
        super(key);
    }

    @NotNull
    @Override
    public String getName() {
        return "Благословение Гефеста";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
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
    public boolean conflictsWith(@NotNull Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return true;
    }
}
