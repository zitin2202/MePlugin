package me.zitin2202.meplugin.events;

import me.zitin2202.meplugin.Converts;
import me.zitin2202.meplugin.Data;
import me.zitin2202.meplugin.MePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Random;


public class ExpEvent implements Listener {

    JavaPlugin plugin = MePlugin.getPlugin();

    Hashtable<Sign,Player> current_signs_for_linkin = new Hashtable<>();
    Random rnd = new Random();

    @EventHandler
    public void OnPlayerLoad(PlayerJoinEvent event){
        Player player = event.getPlayer();
        player.sendMessage("" +
                "Добро пожаловать " + player.getDisplayName() + " " +
                "\nНа данный момент работают следующие ивенты:" +
                "\n-Опыт и вещи за маяк (40 раз в стуки, требуется голод)" +
                "\n-Опыт за жертву лаве" +
                "\n-Телепорт по таблицам" +
                "\n-Давая жертву лаве тотем бессмертия, максимальное здровье увеличивается навсегда" +
                "\n\nНе используйте золотой блок, пока не уверены в своём пропитании" +
                "\n--НОВЫЕ ИВЕНТЫ--:" +
                "\n-Опыт и вещи теперь дают за специальный маяк(золотой блок + алмазный + изумрудный) " +
                "\nа также особая возможность в последний момент" +
                "\n-Давая жертву лаве золотое зачарованное яблоко, текущий уровень увеличивается вдвое");





        if (player.hasMetadata("lottery_block_chance") == false){
            player.setMetadata("lottery_block_chance", new FixedMetadataValue(plugin, 40));

            System.out.println("new player " + player.getName());

            Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                @Override
                public void run() {
                    System.out.println("NEW DAY FOR " + player.getName());
                    player.setMetadata("lottery_block_chance", new FixedMetadataValue(plugin, 40));
                }
            },24000,24000);


        }

