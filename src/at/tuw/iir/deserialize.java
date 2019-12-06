package at.tuw.iir;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

public class deserialize {

    public static void main(String [] args)
    {
        HashMap<Integer, String> map = null;
        try
        {
            FileInputStream fis = new FileInputStream("/Users/JamesGlass/hashmap.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
            return;
        }catch(ClassNotFoundException c)
        {
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }
        System.out.println("Deserialized HashMap..");
        // Display content using Iterator
        Set set = map.entrySet();
        Iterator iterator = set.iterator();

    }

}
