package me.zitin2202.meplugin.events;

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import me.zitin2202.meplugin.Converts;
import me.zitin2202.meplugin.Data;
import me.zitin2202.meplugin.MePlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class Memorise implements Listener {

    Random rnd = new Random();
    
    MePlugin plugin = MePlugin.getPlugin();
    
    NamespacedKey blocks_key = NamespacedKey.fromString("memorise_blocks");
    String sequence_key = "memorise_sequence";
    String sequence_decrement_key = "memorise_sequence_decrement";
    String memorise_frame_key = "memorise_frame";
    String is_frame_free = "is_free_memorise";
    String is_animation_process = "is_animation_process";
    String player_playing_in_memorise_key = "play_plating_in_memorise";
    String item_amount = "item_amount_in_memorise";







    @EventHandler
    public void OnPlaceBlock(BlockPlaceEvent event) throws IOException, ClassNotFoundException {
        Player player = event.getPlayer();
        BlockFace face_player = player.getFacing();
        Block block = event.getBlock();
        World world = block.getWorld();


        if (block.getType() == Material.REDSTONE_LAMP){
            Block block_down = block.getRelative(BlockFace.DOWN);
            if (block_down.getType() == Material.NOTE_BLOCK){
                block.setType(Material.JUKEBOX);

                BlockFace[] all_size = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

                int [][] design = new int[][]
                        {
                                {0,1,0},
                                {1,0,1},
                                {0,1,0},
                        };
                Block[] blocks = Data.MassSetBlock(design,new int[]{1,1}, block.getLocation(), Material.REDSTONE_LAMP, face_player).toArray(Block[]::new);

                Location loc_frame = Data.GetRelativeLocation(block.getLocation(),new int[]{
                        -face_player.getModX(),
                        -face_player.getModY(),
                        -face_player.getModZ()});

                ItemFrame itemFrame = world.spawn(loc_frame, ItemFrame.class);
                itemFrame.setItemDropChance(0);

                PersistentDataContainer container = itemFrame.getPersistentDataContainer();


                Location[] block_locations = Stream.of(blocks).map(blockk->blockk.getLocation()).toArray(Location[]::new);


                container.set(blocks_key, PersistentDataType.BYTE_ARRAY, Converts.BukkitConvertToByteArray(block_locations));


            }
        }
    }

    private boolean CheckItemFrame(Entity entity){
        if (entity instanceof ItemFrame){
            ItemFrame frame = (ItemFrame)entity;
            if (frame.getPersistentDataContainer().has(blocks_key,PersistentDataType.BYTE_ARRAY)){
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void OnPlayerInteractEntity(PlayerInteractEntityEvent event) throws IOException, ClassNotFoundException {
        if (CheckItemFrame(event.getRightClicked())){
            event.setCancelled(true);
            Player player = event.getPlayer();
            ItemFrame frame = (ItemFrame)event.getRightClicked();
            if (frame.hasMetadata(is_frame_free)){
                if ((int)frame.getMetadata(is_frame_free).get(0).value() == 0)
                    return;
            }
            if (frame.hasMetadata(player_playing_in_memorise_key)){
                if (!(event.getPlayer().equals((Player)frame.getMetadata(player_playing_in_memorise_key).get(0).value())))
                    return;
            }
            if (frame.hasMetadata(is_animation_process)){
                if ((int)frame.getMetadata(is_animation_process).get(0).value() == 1)
                    return;
            }
            PersistentDataContainer frame_container = frame.getPersistentDataContainer();
            Location[] block_locs = (Location[])Converts.BukkitConvertToObject(frame_container.get(blocks_key,PersistentDataType.BYTE_ARRAY));
            List<Location> sequence;

            if (player.hasMetadata(sequence_key)){

                sequence = (List<Location>)player.getMetadata(sequence_key).get(0).value();
            }
            else {
                sequence = new ArrayList<>();
                player.setMetadata(memorise_frame_key,new FixedMetadataValue(plugin,frame));
                frame.setMetadata(player_playing_in_memorise_key,new FixedMetadataValue(plugin,player));
            }

            sequence.add(block_locs[rnd.nextInt(block_locs.length)]);
            player.setMetadata(sequence_key,new FixedMetadataValue(plugin,sequence));
            player.setMetadata(sequence_decrement_key,new FixedMetadataValue(plugin,new ArrayList<>(sequence)));
            frame.setMetadata(is_frame_free,new FixedMetadataValue(plugin,0));
            frame.setMetadata(is_animation_process,new FixedMetadataValue(plugin,1));


            LitAnimation(sequence,frame, 20);


        }
    }


    private void LitAnimation(List<Location> sequence, ItemFrame frame, int pause){
        new BukkitRunnable() {
            int sequence_i = 0;
            @Override
            public void run() {
                if (sequence_i>0){
                    Location last_loc = sequence.get(sequence_i-1);
                    Block last_block = last_loc.getBlock();
                    Lightable last_lightable = (Lightable)last_block.getBlockData();
                    last_lightable.setLit(false);
                    last_block.setBlockData(last_lightable);

                }
                if (sequence_i==sequence.size()){
                    this.cancel();
                    frame.setMetadata(is_animation_process,new FixedMetadataValue(plugin,0));
                    return;
                }

                Location loc = sequence.get(sequence_i);
                Block block = loc.getBlock();
                Lightable lightable = (Lightable)block.getBlockData();
                lightable.setLit(true);
                block.setBlockData(lightable);
                Data.PlaySound(block, Sound.BLOCK_NOTE_BLOCK_HARP);

                sequence_i+=1;

            }
        }.runTaskTimer(plugin, 0, pause);
    }

    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent event) throws IOException, ClassNotFoundException {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK){
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if (player.hasMetadata(sequence_decrement_key)){

                ItemFrame frame = (ItemFrame) player.getMetadata(memorise_frame_key).get(0).value();

                if ((int)frame.getMetadata(is_animation_process).get(0).value() == 1){
                    return;
                }

                Location[] all_loc = (Location[])Converts.BukkitConvertToObject(frame.getPersistentDataContainer().get(blocks_key,PersistentDataType.BYTE_ARRAY));

                boolean is_need_block = false;
                for (Location loc: all_loc){
                    if (loc.getBlock().equals(block)){
                        is_need_block = true;
                    }
                }


                if (is_need_block){
                    List<Location> sequence = (List<Location>) player.getMetadata(sequence_decrement_key).get(0).value();
                    if (block.equals(sequence.get(0).getBlock())){
                        sequence.remove(0);

                        List<Location> block_list_for_lit = new ArrayList<>();
                        block_list_for_lit.add(block.getLocation());
                        LitAnimation(block_list_for_lit,frame, 5);

                        if (sequence.size() == 0){
                            player.removeMetadata(sequence_decrement_key,plugin);
                            frame.setMetadata(is_frame_free,new FixedMetadataValue(plugin,1));
                            if (!(frame.hasMetadata(item_amount))){
                                frame.setItem(new ItemStack(Material.AIR));
                                while (frame.getItem().getType()==Material.AIR){
                                    int itemid = rnd.nextInt(Material.values().length);
                                    frame.setItem(new ItemStack(Material.values()[itemid]));
                                }
                                frame.setMetadata(item_amount,new FixedMetadataValue(plugin,1));
                            }
                            else {
                                frame.setMetadata(item_amount,new FixedMetadataValue(plugin,(int)frame.getMetadata(item_amount).get(0).value()+1));

                            }
                        }
                        else {
                            player.setMetadata(sequence_decrement_key, new FixedMetadataValue(plugin,sequence));

                        }

                    }
                    else {
                        Data.PlaySound(block,Sound.BLOCK_CHAIN_STEP);
                        EndMemorise(player,frame);
                        
                    }
                }
               

            }
        }

    }

    @EventHandler
    public void OnEntityDamageByEntity(HangingBreakByEntityEvent event){
        if (CheckItemFrame(event.getEntity())){
            ItemFrame frame = (ItemFrame)event.getEntity();
            event.setCancelled(true);

        }
    }


    @EventHandler
    public void OnPlayerItemFrameChange(PlayerItemFrameChangeEvent event){
       if (event.getAction() == PlayerItemFrameChangeEvent.ItemFrameChangeAction.REMOVE){
           ItemFrame frame = event.getItemFrame();
           if (frame.hasMetadata(player_playing_in_memorise_key)){
               event.setCancelled(true);
               Player player_of_frame = (Player) frame.getMetadata(player_playing_in_memorise_key).get(0).value();
               if (event.getPlayer().equals(player_of_frame)){
                   EndMemorise(player_of_frame,frame);

               }
               else {
               }

           }
       }

    }

    private void EndMemorise(Player player, ItemFrame frame){
        if (frame.hasMetadata(item_amount)){
            int amount = (int)frame.getMetadata(item_amount).get(0).value();
            player.getInventory().addItem(new ItemStack(frame.getItem().getType(),amount));
            frame.setItem(new ItemStack(Material.AIR));
            frame.removeMetadata(item_amount,plugin);
        }

        player.removeMetadata(sequence_key,plugin);
        player.removeMetadata(sequence_decrement_key,plugin);
        player.removeMetadata(memorise_frame_key,plugin);
        frame.removeMetadata(player_playing_in_memorise_key,plugin);
        frame.setMetadata(is_frame_free,new FixedMetadataValue(plugin,1));
    }


}
