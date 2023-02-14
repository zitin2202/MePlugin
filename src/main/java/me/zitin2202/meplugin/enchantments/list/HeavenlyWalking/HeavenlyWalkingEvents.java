package me.zitin2202.meplugin.enchantments.list.HeavenlyWalking;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.zitin2202.meplugin.MePlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class HeavenlyWalkingEvents implements Listener {

    MePlugin plugin = MePlugin.getPlugin();
    String heavenly_walking_count_key = "heavenly_walking_count_key";
    String temporary_block_key = "temporary_block_key";

    BlockFace[] block_faces = new BlockFace[]{BlockFace.NORTH,BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH_WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_WEST, BlockFace.SOUTH_EAST};
    @EventHandler
    public void Onjump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        ItemStack boots = player.getEquipment().getBoots();
        int start_y = player.getLocation().getBlockY();
        if (boots != null && boots.getEnchantments().containsKey(MePlugin.HEAVENLY_WALKING)) {
            int enchant_level = boots.getEnchantments().get(MePlugin.HEAVENLY_WALKING);
            int block_delete_time = enchant_level*30;
            int amount_walk = enchant_level*10;
            Block block_down_start = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

            if (player.hasMetadata(heavenly_walking_count_key)){
                if (block_down_start.hasMetadata(temporary_block_key) || block_down_start.getType() == Material.AIR){
                    int heavenly_walking_count = (int) player.getMetadata(heavenly_walking_count_key).get(0).value();
                    if (heavenly_walking_count >= amount_walk){
                        System.out.println("return");
                        return;

                    }
                }
                else {
                    player.removeMetadata(heavenly_walking_count_key,plugin);
                }
            }





            int pause = 1;

            new BukkitRunnable() {
                int timer_count = 0;

                @Override
                public void run() {
                    if (player.getLocation().getBlockY() == start_y){
                        Block block_down = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
                        if (block_down.getType() == Material.AIR){
                            SetTemporaryBlock(block_down,block_delete_time);
                            if (enchant_level==7){
                                for (BlockFace face: block_faces){
                                    SetTemporaryBlock(block_down.getRelative(face),block_delete_time);
                                }
                            }
                            else if (enchant_level>=5){
                                SetTemporaryBlock(block_down.getRelative(player.getFacing()),block_delete_time);
                            }


                            if (!(player.hasMetadata(heavenly_walking_count_key))){
                                player.setMetadata(heavenly_walking_count_key, new FixedMetadataValue(plugin,0));
                            }

                            int heavenly_walking_count = (int) player.getMetadata(heavenly_walking_count_key).get(0).value();

                            heavenly_walking_count+=1;

                            player.setMetadata(heavenly_walking_count_key, new FixedMetadataValue(plugin,heavenly_walking_count));


                        }
                        this.cancel();
                        return;

                    }
                    timer_count+=pause;
                    if (timer_count>=40){
                        this.cancel();
                        return;
                    }



                }

            }.runTaskTimer(MePlugin.getPlugin(), 10,pause);
        }
    }


    private void SetTemporaryBlock(Block block, long delay){
            if (block.getType() == Material.AIR){
                block.setType(Material.DIRT);
                block.setMetadata(temporary_block_key,new FixedMetadataValue(plugin,0));

            new BukkitRunnable() {
                @Override
                public void run() {
                        block.setType(Material.AIR);
                        block.removeMetadata(temporary_block_key,plugin);

                }
            }.runTaskLater(plugin, delay);
        }
    }
}
