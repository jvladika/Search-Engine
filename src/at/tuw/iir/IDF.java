package at.tuw.iir;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class IDF {

    public static double TF_IDF (Document doc, Set<String> query) {

        double sum = 0,  termFreq = 0, IDF;

        for (String term : query) {

    /*TF: Term Frequency, which measures how frequently a term occurs in a document.*/
            /*raw count*/
            if(SearchEngine.postingList.get(term) == null) return sum;
            if(SearchEngine.postingList.get(term).get(doc.getId()) != null) { /*TODO: never goes inside*/
                termFreq = SearchEngine.postingList.get(term).get(doc.getId());

            /*IDF: Inverse Document Frequency, which measures how important a term is.
     However it is known that certain terms, such as "is", "of", and "that"may appear a lot of times but have little importance.
     Thus we need to weigh down the frequent terms while scale up the rare ones, by computing the following:
    IDF(t) = log_e(Total number of documents / Number of documents with term t in it).*/
                IDF = Math.log(SearchEngine.documents.size() / SearchEngine.postingList.get(term).size());
                sum += termFreq * IDF;
            }

        }
       // if(sum!=0) System.out.print(sum);
        return sum;
    }
}
