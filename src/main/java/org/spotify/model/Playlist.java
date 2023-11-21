package org.spotify.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Playlist implements Serializable {

    // Attributes of the Playlist class
    private UUID id;
    private String name;
    private UUID customerId;
    private List<UUID> songIdsList; // A list to store associated songs


    // Constants
    private static final String DEFAULT_NAME = "No nombre"; // Default name for an animal

    // Constructors

    // Constructor with UUID, name, and list songs provided (Constructor Overloading)
    // This constructor allows creating a Playlist with a specific UUID, name, and list songs.
    // It's useful when you want to initialize a Playlist with known attributes.
    public Playlist(String id, String name, String customerId) {
        validateConstructor(id, name); // Validate the provided parameters

        this.id = UUID.fromString(id);
        this.name = name;
        this.customerId = UUID.fromString(customerId);
        this.songIdsList = new ArrayList<>();


        /***for (String songId : songIdsList) {
            this.songIdsList.add(UUID.fromString(songId));
        }***/
    }

    public Playlist(String id, String name, String customerId, String[] songsIds){
        this.id = UUID.fromString(id);
        this.name = name;
        this.songIdsList = new ArrayList<>();
        for (String songId : songsIds){
            this.songIdsList.add(UUID.fromString(songId));
        }
    }
    public Playlist(UUID id, String name, List<UUID> songIdsList) {
        this.id = id;
        this.name = name;
        this.songIdsList = songIdsList;
    }

    public Playlist(String id, String name, List<UUID> songIdsList) {
        this.id = UUID.fromString(id);
        this.name = name;
        this.songIdsList = songIdsList;
    }

    public Playlist( String name, List<UUID> songIdsList) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.songIdsList = songIdsList;
    }

    public Playlist(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.songIdsList = new ArrayList<>();
    }

    private void validateConstructor(String id, String name) {

        validateId(id); // Validate the provided UUID
        validateName(name); // Validate the provided name

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



    // Constructor with random UUID, name, and age provided (Constructor Overloading)
    // This constructor generates a random UUID and allows setting the name and age.
    // It's useful when you want to create an Playlist with random attributes.





    // Default constructor with random UUID, default name, and minimum age (Constructor Overloading)
    // This constructor sets default values for name and age and generates a random UUID.
    // It's useful when you want to create a generic Playlist with default attributes.


    public List<UUID> getSongIdsList() {
        return new ArrayList<>(songIdsList) ;
    }

    public UUID getCustomerId(){
        return this.customerId;
    }

    public String getName() {
        return this.name;
    }


    public void setName(String name) {

        validateName(name); // Validate the provided name
        this.name = name;
    }


    // Getter method to retrieve the UUID of the animal
    public UUID getId() {return id;
    }

    public void setId(String id) {
        validateId(id);
        this.id = UUID.fromString(id);
    }



    public void addSongId(UUID song) {
        songIdsList.add(song);
    }

    public void removeSongId(UUID song){
        songIdsList.remove(song);
    }

    // Override the toString() method to provide a formatted string representation of the Playlist object
    @Override
    public String toString() {
        return "id: " + id + " nombre: '" + name ;
    }

    public String toCSV(String delimiter) {
        String songIds = getSongIdsAsString();
        return id + delimiter + name + delimiter + songIds ;
    }
    public String getSongIdsAsString(){
        String[] songIdsArray = this.songIdsList.stream()
                .map(UUID::toString)
                .toArray(String[]::new);

        return "{" + String.join(",",songIdsArray ) + "}";
    }


}
