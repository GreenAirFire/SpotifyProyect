package org.spotify.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Artist implements Serializable {

    private UUID id;
    private String name;

    //Constructor 1: takes 2 parameters
    public Artist(String id, String name){
        validateConstructor(id, name);

        this.id = UUID.fromString(id);
        this.name = name;
    }
    //Constructor 2: takes 2 parameters
    public Artist(UUID id, String name){
        validateName(name);

        this.id = id;
        this.name = name;
    }

    public Artist(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    private void validateConstructor(String id, String name) {

        validateId(id);
        validateName(name);

    }

    private void validateName(String name) {
        // Step 1: Check if the provided name is null
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }

        // Step 2: Check if the provided name is empty
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    private void validateId(String id) {
        // Step 1: Check if the provided id is null
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        // Step 2: Check if the provided id is empty
        if (id.isEmpty()) {
            throw new IllegalArgumentException("Id cannot be empty");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public String toString() {
        return "Artist{" + " id: " + id + " nombre: '" + name + "}" ;
    }
}