//        Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
//            @Override
//            public void run() {
////                player.chat(player.getLocation().getDirection().getY() + "    " + player.getLocation().getYaw());
//                System.out.println(player.getFacing());
//
//            }
//        },0,100);




    };





    @EventHandler
    public void ExpWithHelpItemMelting(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        int amount = item.getItemStack().getAmount();



        try {


        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (item.getLastDamageCause() !=null && item.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.LAVA){
                    if (item.getItemStack().getType() ==  Material.TOTEM_OF_UNDYING){
                        AttributeInstance max_hp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                        max_hp.setBaseValue(max_hp.getValue()+1);
                    }
                    else if (item.getItemStack().getType() ==  Material.ENCHANTED_GOLDEN_APPLE){
                        player.setLevel(player.getLevel()*2);
                    }

                    else {
                        player.giveExp(amount*2);
                    }

                }
            }
        },15);
        }
        catch (Exception e){
            System.out.println("ошибка при выбрасывании предметов");
        }

    }


    @EventHandler
        public void Teleport(PlayerInteractEvent event) throws IOException, ClassNotFoundException {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            if (block.getState() instanceof Sign){
                Sign sign = (Sign)block.getState();
                if (sign.getLines()[0].equals("телепорт")){
                    TileState state = (TileState)block.getState();
                    PersistentDataContainer container = sign.getPersistentDataContainer();
                    NamespacedKey key = NamespacedKey.fromString("teleport_info");
                    if (!(container.has(key,PersistentDataType.BYTE_ARRAY))){
                        if (!(current_signs_for_linkin.containsKey(sign))){
                            if (player.hasMetadata("sign_there")){
                                Sign sign_there = (Sign)player.getMetadata("sign_there").get(0).value();
                                //Сохраняем локацию другой таблицы в текущей
                                container.set(key, PersistentDataType.BYTE_ARRAY, Converts.BukkitConvertToByteArray(sign_there.getLocation()));
                                sign.update();

                                //Сохраняем локацию текущей таблицы в в другой
                                sign_there.getPersistentDataContainer().set(key, PersistentDataType.BYTE_ARRAY,Converts.BukkitConvertToByteArray(sign.getLocation()));
                                sign_there.update();

                                player.removeMetadata("sign_there",plugin);
                                current_signs_for_linkin.remove(sign_there);


                                String[] sign_text = sign.getLines().clone();//Свапаю текст табличек
                                for(int i=0; i<sign_there.getLines().length;i++){
                                    sign.setLine(i,sign_there.getLine(i));

                                }

                                for(int i=0; i<sign_text.length;i++){
                                    sign_there.setLine(i,sign_text[i]);

                                }

                                sign.setGlowingText(true);
                                sign_there.setGlowingText(true);

                                sign.update();
                                sign_there.update();

                                SignAddEvent(block);

                                System.out.println("Success");



                            }
                            else {
                                player.setMetadata("sign_there", new FixedMetadataValue(plugin,sign));
                                current_signs_for_linkin.put(sign,player);
                                SignAddEvent(block);

                            }
                        }

                    }

                    else {
                        Location location =(Location)Converts.BukkitConvertToObject(container.get(key,PersistentDataType.BYTE_ARRAY));
                        Data.PlaySound(block,org.bukkit.Sound.BLOCK_BEACON_ACTIVATE);
                        Data.PlaySound(location.getBlock(),org.bukkit.Sound.BLOCK_BEACON_ACTIVATE);
                        player.teleport(location);

                    }
                }

                else {
                    System.out.println("text not is 'телепорт' " + "you read is '" + sign.getLines()[0] + "'. text " + (sign.getLines()[0].equals("телепорт")));
                    player.sendMessage(sign.getLines()[0]);
                }

            }

        }
    }


    private void SignAddEvent(Block block){
        System.out.println("Add teleport block");
        Data.PlaySound(block,org.bukkit.Sound.BLOCK_CHAIN_STEP);
    }


    @EventHandler
    public void OnBlockBreak(BlockBreakEvent event) throws IOException, ClassNotFoundException {
        SignTeleportDestroy(event.getBlock());
    }


    @EventHandler
    public void OnEntityExplode(EntityExplodeEvent event) throws IOException, ClassNotFoundException {
        for (Block block : event.blockList()){
            SignTeleportDestroy(block);
        }

    }


    public void SignTeleportDestroy(Block block) throws IOException, ClassNotFoundException {
        if (block.getState() instanceof Sign){
            NamespacedKey key = NamespacedKey.fromString("teleport_info");
            PersistentDataContainer container = ((Sign) block.getState()).getPersistentDataContainer();

            Sign sign = (Sign)block.getState();
            if (current_signs_for_linkin.containsKey(sign)){
                Player player = current_signs_for_linkin.get(sign);
                player.removeMetadata("sign_there",plugin);
                current_signs_for_linkin.remove(sign);
            }
            else if (container.has(key,PersistentDataType.BYTE_ARRAY)){
                Location location = (Location)Converts.BukkitConvertToObject(container.get(key,PersistentDataType.BYTE_ARRAY));
                Sign there_sign = (Sign)location.getBlock().getState();
                System.out.println("remove key from container");
                there_sign.getPersistentDataContainer().remove(key);
                there_sign.setGlowingText(false);
                there_sign.update();
           }
        }
    }






    }



//
//    Thread myThready = new Thread(new Runnable()
//    {
//        public void run() //Этот метод будет выполняться в побочном потоке
//        {
//            int exp = rnd.nextInt(40);
//            int pause = rnd.nextInt(10000);
//
//            while (true) {
//                player.giveExp(exp);
//                int itemid = rnd.nextInt(Material.values().length);
//                player.getInventory().addItem(new ItemStack(Material.values()[itemid],rnd.nextInt(64)));
//                try {
//                    Thread.sleep(pause);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//        }
//    });
//                myThready.start();	//Запуск потока
