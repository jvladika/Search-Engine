package at.tuw.iir;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class deserialize {

    public static void main(String [] args)
    {

        HashMap<String, Map<Long, Integer>> map = null;
        HashMap<String, Map<Long, Integer>> finalMap = new HashMap<>();

        BufferedReader br = null;
        BufferedWriter bw = null;


        for(int i = 6; i <= 6; i++) {

            try {
                if(!Files.exists(Paths.get("inverted_index" + Integer.toString(i) +".txt"))){
                    Files.createFile(Paths.get("inverted_index" + Integer.toString(i) +".txt"));
                }
                bw = Files.newBufferedWriter(Paths.get("inverted_index" + Integer.toString(i) +".txt"));
            } catch(Exception ex){
                System.out.println("Exception while trying to read the file!");
                System.exit(1);
            }

            System.out.println(i);

            try (FileInputStream fis = new FileInputStream("ser/postinglist" + Integer.toString(i) + ".ser");
                 ObjectInputStream ois = new ObjectInputStream(fis);) {

                map = (HashMap) ois.readObject();
                System.out.println("Deserialized HashMap..");

                for(Map.Entry<String, Map<Long, Integer>> entry : map.entrySet()){
                    String word = entry.getKey();

                    StringBuilder sb = new StringBuilder();
                    sb.append(word);
                    sb.append(": ");

                    Map<Long, Integer> termFreqs = entry.getValue();
                    termFreqs.entrySet().forEach(e -> {
                        sb.append(Long.toString(e.getKey()));
                        sb.append(",");
                        sb.append(Integer.toString(e.getValue()));
                        sb.append(" ");
                    });

                    sb.setLength(sb.length()-1);
                    String line = sb.toString();
                    bw.write(line);
                    bw.newLine();
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
                return;
            } catch (ClassNotFoundException c) {
                System.out.println("Class not found");
                c.printStackTrace();
                return;
            }

            System.out.println("Written contents to document..");

        }

    }

    private static int containsTerm(String word) throws IOException {

        return -1;
    }

}
