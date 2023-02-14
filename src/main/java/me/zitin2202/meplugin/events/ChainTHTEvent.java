package me.zitin2202.meplugin.events;

import me.zitin2202.meplugin.MePlugin;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.IOException;

public class ChainTHTEvent implements Listener {
    String chain_THT_key = "chain_THT_key";
    @EventHandler
    public void OnEntityExplode(EntityExplodeEvent event) throws IOException, ClassNotFoundException {
        Entity entity = event.getEntity();
        if (entity.hasMetadata(chain_THT_key)){
            System.out.println("cgeck container");
            int chain_count = (int) entity.getMetadata(chain_THT_key).get(0).value();
            if (chain_count<3){
                System.out.println(event.blockList().size());
                for (Block block : event.blockList()){
                   Entity entity_spawn =  block.getWorld().spawn(block.getLocation(),event.getEntity().getClass());
                   entity_spawn.setMetadata(chain_THT_key, new FixedMetadataValue(MePlugin.getPlugin(),chain_count+1));
                    ((TNTPrimed)entity_spawn).setFuseTicks(400);
                }
            }
            if (chain_count == 0){
                Entity entity_0 = entity.getWorld().spawn(entity.getLocation(),event.getEntity().getClass());
                entity_0.setMetadata(chain_THT_key, new FixedMetadataValue(MePlugin.getPlugin(),0));

            }

        }


    }

}
