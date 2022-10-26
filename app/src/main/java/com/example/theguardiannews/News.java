package com.example.theguardiannews;


/**
 * {@link News} represents a single News item.
 * Each object has 4 properties: title, section, author, and date
 */
public class News {

    // The section of that particular News item
    private String mSection;

    // The title of that News item
    private String mTitle;

    // The author of the News item
    private String mAuthor;

    // The date in which the News item was published
    private String mDate;

    // The link to the news article on the Guardian website
    private String mLink;

    /**
     * Create a new News object.
     * @param vSection is the section in which the News object belongs(eg. Technology)
     * @param vTitle is the title of the News object
     * @param vAuthor is the author of the News object
     * @param vDate is the date in which the News object was published
     * @param vLink is the link to the news article on the Guardian website.
     */
    public News(String vSection, String vTitle, String vAuthor, String vDate, String vLink){
        mSection = vSection;
        mTitle = vTitle;
        mAuthor = vAuthor;
        mDate = vDate;
        mLink = vLink;
    }

    /**
     * Get the section of the News item
     */
    public String getSection() {
        return mSection;
    }

    /**
     * Get the title of the News item
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get the author of the News item
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Get the date of the News item
     */
    public String getDate() {
        return mDate;
    }

    /**
     * Get the link of the News item
     */
    public String getLink() { return mLink;}


}
