package at.tuw.iir;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TopicProcessor {

    private static List<Topic> topics = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        //processWord();
    }

   private static void mergeIndices(){
        Set<String> dict = new HashSet<>();

        for(int j = 1; j<= 6; j++){
            try(BufferedReader br = Files.newBufferedReader(Paths.get("index/inverted_index_Mod" + Integer.toString(j) + ".txt"))){

                String line = br.readLine();

                while (line != null) {
                    //get the line such as "cold: 432490,2 853498,3" and split to "cold" and "432490,2 853498,3"
                    String[] args1 = line.trim().split(":");
                    String term = args1[0];

                    dict.add(term);

                    line = br.readLine();
                }

            }catch(IOException ex){
                ex.printStackTrace();
            }
        }

        dict = dict.stream().filter(s -> !s.startsWith("http")).collect(Collectors.toSet());

        System.out.println(dict.size());

       try(BufferedWriter bw = Files.newBufferedWriter(Paths.get("index/final_index.txt"))){

           for(int i = 1; i <=6; i++) {
               try(BufferedReader br2 = Files.newBufferedReader(Paths.get("index/inverted_index_Mod" + Integer.toString(i) + ".txt"))){
                    String line = br2.readLine();

                    while(line != null) {
                        bw.write(line);
                        bw.newLine();
                        line = br2.readLine();
                    }
               }
           }

       } catch(IOException ex){
           ex.printStackTrace();
       }

    }


    private static void processWord() throws IOException {

        final String regex = "^(\\w+): (.*)\\n((?:(?!\\1).*\\n)+)\\1: (.*\\n)";

        final String string = new String(Files.readAllBytes(Paths.get("index/final_index.txt")), "UTF-8");
        final String subst = "\\1: \\2 \\4\\3";

        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(string);

        final String result = matcher.replaceAll(subst);

        BufferedWriter bw = Files.newBufferedWriter(Paths.get("index/new_index.txt"));
        bw.write(result);
        bw.close();
    }


}
