package me.zitin2202.meplugin.enchantments;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.zitin2202.meplugin.Converts;
import me.zitin2202.meplugin.MePlugin;
import me.zitin2202.meplugin.Data;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static java.lang.Math.max;

public class EnchantmentEvents implements Listener {

    Random rnd = new Random();
    public static Hashtable<EnchantItemType.EnchantType, List<Enchantment>> enchantment_list = new Hashtable<>();
    public static String[] LevelString = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    ;


    public static void AddListsEnchantment() {
        enchantment_list.put(EnchantItemType.EnchantType.FEET, new ArrayList<>());
        enchantment_list.put(EnchantItemType.EnchantType.CHEST, new ArrayList<>());
        enchantment_list.put(EnchantItemType.EnchantType.HEAD, new ArrayList<>());
        enchantment_list.put(EnchantItemType.EnchantType.LEGS, new ArrayList<>());
        enchantment_list.put(EnchantItemType.EnchantType.PICKAXE, new ArrayList<>());
        enchantment_list.put(EnchantItemType.EnchantType.ANY, new ArrayList<>());


    }

    public static void AddItemToList(Enchantment enchant, EnchantItemType.EnchantType type) {
        enchantment_list.get(type).add(enchant);
        enchantment_list.get(EnchantItemType.EnchantType.ANY).add(enchant);

    }






    @EventHandler
    public void addCustomEnchantToTable(PrepareItemEnchantEvent event) throws Exception {
        System.out.println("PrepareItemEnchantEvent");
        ItemStack item = event.getItem();
        EnchantItemType.EnchantType type_equipment = EnchantItemType.EnchantType.CheckType(item);

        if (type_equipment != null && enchantment_list.containsKey(type_equipment)) {
            Player player = event.getEnchanter();
            PersistentDataContainer container = player.getPersistentDataContainer();
            NamespacedKey key = NamespacedKey.fromString("items_enchants");

            if (!(container.has(key, PersistentDataType.BYTE_ARRAY))) {

                container.set(key, PersistentDataType.BYTE_ARRAY, Converts.ConvertToByteArray(new Hashtable<EnchantItemType.EnchantType, EnchantmentOffersCustom[]>()));

            }
//            container.set(key,PersistentDataType.BYTE_ARRAY,Converts.ConvertToByteArray(new Hashtable<EnchantItemType.EnchantType, EnchantmentOffersCustom[]>()));

            Hashtable<EnchantItemType.EnchantType, EnchantmentOffersCustom[]> items_enchants = (Hashtable<EnchantItemType.EnchantType, EnchantmentOffersCustom[]>) Converts.ConvertToObject(container.get(key, PersistentDataType.BYTE_ARRAY));

            System.out.println("Hashtable ");

            if (!(items_enchants.containsKey(type_equipment))) {
                System.out.println("not exist");


                List<Enchantment> list = enchantment_list.get(type_equipment);

                ListIterator<Enchantment> itter = list.listIterator();
                int chance = (type_equipment == EnchantItemType.EnchantType.ANY ? 5 : 10);


                EnchantmentOffersCustom[] offers_custom = new EnchantmentOffersCustom[3];

                while (itter.hasNext()) {
                    Enchantment enchantment = itter.next();

                    System.out.println(chance + "");
                    if (chance >= rnd.nextInt(1, 101)) {
                        int offer_index = rnd.nextInt(3);
                        System.out.println("offer_index " + offer_index);
                        EnchantmentOffer offer = event.getOffers()[offer_index];
                        int level_cost = offer.getCost();
                        int level_ench = (int)Math.ceil((offer_index+1) / (3.0/ (enchantment.getMaxLevel()<4 ? enchantment.getMaxLevel() : 4) ));
                        if (rnd.nextInt(0,4)>0){ //добовляю фактор случаности при зачаровании
                            if (level_ench-1>0){
                                level_ench-=1;
                            }
                        }
                        System.out.println(level_ench);

                        offers_custom[offer_index] = new EnchantmentOffersCustom(enchantment.getKey().getNamespace().equals("meplugin"),enchantment.getKey().getKey(), enchantment.getKey().getNamespace(), level_ench, level_cost);

                        if (offers_custom[offer_index].isCustom()){
                            event.getOffers()[offer_index] = new EnchantmentOffer(enchantment, level_ench, level_cost);
                        }

                    }
                }

                items_enchants.put(type_equipment, offers_custom);
                System.out.println("Converts1");
                container.set(key, PersistentDataType.BYTE_ARRAY, Converts.ConvertToByteArray(items_enchants));



            } else {
                System.out.println("exist");
                EnchantmentOffersCustom[] having_offers = items_enchants.get(type_equipment);
                for (int i = 0; i <= 2; i++) {
                    if (having_offers[i] !=null && having_offers[i].isCustom()){
                        event.getOffers()[i] = new EnchantmentOffer(having_offers[i].getEnchantment(), having_offers[i].getEnchantmentLevel(), having_offers[i].getCost());

                    }

                }
            }


        }

    }




