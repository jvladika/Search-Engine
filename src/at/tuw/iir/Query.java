package at.tuw.iir;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Query
{
    /** Tells if body of a document is currently being read. **/
    private static boolean isBody;

    /** Tells if title of a document is currently being read. **/
    private static boolean isTitle;

    /** Current highest ID that was read. **/
    private static int currId = 0;

    /** Porter Stemmer used for stemming tokens. **/
    private static Stemmer stemmer = new Stemmer();

    /** NLTK's list of 128 stop words. **/
    private static HashSet<String> stopWords = new HashSet<String>(Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"
    ));

    /** Inverted index for posting list.
     * Key is a term and it is mapped to a linked list of documents.
     * Value is a linked hash list where each entry is a pair (document, frequency);
     * i.e. each entry says in which document and how many times the term appears.
     * **/
    private static Map<String, LinkedHashMap<Long, Integer>> postingList = new HashMap<>();

    private static List<Document> documents = new ArrayList<>();


    public static void main(String[] args) throws IOException {
        char[] wordBuffer = new char[100];
        byte[] byteBuffer = new byte[10000];
        int currentWordStart = 0;

        /*
        TODO:
        I have tried it only for dev set so far and evaluation set, which is way larger.
        It seemed to work okay for dev set...
         */

        for(int xmlId = 115; xmlId <= 250; xmlId++) {

            /* Gets a reference to the file, READ_ONLY */
            //NOTE: I couldn't get IntelliJ to recognize resources with a relative path so I put my absolute path to XML's...
            //Change that to your own when you pull.
            RandomAccessFile aFile = new RandomAccessFile("/Users/JamesGlass/gir-wiki-subset/evaluation-set/"
                    + Integer.toString(xmlId) + ".xml", "r");
            FileChannel inChannel = aFile.getChannel();
            MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            buffer.load();
                System.out.println(xmlId);
            /* Have two buffers, retrieve string and save bytes */
		/*
		 * Splits articles Tokenize each, add number count Add Doc ID

        /*Gets an article from parsed xml,
         * Convert byte sequence into a linear seq of chars
         * Each article is our Doc
         * Tokenize it by splitting text by white spaces,  throw away punctuation
         * Delete stop words (use a stop list)
         * Normalize words, remove accents, lower case
         * Index the documents each word appears in
         * */

            int size = 0;
            for (int i = 0; i < buffer.limit(); i++) {
                byte b = buffer.get();

                // New word found
                if (b == 32 || b == 10) {
                    if (currentWordStart < i) {

                        String s = new String(Arrays.copyOfRange(byteBuffer, 0, size), StandardCharsets.UTF_8);
                        //System.out.printf("%s ", s);
                        size = 0;
                        Arrays.fill(byteBuffer, 0, byteBuffer.length, (byte) 0);

                        wordProcessor(s);
                        if (s.startsWith("<bdy>")) {
                            Document last = documents.get(documents.size() - 1);
                            last.setStartByte(i);
                        } else if (s.endsWith("</bdy>")) {
                            Document last = documents.get(documents.size() - 1);
                            //subtract the length of the string "</bdy>" because it's not part of text
                            last.setEndByte(i - size);
                        }
                    }
                } else {
                    byteBuffer[size++] = b;
                }
            }
            buffer.clear(); // do something with the data and clear/compact it.
            inChannel.close();
            aFile.close();

            for(Document doc : documents){
                if(doc.getXmlNumber() == 0){
                    doc.setXmlNumber(xmlId);
                }
                doc.setDevSet(true);
            }
        }

        /*
        If you want to see how does the posting list look at the end:
        put a breakpoint at one of these lines (red dot to the left) and then run in debug mode.
        Then open static variables and expand the postingList. :))
         */
        System.out.println();
        System.out.println();
        System.out.println();

        try
        {
            FileOutputStream fos =
                    new FileOutputStream("/Users/JamesGlass/hashmap.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(postingList);
            oos.close();
            fos.close();
            System.out.printf("Serialized HashMap data is saved in hashmap.ser");
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }


    private static void wordProcessor(String word) {

        /*
        TODO:
        Check if it's a stop word.
        */

        if(word.contains("<bdy")) {
            isBody = true;
        }
        else if(word.startsWith("</bdy>")) {
            isBody = false;
        }
        else if(word.startsWith("<id>")) {
            if(currId%3 == 0) {
                String pureId = word.replace("<id>", "").replace("</id>", "");
                long id = Long.parseLong(pureId);
                documents.get(documents.size()-1).setId(id);
            }
            currId++;
        }
        else if(word.startsWith("<title")) {
            isTitle = true;
            //This token will look like "<title>Federico" so we send the correct token to processing
            wordProcessor(word.substring("<title>".length(), word.length()));
        }
        else if(word.endsWith("</title>")) {
            //This token will look like "Lorca</title>" so we add only the relevant part to title
            int last = documents.size()-1;
            documents.get(last).setTitle(documents.get(last).getTitle()
                    + word.replace("</title", ""));
            isTitle = false;
        }
        else if(word.startsWith("<article")) {
            documents.add(new Document());
        }
        else if(word.endsWith("</article>")) {
        }

        //Check if word is a stop word or empty.
        else if(isBody) {
            word = normalize(word);

            if(word.isEmpty()) return;
            if(stopWords.contains(word)) return;

            addToIndex(word);
        }

        else if(isTitle){
            Document last = documents.get(documents.size()-1);
            if(last.getTitle() == null){
                last.setTitle(word + " ");
            } else{
                last.setTitle(last.getTitle() + word + " ");
            }
        }

    }


    private static String normalize(String word) {

        //Lowercase
        word = word.toLowerCase();

        //Punctuation
        word = word.replaceAll("[^a-zA-Z0-9]", "");

        //Stop word ==> moved upwards

        //Stem
        stemmer.add(word.toCharArray(), word.length());
        stemmer.stem();
        word = stemmer.toString();

        return word;
    }

    /**
     * Adds a stemmed word to inverted index / frequency dictionary / posting list.
     * **/
    private static void addToIndex(String word) {
        LinkedHashMap<Long, Integer> value;
        if(postingList.get(word) == null){
            value = new LinkedHashMap<>();
            value.put(documents.get(documents.size()-1).getId(), 1);
            postingList.put(word, value);
        } else{
            long id = documents.get(documents.size()-1).getId();
            value = postingList.get(word);
            if(value.get(id) == null){
                value.put(id, 1);
            } else{
                value.put(id, value.get(id)+1);
            }
        }




    }





/*
private static Tag checkType (String word) {
	switch(word) {
	case "<bdy>" : return Tag.BODY;
	case "<id>" : return Tag.ID;
	case "<title>": return Tag.TITLE;
	case "<article": return Tag.ARTICLE;
	case "</bdy>": return Tag.END_BODY;
	case "</id>": return Tag.END_ID;
	case "</title>": return Tag.END_TITLE;
	case "</article>": return Tag.END_ARTICLE;
	default: return Tag.OTHER;
	}
	}
	*/

}