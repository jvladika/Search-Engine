package at.tuw.iir;


import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SearchEngine {

    /** Tells if body of a document is currently being read. **/
    private static boolean isBody;

    /** Tells if title of a document is currently being read. **/
    private static boolean isTitle;

    /** Current highest ID that was read. **/
    private static int currId = 0;

    /** Porter Stemmer used for stemming tokens. **/
    public static Stemmer stemmer = new Stemmer();

    /** NLTK's list of 128 stop words. **/
    public static HashSet<String> stopWords = new HashSet<String>(Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"
    ));

    /** Inverted index for posting list.
     * Key is a term and it is mapped to a linked list of documents.
     * Value is a linked hash list where each entry is a pair (document, frequency);
     * i.e. each entry says in which document and how many times the term appears.
     * **/
    public static Map<String, LinkedHashMap<Long, Integer>> postingList = new HashMap<>();

    public static List<Document> documents = new ArrayList<>();


    public static void main(String[] args) throws IOException {
       indexData();
        run();
    }

    private static void run() throws IOException {
        Scanner sc = new Scanner(System.in);

        while(true){
            System.out.print("Write your query: ");
            while(!sc.hasNext()){
            }
            String line = sc.nextLine();
            processQuery(line);
        }

    }

    private static void processQuery(String line) throws IOException {
        String[] args = line.split(" ");
        Set<String> query = new HashSet<>(Arrays.asList(args));
        System.out.println(query);
        Set<String> stemmedQuery = new HashSet<>();

        for(String term : query){
            stemmedQuery.add(normalize(term));
        }

        Map<Document, Double> scores = new HashMap<>();
        for(Document doc : documents){
            double score = IDF.TF_IDF(doc, stemmedQuery); /*IDF calculation*/
            scores.put(doc, score);
        }

        scores = sortByValue(scores);
        int it = 0;
        for(Map.Entry<Document, Double> entry : scores.entrySet()){
            if(it == 10) break;

            Document doc = entry.getKey();
         //  System.out.println(doc.getId() + " " + doc.getTitle());
            printDoc(doc);

            it++;
        }
    }

    private static void printDoc(Document doc) throws IOException {
        int xml = doc.getXmlNumber();
        int start = doc.getStartByte();
        int end = doc.getEndByte();
        String title = doc.getTitle();

        FileInputStream fis = new FileInputStream("/Users/JamesGlass/gir-wiki-subset/evaluation-set/"
                + Integer.toString(xml) + ".xml");

        byte[] bytes = new byte[2000];
        fis.read(bytes, start, 800);


        System.out.println("TITLE:    " +title);
        System.out.println(new String(bytes, StandardCharsets.UTF_8));

    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.<K,V>comparingByValue().reversed());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private static void indexData() throws IOException{
        char[] wordBuffer = new char[100];
        byte[] byteBuffer = new byte[10000];
        int currentWordStart = 0;

        /*
        TODO:
        I have tried it only for dev set so far and evaluation set, which is way larger.
        It seemed to work okay for dev set...
         */

       // int iteration = 0;
      //  OuterWhile: while(true) {
            postingList = new HashMap<>();

            for (int xmlId =  1; xmlId <= 30 ; xmlId++) {


                if (xmlId == 554) {
                    //break OuterWhile;
                }
                System.out.println(xmlId);

                /* Gets a reference to the file, READ_ONLY */
                //NOTE: I couldn't get IntelliJ to recognize resources with a relative path so I put my absolute path to XML's...
                //Change that to your own when you pull.
                RandomAccessFile aFile = new RandomAccessFile("/Users/JamesGlass/gir-wiki-subset/evaluation-set/"
                        + Integer.toString(xmlId) + ".xml", "r");
                FileChannel inChannel = aFile.getChannel();
                MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
                buffer.load();

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

                            //increment count of words;
                            if(documents.size() != 0) {
                                Document last1 = documents.get(documents.size() - 1);
                                last1.setNumWords(last1.getNumWords() + 1);
                            }
                            String s = new String(Arrays.copyOfRange(byteBuffer, 0, size), StandardCharsets.UTF_8);
                            //System.out.printf("%s ", s);
                            wordProcessor(s);
                            size = 0;
                            Arrays.fill(byteBuffer, 0, byteBuffer.length, (byte) 0);

                            //wordProcessor(s);
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

                for (Document doc : documents) {
                    if (doc.getXmlNumber() == 0) {
                        doc.setXmlNumber(xmlId);
                    }
                    doc.setDevSet(true);
                }

            }

       //     serialize("postinglist" + Integer.toString(iteration + 1) + ".ser");

        //    iteration++;
      //  }

     //   serialize("postinglist" + Integer.toString(iteration + 1) + ".ser");

        /*
        If you want to see how does the posting list look at the end:
        put a breakpoint at one of these lines (red dot to the left) and then run in debug mode.
        Then open static variables and expand the postingList. :))
         */
        System.out.println();
        System.out.println();
        System.out.println();
    }

    private static void serialize(String fileName){
        try {
            FileOutputStream fos =
                    new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(postingList);
            oos.close();
            fos.close();
            System.out.println("Serialized HashMap data is saved in postinglist.ser");
        } catch (IOException ioe) {
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
        LinkedHashMap<Long, Integer> value = postingList.get(word);

        if(value == null){
            value = new LinkedHashMap<>();
            value.put(documents.get(documents.size()-1).getId(), 1);
            postingList.put(word, value);
        } else{
            long id = documents.get(documents.size()-1).getId();
            if(value.get(id) == null){
                value.put(id, 1);
            } else{
                value.put(id, value.get(id)+1);
            }

        }
    }

    public static double avgDl() {
        return 0;
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
