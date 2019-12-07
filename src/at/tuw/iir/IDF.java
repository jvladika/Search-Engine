package at.tuw.iir;

import javax.print.Doc;
import java.util.*;

public class IDF {

    public static double TF_IDF (Document doc, Set<String> query) {

        double sum = 0,  termFreq = 0, IDF;

        for (String term : query) {

    /*TF: Term Frequency, which measures how frequently a term occurs in a document and its importance in
    the text by dividing it by the total number of words inside the document.*/
            /*raw count*/
            if(SearchEngine.postingList.get(term) == null) return sum;
            if(SearchEngine.postingList.get(term).get(doc.getId()) != null) {
                termFreq = (double) SearchEngine.postingList.get(term).get(doc.getId());

            /*IDF: Inverse Document Frequency, which measures how important a term is.
     However it is known that certain terms, such as "is", "of", and "that"may appear a lot of times but have little importance.
     Thus we need to weigh down the frequent terms while scale up the rare ones, by computing the following:
    IDF(t) = log_e(Total number of documents / Number of documents with term t in it).*/
                IDF = Math.log((double) SearchEngine.documents.size() / SearchEngine.postingList.get(term).size());
                sum += termFreq * IDF;
            }

        }
       // if(sum!=0) System.out.print(sum);
        return sum;
    }



    public static double TF_IDF2 (Document doc, Set<String> query) {

        double sum = 0,  termFreq = 0, IDF;


        for (String term : query) {

            if(SearchEngine.postingList.get(term) == null) return sum;

            Map<Long, Integer> freqs = SearchEngine.postingList.get(term);
            int wordSum = freqs.values().stream().reduce(0,(a, b) -> a + b);

    /*TF: Term Frequency, which measures how frequently a term occurs in a document and its importance in
    the text by dividing it by the total number of words inside the document.*/
            /*raw count*/

            if(SearchEngine.postingList.get(term).get(doc.getId()) != null) {
                 termFreq = (double) SearchEngine.postingList.get(term).get(doc.getId());

            /*IDF: Inverse Document Frequency, which measures how important a term is.
     However it is known that certain terms, such as "is", "of", and "that"may appear a lot of times but have little importance.
     Thus we need to weigh down the frequent terms while scale up the rare ones, by computing the following:
    IDF(t) = log_e(Total number of documents / Number of documents with term t in it).*/
                IDF = (double) Math.log((double)SearchEngine.documents.size() / (double)SearchEngine.postingList.get(term).size());
                sum += (double) termFreq/wordSum * IDF;
            }

        }
        // if(sum!=0) System.out.print(sum);
        return sum;
    }

}
