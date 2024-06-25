package it.saimao.tmkkeyboard.model;

public class MaoItem {

    private int id;
    private String title, summary, sharedPreferenceName;
    private boolean isShowingCb;

    public int getId() {
        return id;
    }

    public String getSharedPreferenceName() {
        return sharedPreferenceName;
    }

    public void setSharedPreferenceName(String sharedPreferenceName) {
        this.sharedPreferenceName = sharedPreferenceName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MaoItem(int id, String title, String summary, boolean isChecked, String sharedPreferenceName) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.isShowingCb = isChecked;
        this.sharedPreferenceName = sharedPreferenceName;
    }

    public MaoItem(int id, String title, String summary, boolean isChecked) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.isShowingCb = isChecked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isShowingCb() {
        return isShowingCb;
    }

    public void setShowingCb(boolean showingCb) {
        isShowingCb = showingCb;
    }
}
