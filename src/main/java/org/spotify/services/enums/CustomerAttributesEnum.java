package org.spotify.services.enums;

public enum CustomerAttributesEnum {
    TYPE(0, "type"),
    ID(1,"id"),
    USERNAME(2,"username"),
    PASSWORD(3,"password"),
    NAME(4,"name"),
    LAST_NAME(5,"lastname"),
    AGE(6,"age"),
    ARTIST_FOLLOWED_IDS(7, "artistIdsFollowedSet");

    private int index;

    private String header;

    CustomerAttributesEnum(int index, String header) {
        this.index = index;
        this.header = header;

    }

    public static CustomerAttributesEnum fromString(String value){
        for (CustomerAttributesEnum t : CustomerAttributesEnum.values()){
            if (t.header.equalsIgnoreCase(value)){
                return t;
            }
        }
        return null;
    }
    public int getIndex() {
        return index;
    }

    public String getHeader(){ return header; }
}
