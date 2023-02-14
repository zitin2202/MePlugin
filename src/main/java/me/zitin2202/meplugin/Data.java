package me.zitin2202.meplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {

    private static Map<Material,Material> ore_ingot = new HashMap<>()
    {
        {
            put(Material.IRON_ORE, Material.IRON_INGOT);
            put(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT);
            put(Material.GOLD_ORE, Material.GOLD_INGOT);
            put(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT);
            put(Material.COPPER_ORE, Material.COPPER_INGOT);
            put(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT);
            put(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP);

        }
    };


    public static void PlaySound(Block block, Sound sound) {
        block.getWorld().playSound(block.getLocation(), sound,6f,6f);
    }

    public static int[] GetSizeAcross(BlockFace direction) {
        int size = 0;
        int coord_i = 0;
        switch (direction) {
            case NORTH -> {
                size = 1;
                coord_i = 0;
            }
            case SOUTH -> {
                size = -1;
                coord_i = 0;
            }
            case WEST -> {
                size = -1;
                coord_i = 2;
            }
            case EAST -> {
                size = 1;
                coord_i = 2;
            }
        }

        int[] retrn = {size,coord_i};
        return retrn;
    }
    public static Location GetRelativeLocation(Location loc, int[] add_cords) {
               return new Location(loc.getWorld(),
                        loc.getX()+add_cords[0],
                        loc.getY()+add_cords[1],
                        loc.getZ()+add_cords[2]);
    }

    public static int[] GetAddCord(int size, int cord, int add_cord) {
        int[] loc = {0,0,0};
        loc[cord] = add_cord*size;

        return loc;

    }


        public static Map<Material,Material> getOre_ingot() {
        return ore_ingot;
    }
    public static List<Block> MassSetBlock(int[][] design, int[] center, Location center_location, Material typeBlock, BlockFace direction){
        List<Block> blocks = new ArrayList<>();
        int[] size_and_coord = GetSizeAcross(direction);
        int size = size_and_coord[0];
        int coord_i = size_and_coord[1];
        for (int i=0; i<design.length;i++){
            for(int j=0; j<design[i].length; j++){
                if (design[i][j] == 1){
                    int[] coords = {0,0,0};
                    coords[coord_i] = (j-center[1])*size;
                    coords[1] = center[0] - i ;
                    Block block = center_location.getWorld().getBlockAt(GetRelativeLocation(center_location,coords));
                    block.setType(typeBlock);
                    blocks.add(block);
                }
            }
        }

        return blocks;

    }

}
