package org.spotify.services.enums;

public enum ArtistAttributesEnum {
    ID(0,"id"),
    ARTIST_NAME(1,"name");

    private int index;

    private String header;

    private ArtistAttributesEnum(int index, String header) {
        this.index = index;
        this.header = header;

    }

    public String getHeader() {
        return header;
    }

    public int getIndex() {
        return index;
    }
}
