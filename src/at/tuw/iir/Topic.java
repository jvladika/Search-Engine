package at.tuw.iir;

public class Topic {

    private long topicId;
    private String title;
    private String phraseTitle;
    private String description;
    private String narrative;

    public Topic(){
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhraseTitle() {
        return phraseTitle;
    }

    public void setPhraseTitle(String phraseTitle) {
        this.phraseTitle = phraseTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

}
