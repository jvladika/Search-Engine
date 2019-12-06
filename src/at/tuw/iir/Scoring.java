package at.tuw.iir;

import java.util.Random;
import java.util.Set;

public class Scoring {

    public static double scoreBM25(Document doc, Set<String> query){
        double k1 = 1.5;
        double b = 0.75;
        int numDocuments = SearchEngine.documents.size();
        double avgdl = SearchEngine.avgDl();

        double sum = 0.0;
        for(String term : query){
            double idf = 1; //= scoreIdf(term);
            int termFreq = SearchEngine.postingList.get(term).get(doc);
            double denominator = termFreq * (k1 + 1);
            double numerator = termFreq + k1 * (1 - b + b * avgdl);

            sum += idf * denominator/numerator;
        }

        //return sum;
        Random r = new Random();
        return r.nextDouble();
    }
}
