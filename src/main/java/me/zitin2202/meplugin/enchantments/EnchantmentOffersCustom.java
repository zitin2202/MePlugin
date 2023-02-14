package me.zitin2202.meplugin.enchantments;

import com.google.common.base.Preconditions;
import me.zitin2202.meplugin.MePlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.io.Serializable;

public class EnchantmentOffersCustom implements Serializable  {

    private boolean custom;
    private String enchantment_key;
    private String namespace;
    private int enchantmentLevel;
    private int cost;


    public EnchantmentOffersCustom(boolean custom, String enchantment_key, String namespace_key, int enchantmentLevel, int cost) {
        this.custom = custom;
        this.enchantment_key = enchantment_key;
        this.namespace = namespace_key;
        this.enchantmentLevel = enchantmentLevel;
        this.cost = cost;
    }

    public Enchantment getEnchantment() {
            if (namespace.equals("meplugin")){
                return Enchantment.getByKey(new NamespacedKey(MePlugin.getPlugin(),enchantment_key));
            }
        return Enchantment.getByKey(NamespacedKey.minecraft(enchantment_key));
    }

    public String getEnchantmentkey() {
     return enchantment_key;
    }


    public void setEnchantment_key(String enchantment_key) {
        Preconditions.checkArgument(enchantment_key != null, "The enchantment may not be null!");

        this.enchantment_key = enchantment_key;
    }

    /**
     * Gets the level of the enchantment.
     *
     * @return level of the enchantment
     */
    public int getEnchantmentLevel() {
        return enchantmentLevel;
    }

    /**
     * Sets the level of the enchantment.
     *
     * @param enchantmentLevel level of the enchantment
     */
    public void setEnchantmentLevel(int enchantmentLevel) {
        Preconditions.checkArgument(enchantmentLevel > 0, "The enchantment level must be greater than 0!");

        this.enchantmentLevel = enchantmentLevel;
    }

    /**
     * Gets the cost (minimum level) which is displayed as a number on the right
     * hand side of the enchantment offer.
     *
     * @return cost for this enchantment
     */
    public int getCost() {
        return cost;
    }

    /**
     * Sets the cost (minimum level) which is displayed as a number on the right
     * hand side of the enchantment offer.
     *
     * @param cost cost for this enchantment
     */
    public void setCost(int cost) {
        Preconditions.checkArgument(cost > 0, "The cost must be greater than 0!");

        this.cost = cost;
    }

    public String getNamespacekey() {
        return namespace;
    }


    public boolean isCustom() {
        return custom;
    }


}
