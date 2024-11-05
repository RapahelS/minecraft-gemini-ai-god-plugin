package net.bigyous.gptgodmc.loggables;

public class CameraEventLoggable extends BaseLoggable {

    // who took the photo?
    private String photoArtist;
    private String structureName;
    private String description;
    private boolean isItUgly;

    public CameraEventLoggable(String structureName, String description, boolean isItUgly, String photoTakenBy) {
        super();
        this.structureName = structureName;
        this.description = description;
        this.isItUgly = isItUgly;
        this.photoArtist = photoTakenBy;
    }

    public CameraEventLoggable(String structureName, String description, boolean isItUgly) {
        this(structureName, description, isItUgly, "God");
    }

    @Override
    public String getLog() {
        String uglyOrPretty = isItUgly ? "It is ugly" : "It is pretty";
        return String.format("%s took a photo of %s and god thinks this about it: %s. %s.", photoArtist, structureName, uglyOrPretty, description);
    }
}
