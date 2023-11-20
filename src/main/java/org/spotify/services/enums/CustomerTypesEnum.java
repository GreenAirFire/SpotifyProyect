package org.spotify.services.enums;

public enum CustomerTypesEnum {
    PREMIUM("Premium"),
    REGULAR("Regular");

    private String type;

    CustomerTypesEnum(String type){ this.type = type; }

    public String getType() { return type; }

    public static CustomerTypesEnum fromString(String text){
        for (CustomerTypesEnum c : CustomerTypesEnum.values()){
            if(c.type.equalsIgnoreCase(text)){
                return c;
            }
        }
        return null;
    }
}