    @EventHandler
    public void OnEnchant(EnchantItemEvent event) throws Exception {
        System.out.println("Enchant" + event.getItem().getType().toString());

        ItemStack item = event.getItem();

        EnchantItemType.EnchantType type_equipment = EnchantItemType.EnchantType.CheckType(item);
        Player player = event.getEnchanter();
        NamespacedKey key = NamespacedKey.fromString("items_enchants");
        PersistentDataContainer container = player.getPersistentDataContainer();

        if (type_equipment != null && container.has(key, PersistentDataType.BYTE_ARRAY)) {
            Hashtable<EnchantItemType.EnchantType, EnchantmentOffersCustom[]> items_enchants = (Hashtable<EnchantItemType.EnchantType, EnchantmentOffersCustom[]>) Converts.ConvertToObject(container.get(key, PersistentDataType.BYTE_ARRAY));
            if (items_enchants !=null && items_enchants.containsKey(type_equipment)) {
                EnchantmentOffersCustom offer = items_enchants.get(type_equipment)[event.whichButton()];
                if (offer!=null){
                    if (offer.getNamespacekey().equals("meplugin")) {
                        item.addEnchantment(offer.getEnchantment(), offer.getEnchantmentLevel());
                        System.out.println("addEnchantment");
                        CustomEnchantLore(item, offer.getEnchantment(), offer.getEnchantmentLevel());
                    }
                }


            }
            container.remove(key);
        }

    }

//    @EventHandler
//    public void RemoveCustomEnchant(InventoryClickEvent event){
//        if (event.getClickedInventory().getType() == InventoryType.GRINDSTONE && event.getSlotType() == InventoryType.SlotType.RESULT) {
//            ItemStack item = event.getCurrentItem();
//            ItemMeta meta = item.getItemMeta();
//            PersistentDataContainer container = meta.getPersistentDataContainer();
//            NamespacedKey key = NamespacedKey.fromString("for_remove_lore_enchant");
//            if (container.has(key, PersistentDataType.PrimitivePersistentDataType.STRING)){
//                String[] custom_enchant_line = container.get(key,PersistentDataType.STRING).split(",");
//                List<String> lore = meta.getLore();
//                if (lore!=null){
//                    for (String i : custom_enchant_line){
//                        if (lore.contains(i)){
//                            lore.remove(lore.indexOf(i));
//                        }
//                    }
//                }
//
//                meta.setLore(lore);
//                item.setItemMeta(meta);
//
//                container.remove(key);
//            }
//
//        }
//    }

    @EventHandler
    public void RemoveCustomEnchant(PrepareGrindstoneEvent event){
        ItemStack item = event.getResult();
        if (item!=null && item.hasItemMeta()){
            RemoveCustomEnchantsLore(item);
            }
    }

    private void RemoveCustomEnchantsLore(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = NamespacedKey.fromString("for_remove_lore_enchant");
        if (container.has(key, PersistentDataType.PrimitivePersistentDataType.STRING)){
            String[] custom_enchant_line = container.get(key,PersistentDataType.STRING).split(",");
            List<String> lore = meta.getLore();
            for (String i : custom_enchant_line){
                if (lore!=null){
                    if (lore.contains(i)){
                        lore.remove(i);
                    }
                }

            }

            meta.setLore(lore);
            container.remove(key);
            item.setItemMeta(meta);


        }

    }


