package at.tuw.iir;

import java.util.Set;

public class IDF {

    public static double TF_IDF (Document doc, Set<String> query) {

        int sum = 0,  termFreq, IDF;

        for (String term : query) {

    /*TF: Term Frequency, which measures how frequently a term occurs in a document.*/
            /*raw count*/
           //  termFreq = postingList.get(term).get(doc);

            /*IDF: Inverse Document Frequency, which measures how important a term is.
     However it is known that certain terms, such as "is", "of", and "that"may appear a lot of times but have little importance.
     Thus we need to weigh down the frequent terms while scale up the rare ones, by computing the following:
    IDF(t) = log_e(Total number of documents / Number of documents with term t in it).*/
           //  IDF = Math.log(SearchEngine.documents.size() / postingsList.get(term).size());

           //  sum += termFreq * IDF;


        }
        return sum;
    }
}
