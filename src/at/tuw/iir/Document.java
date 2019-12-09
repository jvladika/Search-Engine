package at.tuw.iir;

import java.io.Serializable;

public class Document implements Serializable {

    private long id;
    private String title;
    private int startByte;
    private int endByte;

    private int xmlNumber;
    private boolean isDevSet;

    private int numWords;

    public Document(){
    }

    public Document(long id) {
        this.id = id;
        numWords = 0;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStartByte() {
        return startByte;
    }

    public void setStartByte(int startByte) {
        this.startByte = startByte;
    }

    public int getEndByte() {
        return endByte;
    }

    public void setEndByte(int endByte) {
        this.endByte = endByte;
    }

    public int getXmlNumber() {
        return xmlNumber;
    }

    public void setXmlNumber(int xmlNumber) {
        this.xmlNumber = xmlNumber;
    }

    public boolean isDevSet() {
        return isDevSet;
    }

    public void setDevSet(boolean devSet) {
        isDevSet = devSet;
    }

    public int getNumWords() {
        return numWords;
    }

    public void setNumWords(int numWords) {
        this.numWords = numWords;
    }
}