    @EventHandler
    public void UpgradeCustomEnchant(PrepareAnvilEvent event){
        ItemStack item_1 = event.getInventory().getItem(0);
        ItemStack item_2 = event.getInventory().getItem(1);

        ItemStack result = event.getResult();
        System.out.println(result);
        if (item_1 !=null && item_2 !=null && (item_1.getType() == item_2.getType() || item_2.getType() == Material.ENCHANTED_BOOK)){
            Map<Enchantment, Integer>  item_1_custom_enchants = getCustomEnchantments(item_1.getEnchantments());
            Map<Enchantment, Integer>  item_2_custom_enchants = getCustomEnchantments(item_2.getEnchantments());
            Map<Enchantment, Integer>  result_custom_enchants = getCustomEnchantments(item_2.getEnchantments());
            if (item_1_custom_enchants.size() > 0 || item_2_custom_enchants.size() > 0){ //если у хоть у кого то есть кастомные зачарования
                for (Enchantment enchant: item_1_custom_enchants.keySet()){
                    if (item_2_custom_enchants.containsKey(enchant)){ //если есть совподающие зачарование
                        if (item_1_custom_enchants.get(enchant) == item_2_custom_enchants.get(enchant)){ //если совподает их уровень
                            System.out.println(enchant.getMaxLevel() + " " + (item_1_custom_enchants.get(enchant) +1));
                            if (item_1_custom_enchants.get(enchant)+1<=enchant.getMaxLevel()){
                                result_custom_enchants.put(enchant, item_1_custom_enchants.get(enchant)+1); //то ставим это зачарование с уровенем выше
                            }
                            else {
                                result_custom_enchants.put(enchant, item_1_custom_enchants.get(enchant));
                            }
                        }
                        else {//иначе, берем то, что больше
                            result_custom_enchants.put(enchant,max(item_1_custom_enchants.get(enchant),item_2_custom_enchants.get(enchant)));
                        }
                    }
                    else {//если зачарования не совоподают, ставим зачарования из первого слота
                        result_custom_enchants.put(enchant, item_1_custom_enchants.get(enchant));
                    }

                }

                for (Enchantment enchant: item_2_custom_enchants.keySet()){//ищем оставшиеся кастомные зачарования из второго слота и ставим их
                    if (!(item_1_custom_enchants.containsKey(enchant))){
                        result_custom_enchants.put(enchant, item_2_custom_enchants.get(enchant));

                    }
                }

                if (result == null && result_custom_enchants.size()>0){
                    event.getInventory().setItem(2,item_1);
                    event.getInventory().setRepairCost(0);
                    event.setResult(event.getInventory().getItem(2));
                    result = event.getResult();

                }


                for (Enchantment enchant : result_custom_enchants.keySet()){
                    if (enchant.getItemTarget().includes(result) || result.getType() == Material.ENCHANTED_BOOK){
                        result.addEnchantment(enchant,result_custom_enchants.get(enchant));

                        System.out.println(result_custom_enchants.get(enchant));
                        event.getInventory().setRepairCost(event.getInventory().getRepairCost()+result_custom_enchants.get(enchant));

                    }
                }
                System.out.println(result);


                RemoveCustomEnchantsLore(result);

                for (Enchantment enchant: result.getEnchantments().keySet()){//записываем все кастомные зачарования в лор
                    if (enchant instanceof CustomEnchantmentWrapper){
                        CustomEnchantLore(result,enchant, result.getEnchantments().get(enchant));
                    }
                }

            }
        }


    }

    private Map<Enchantment, Integer> getCustomEnchantments(Map<Enchantment,Integer> enchants){
        Map<Enchantment,Integer> map = new Hashtable<>();
        for (Enchantment enchant: enchants.keySet()){
            if (enchant instanceof CustomEnchantmentWrapper){
                map.put(enchant, enchants.get(enchant));
            }
        }

        return map;
    }



    public void CustomEnchantLore(ItemStack item, Enchantment enchant, int level) {
    List<String> newLores = new ArrayList<String>();
    if (item.hasItemMeta()) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta.hasLore()) {
            newLores.addAll(itemMeta.getLore());
        }
        String lore_str = FormingEnchantLore(enchant, level);
        newLores.add(ChatColor.translateAlternateColorCodes('&', lore_str));
        itemMeta.setLore(newLores);

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey key = NamespacedKey.fromString("for_remove_lore_enchant");
        if (container.has(key, PersistentDataType.PrimitivePersistentDataType.STRING)){

            String str = container.get(key,PersistentDataType.STRING);
            container.set(key,PersistentDataType.STRING,str + "," +  lore_str);
        }
        else{
            container.set(key,PersistentDataType.STRING, lore_str);
        }

        item.setItemMeta(itemMeta);

        }
    }
    public String FormingEnchantLore(Enchantment enchant, int level) {
        if (enchant.getMaxLevel()>1){
            return enchant.getName().toString() + " " +LevelString[level-1];
        }
        return enchant.getName().toString();
    }

    @EventHandler
    public void OnEquipmentSlot(PlayerArmorChangeEvent event){

        ItemStack new_item = event.getNewItem();
        ItemStack old_item = event.getOldItem();

        if (event.getSlotType() == PlayerArmorChangeEvent.SlotType.FEET){
              Map<Enchantment,Integer> enchantments = new_item.getEnchantments();
            if (enchantments.containsKey(MePlugin.SPEED)){
                int level = enchantments.get(MePlugin.SPEED);
                float speed = 0.2F + (level*0.04F);
                event.getPlayer().setWalkSpeed(speed);
            }
            else if (old_item.getEnchantments().containsKey(MePlugin.SPEED)){
                event.getPlayer().setWalkSpeed(0.2F);
            }
        }

    }

    @EventHandler
    public void OnBlockDropItem(BlockDropItemEvent event) {
        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        Material material_block = event.getBlockState().getType();
        if (tool.getEnchantments().containsKey(MePlugin.BLESSING_OF_HEPHAESTUS)) {
            System.out.println(material_block);
            List<Item> items = event.getItems();
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                ItemStack itemStack = item.getItemStack();
                if (Data.getOre_ingot().containsKey(material_block)){
                    itemStack.setType(Data.getOre_ingot().get(material_block));
                    item.setItemStack(itemStack);
            }
        }
    }


    }

}



