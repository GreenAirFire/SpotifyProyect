package org.spotify.services.enums;

public enum SongAttributesEnum {
    ID(0,"id"),
    NAME(1,"name"),
    ARTIST_IDS(2,"artistIds"),
    GENRE(3,"genre"),
    DURATION(4,"duration"),
    ALBUM(5,"album");
    private int index;

    private String header;

    private SongAttributesEnum(int index, String header) {
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
