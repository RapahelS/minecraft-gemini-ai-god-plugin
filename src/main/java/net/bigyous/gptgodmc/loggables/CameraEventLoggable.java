package net.bigyous.gptgodmc.loggables;

public class CameraEventLoggable extends BaseLoggable {

    // who took the photo?
    private String photoArtist;
    private String subjectName;
    private String description;
    private boolean isItUgly;

    public CameraEventLoggable(String subjectName, String description, boolean isItUgly, String photoTakenBy) {
        super();
        this.subjectName = subjectName;
        this.description = description;
        this.isItUgly = isItUgly;
        this.photoArtist = photoTakenBy;
    }

    public CameraEventLoggable(String subjectName, String description, boolean isItUgly) {
        this(subjectName, description, isItUgly, "God");
    }

    @Override
    public String getLog() {
        String uglyOrPretty = isItUgly ? "dislikes" : "likes";
        return String.format("God %s %s's picture of %s. God says: %s", uglyOrPretty, photoArtist, subjectName, description);
    }
}
