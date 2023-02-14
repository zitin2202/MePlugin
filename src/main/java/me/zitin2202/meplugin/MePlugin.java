package me.zitin2202.meplugin;

import me.zitin2202.meplugin.commands.ChainTHT;
import me.zitin2202.meplugin.commands.Sound.Sound;
import me.zitin2202.meplugin.commands.Sound.SoundCompleter;
import me.zitin2202.meplugin.enchantments.CustomEnchantmentWrapper;
import me.zitin2202.meplugin.enchantments.EnchantItemType;
import me.zitin2202.meplugin.enchantments.EnchantmentEvents;
import me.zitin2202.meplugin.enchantments.list.BlessingOfHephaestus;
import me.zitin2202.meplugin.enchantments.list.HeavenlyWalking.HeavenlyWalking;
import me.zitin2202.meplugin.enchantments.list.HeavenlyWalking.HeavenlyWalkingEvents;
import me.zitin2202.meplugin.enchantments.list.Speed;
import me.zitin2202.meplugin.events.ChainTHTEvent;
import me.zitin2202.meplugin.events.ExpEvent;
import me.zitin2202.meplugin.events.Lottery;
import me.zitin2202.meplugin.events.Memorise;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

public final class MePlugin extends JavaPlugin {
    private static MePlugin plugin;
    public static Enchantment SPEED;
    public static Enchantment BLESSING_OF_HEPHAESTUS;

    public static Enchantment HEAVENLY_WALKING;



    @Override
    public void onEnable() {
        plugin = this;
        EnchantmentEvents.AddListsEnchantment();
        SPEED = new Speed("speed");
        BLESSING_OF_HEPHAESTUS = new BlessingOfHephaestus("Blessing_of_hephaestus");
        HEAVENLY_WALKING = new HeavenlyWalking("heavenly_walking");


        if (Enchantment.getByKey(SPEED.getKey()) == null){
            CustomEnchantmentWrapper.registerEnchantment(SPEED);
        }

        if (Enchantment.getByKey(BLESSING_OF_HEPHAESTUS.getKey()) == null){
            CustomEnchantmentWrapper.registerEnchantment(BLESSING_OF_HEPHAESTUS);
        }
        if (Enchantment.getByKey(HEAVENLY_WALKING.getKey()) == null){
            CustomEnchantmentWrapper.registerEnchantment(HEAVENLY_WALKING);
        }



        EnchantmentEvents.AddItemToList(SPEED, EnchantItemType.EnchantType.FEET);
        EnchantmentEvents.AddItemToList(BLESSING_OF_HEPHAESTUS, EnchantItemType.EnchantType.PICKAXE);
        EnchantmentEvents.AddItemToList(HEAVENLY_WALKING, EnchantItemType.EnchantType.FEET);



        Bukkit.getPluginManager().registerEvents(new ExpEvent(), this);
        Bukkit.getPluginManager().registerEvents(new EnchantmentEvents(), this);
        Bukkit.getPluginManager().registerEvents(new Lottery(), this);
        Bukkit.getPluginManager().registerEvents(new Memorise(), this);
        Bukkit.getPluginManager().registerEvents(new HeavenlyWalkingEvents(), this);
        Bukkit.getPluginManager().registerEvents(new ChainTHTEvent(), this);



        getCommand("sound").setExecutor(new Sound());
        getCommand("sound").setTabCompleter(new SoundCompleter());

        getCommand("chaintnt").setExecutor(new ChainTHT());



    }

    @Override
    public void onDisable() {

        CustomEnchantmentWrapper.CancelRegistrationEnchantment(SPEED);
        CustomEnchantmentWrapper.CancelRegistrationEnchantment(BLESSING_OF_HEPHAESTUS);
        CustomEnchantmentWrapper.CancelRegistrationEnchantment(HEAVENLY_WALKING);

    }

    public static MePlugin getPlugin() {
        return plugin;
    }


}
