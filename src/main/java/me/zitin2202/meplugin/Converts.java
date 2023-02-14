package me.zitin2202.meplugin;

import me.zitin2202.meplugin.enchantments.EnchantItemType;
import me.zitin2202.meplugin.enchantments.EnchantmentOffersCustom;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.Hashtable;


public class Converts implements Serializable{


    public static byte[] BukkitConvertToByteArray(Object object) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BukkitObjectOutputStream oos = new BukkitObjectOutputStream(baos);
        oos.writeObject(object);
        oos.flush();

        return baos.toByteArray();

    }



    public static Object BukkitConvertToObject(byte[] btt) throws IOException, ClassNotFoundException {

        ByteArrayInputStream bais = new ByteArrayInputStream(btt);
        BukkitObjectInputStream ois = new BukkitObjectInputStream(bais);
        Object obj = ois.readObject();

        return obj;

    }

    public static byte[] ConvertToByteArray(Hashtable<EnchantItemType.EnchantType, EnchantmentOffersCustom[]> object) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(object);
        System.out.println("ConvertToByteArray " + object.getClass());
        oos.flush();

        return baos.toByteArray();

    }

    public static Object ConvertToObject(byte[] btt) throws IOException, ClassNotFoundException {

        System.out.println("ConvertToOBJ");

        ByteArrayInputStream bais = new ByteArrayInputStream(btt);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object obj = ois.readObject();
        System.out.println("ConvertToObject ");

        System.out.println("ConvertToObject " + obj);

        return obj;

    }
}
