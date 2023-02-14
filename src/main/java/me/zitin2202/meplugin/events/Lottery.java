package me.zitin2202.meplugin.events;

import me.zitin2202.meplugin.Data;
import me.zitin2202.meplugin.MePlugin;
import org.bukkit.Sound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static java.lang.Math.round;

public class Lottery implements Listener {


    Object[][] items = {
            {new ItemStack(Material.TOTEM_OF_UNDYING), 5},
            {new ItemStack(Material.LAPIS_LAZULI, 15), 20},
            {new ItemStack(Material.DIAMOND), 10},
            {new ItemStack(Material.NETHERITE_INGOT), 7},
            {new ItemStack(Material.IRON_INGOT, 20), 18},
            {new ItemStack(Material.EMERALD, 10), 15},
            {new ItemStack(Material.COAL, 64), 20},
            {new ItemStack(Material.ENCHANTED_GOLDEN_APPLE), 5},


    };

    int[] items_way = {0,7,1,6,2,5,3,4};
    NamespacedKey block_key = NamespacedKey.fromString("lottery_block");
    NamespacedKey frame_key = NamespacedKey.fromString("lottery_frame");

    Random rnd = new Random();




    private void CreateFrame(Location location, int code, int item_number, int light, Class<? extends ItemFrame> classs, boolean state){
        ItemFrame itemFrame = location.getWorld().spawn(location, classs);
        itemFrame.setItemDropChance(0);
        itemFrame.setFixed(true);
        itemFrame.setItem((ItemStack)items[item_number][0]);

        if (!(state)) {
            itemFrame.setVisible(false);
        }

        int[] data_frame = {code,item_number,light};
        itemFrame.getPersistentDataContainer().set(frame_key,PersistentDataType.INTEGER_ARRAY,data_frame);


    }
    @EventHandler
    public void OnBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        World world = event.getBlock().getWorld();
        if (block.getType() == Material.EMERALD_BLOCK) {
            Block blockZ_1 = block.getRelative(BlockFace.DOWN);
            Block blockZ_2 = blockZ_1.getRelative(BlockFace.DOWN);
            if (blockZ_1.getType() == Material.DIAMOND_BLOCK && blockZ_2.getType() == Material.GOLD_BLOCK) {
                block.setType(Material.AIR);
                blockZ_2.setType(Material.AIR);
                blockZ_1.setType(Material.BEACON);
                TileState state = ((TileState) blockZ_1.getState());
                PersistentDataContainer container = state.getPersistentDataContainer();
                int code = rnd.nextInt(1000000000);
                container.set(block_key, PersistentDataType.INTEGER,code);
                state.update();
                Player player = event.getPlayer();
                BlockFace face_player = player.getFacing();

                int[][] design =
                        {
                                {0, 0, 1, 0, 0},
                                {0, 1, 0, 1, 0},
                                {1, 0, 0, 0, 1},
                                {0, 1, 0, 1, 0},
                                {0, 0, 1, 0, 0},
                        };
                int[] center_coord = {design.length - 1 - 1, design[0].length / 2};
                List<Block> blocks = Data.MassSetBlock(design, center_coord, blockZ_1.getLocation(), Material.WHITE_CONCRETE, player.getFacing());
                for (int i = 0; i < blocks.size(); i++) {
                    Location location_frame = new Location(world,
                            blocks.get(i).getX() + (-face_player.getModX()),
                            blocks.get(i).getY() + (-face_player.getModY()),
                            blocks.get(i).getZ() + (-face_player.getModZ()));

                    CreateFrame(location_frame,code,items_way[i],0, ItemFrame.class,true);
                    CreateFrame(location_frame,code,items_way[i],1, GlowItemFrame.class,false);

                }
            }


        }
    }

    private Collection<ItemFrame> GetItemFrames(Block block){
        Collection<Entity> collection = block.getWorld().getNearbyEntities(block.getLocation(),4,4,4);
        TileState state = (TileState)block.getState();
        int code = state.getPersistentDataContainer().get(block_key,PersistentDataType.INTEGER);
        Collection<ItemFrame> itemFrames = new ArrayList<>();
        for (Entity entity : collection){
            PersistentDataContainer entity_container = entity.getPersistentDataContainer();
            if (entity instanceof ItemFrame && entity_container.has(frame_key,PersistentDataType.INTEGER_ARRAY) && entity_container.get(frame_key,PersistentDataType.INTEGER_ARRAY)[0] == code){
                itemFrames.add((ItemFrame)entity);
            }
        }
        return itemFrames;
    }

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getState() instanceof TileState && ((TileState)block.getState()).getPersistentDataContainer().has(block_key,PersistentDataType.INTEGER)){
            Collection<ItemFrame> itemFrames = GetItemFrames(block);
            for (ItemFrame frame : itemFrames){
                frame.remove();
            }
        }
    }


    @EventHandler
    public void OnLotteryBlockClick(PlayerInteractEvent event) throws InterruptedException {
        Player player = event.getPlayer();
        System.out.println(event.getBlockFace());
        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            Block block = event.getClickedBlock();
            if (block.getType() == Material.BEACON && block.getState() instanceof TileState && ((TileState)block.getState()).getPersistentDataContainer().has(block_key,PersistentDataType.INTEGER)){
                int chance = player.getMetadata("lottery_block_chance").get(0).asInt();
                if (chance == 1){
                    player.setMetadata("lottery_block_chance", new FixedMetadataValue(MePlugin.getPlugin(), chance-1));
                    SuperLottery(block,player);
                }
                else if (chance > 0){
                    player.setMetadata("lottery_block_chance", new FixedMetadataValue(MePlugin.getPlugin(), chance-1));
                    player.giveExp(rnd.nextInt(200));
//                    int itemid = rnd.nextInt(Material.values().length);
//                    player.getInventory().addItem(new ItemStack(Material.values()[itemid]));
                    player.setFoodLevel(player.getFoodLevel()-1);
                    Data.PlaySound(block,org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_HIT);
                }
            }

        }
    }
    private void SuperLottery(Block block,Player player) throws InterruptedException {
        Collection<ItemFrame> itemFramesList = GetItemFrames(block);
        Object[] itemFrames = itemFramesList.toArray();
        ItemFrame[][] itemFramesSort= new ItemFrame[items.length][2];
        for (int i=0; i<itemFrames.length;i++){
            int[] data_frame =  ((ItemFrame)itemFrames[i]).getPersistentDataContainer().get(frame_key, PersistentDataType.INTEGER_ARRAY);
            int item_id = data_frame[1];
            int light = data_frame[2];
            itemFramesSort[item_id][light] = (ItemFrame)itemFrames[i];
        }

        int luck = rnd.nextInt(100);
        int chance_sum = 0;
        int result = 0;

        int chance;
        for (int i=0; i<items.length;i++){
            chance = (int) items[i][1];
            if (chance_sum <= luck && luck < chance_sum+chance){
                result = i;
                break;
            }
            chance_sum +=chance;
        }

        System.out.println(items[result][0]);
        int laps = 5;
        int quantity = (itemFramesSort.length)*laps+1+result;
        int start_speed = 100;
        int finish_speed = 500;
        float coeff = (float)finish_speed/start_speed/quantity;


        int finalResult = result; //особый функции не несёт. Но в run не принимаются переменные, которых определили больше одного раза.Пришлось создать еще одну
        Thread myThready = new Thread(new Runnable()
        {
            public void run()
            {
                int pause = start_speed;
                int count = 0;
                for (int lap=0; lap<laps+1;lap++){
                    for (int i=0; i<itemFramesSort.length && count<quantity;i++) {
                        Data.PlaySound(block, Sound.BLOCK_ANVIL_HIT);
                        if (count>0){
                            pause+= round(start_speed*coeff);
                        }
                        itemFramesSort[i][0].setVisible(false);
                        itemFramesSort[i][1].setVisible(true);

                        if (count+1==quantity){
                            pause*=5;
                        }
                        try {
                            Thread.sleep(pause);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        itemFramesSort[i][1].setVisible(false);
                        itemFramesSort[i][0].setVisible(true);

                        count+=1;


                    }
                }
                player.getInventory().addItem((ItemStack)items[finalResult][0]);
            }
        });
        myThready.start();	//Запуск потока


    }
}