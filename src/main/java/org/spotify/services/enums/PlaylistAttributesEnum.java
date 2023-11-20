package org.spotify.services.enums;

public enum PlaylistAttributesEnum {
    ID(0,"id"),
    NAME(1,"name"),
    CUSTOMER_ID(2,"customerId"),
    SONG_IDS(3,"songsIds");

    private int index;

    private String header;

    private PlaylistAttributesEnum(int index, String header) {
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
