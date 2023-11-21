package org.spotify.model;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import org.spotify.exceptions.MaxSongsInPlayList;
import org.spotify.exceptions.NotFoundException;
import org.spotify.exceptions.UnsupportedOperationException;
import org.spotify.services.enums.CustomerTypesEnum;

public abstract class Customer implements Serializable {
    private static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$";
    private final static int MINIMUM_AGE = 18; // Minimum allowed age
    private static final String USERNAME_PATTERN = "^[a-zA-Z][a-zA-Z0-9_]{7,30}$";

    // Attributes of the Customer class
    protected UUID id;
    protected String username;
    protected String password;
    protected String name;
    protected String lastname;
    protected int age;
    protected Set<UUID> artistIdsFollowedSet;



    public Customer(String id,
                    String username,
                    String password,
                    String name,
                    String lastname,
                    int age,
                    String[] artistIds) {

        validateConstructor(
            id,
            username,
            password,
            name,
            lastname,
            age);

        this.id = UUID.fromString(id); // Generate a unique ID for the owner
        this.username = username;
        this.password = password;
        this.name = name;
        this.lastname = lastname;
        this.age = age;
        this.artistIdsFollowedSet = new HashSet<>();
        for (String artistId : artistIds){
            this.artistIdsFollowedSet.add(UUID.fromString(artistId));
        }
        //this.playlists = new ArrayList<>();
    }

    public Customer(String username,
                    String password,
                    String name,
                    String lastname,
                    int age) {

        validateConstructor(
                username,
                password,
                name,
                lastname,
                age);

        this.id = UUID.randomUUID(); // Generate a unique ID for the owner
        this.username = username;
        this.password = password;
        this.name = name;
        this.lastname = lastname;
        this.age = age;
        this.artistIdsFollowedSet = new HashSet<>();

    }
    public Customer(UUID id,
                    String username,
                    String password,
                    String name,
                    String lastname,
                    int age) {

        validateConstructor(
                username,
                password,
                name,
                lastname,
                age);

        this.id = id; // Generate a unique ID for the owner
        this.username = username;
        this.password = password;
        this.name = name;
        this.lastname = lastname;
        this.age = age;
        this.artistIdsFollowedSet = new HashSet<>();

    }

    public Customer(UUID id, String username,
                    String password,
                    String name,
                    String lastName,
                    int age,
                    Set<UUID> artistIdsFollowedSet) {
        this.id = id; // Generate a unique ID for the owner
        this.username = username;
        this.password = password;
        this.name = name;
        this.lastname = lastName;
        this.age = age;
        this.artistIdsFollowedSet = artistIdsFollowedSet;

    }

    public void addArtistIdFollowed(UUID artistId){
        artistIdsFollowedSet.add(artistId);
    }



    private void validateConstructor(String id,
                                     String username,
                                     String password,
                                     String name,
                                     String lastname,
                                     int age) {

        validateId(id); // Validate the provided UUID
        validateConstructor(
                username,
                password,
                name,
                lastname,
                age);


    }

    /***public static Animal getAnimalFromType(AnimalTypesEnum animalType,
                                           String id,
                                           String name,
                                           int age,
                                           String breed,
                                           String[] ownerIdsArray) {
        return switch (animalType) {
            case CAT -> new Cat(id, name, age, ownerIdsArray);
            case DOG -> new Dog(id, name, age, breed, ownerIdsArray);
            case PUPPY -> new Puppy(id, name, age, breed, ownerIdsArray);
        };

    }***/

    private void validateConstructor(String username, String password,
                                     String name,
                                     String lastname,
                                     int age) {

        validateName(name); // Validate the provided name
        validateName(lastname); // Validate the provided name
        validateUsername(username); // Validate the provided username
        validatePassword(password); // Validate the provided password
        validateAge(age); // Validate the provided age

    }

    private void validateAge(int age) {
        // Step 1: Check if the provided age is less than the minimum allowed age
        if (age < MINIMUM_AGE) {
            throw new IllegalArgumentException("Age cannot be less than " + MINIMUM_AGE);
        }
    }


    private void validateUsername(String username) {
        // Step 1: Check if the provided username is null
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }

        // Step 2: Check if the provided username is empty
        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        // Step 3: Check if the provided username is in the correct format
        if (!username.matches(USERNAME_PATTERN)) {
            throw new IllegalArgumentException("Username must be in the appropriate format");
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

    private void validatePassword(String password) {
        // Step 1: Check if the provided password is null
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        // Step 2: Check if the provided password is empty
        if (password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Step 3: Check if the provided password is less than 8 characters and includes at least one number and one letter and one special character and one uppercase letter
        /***if (!password.matches(PASSWORD_PATTERN)) {
            throw new IllegalArgumentException(
                """
                    At least one upper case English letter
                    At least one lower case English letter
                    At least one digit
                    At least one special character or space from the following: #?!@$ %^&*-
                    Minimum eight in length
                    """);
        }***/
    }


    public static Customer getCustomerFromType(CustomerTypesEnum customerType,
                                               String id,
                                               String username,
                                               String password,
                                               String name,
                                               String lastname,
                                               int age,
                                               String[] artistIds
                                               ){
        return switch (customerType){
            case PREMIUM -> new Premium(id, username, password, name, lastname, age, artistIds);
            case REGULAR -> new Regular(id, username, password, name, lastname, age, artistIds);
        };
    }

    public void addArtistId(UUID artist){artistIdsFollowedSet.add(artist);}

    public Set<UUID> getArtistIds() {
        return new HashSet<>(artistIdsFollowedSet);
    }






    //Abstract methods

    public abstract boolean addPlaylists(List<Playlist> playlists);

    public abstract Optional<Playlist> getPlaylistById(UUID playlistId);

    public abstract void addPlaylist(String namePlaylist)throws UnsupportedOperationException;

    public abstract List<Playlist> getPlaylists();

    public abstract void addPlayLists(List<Playlist> playlists);
    //return this.playlists.addAll(playlists);

    public abstract void removePlaylist(UUID playlistId);

    public abstract void addSongToPlaylist(UUID playlistId, UUID songId) throws MaxSongsInPlayList, NotFoundException;

    public abstract void removeSongFromPlaylist(UUID playlistId, UUID songId)throws NotFoundException;

    public abstract List<UUID> getSongsFromPlaylist(UUID playlistId)throws NotFoundException;

    //return new ArrayList<>(playlists); // Return a copy of the playlist list to prevent external modification


    /**public void addPlaylist(String name, String id){
        Playlist playlist = new Playlist(name,id);
        this.playlists.add(playlist); //Add the playlist to the list
    }**/
     public abstract void addPlaylist(Playlist playlist);
        //this.playlists.add(playlist);

    //Imprimir
    public void printArtistIdsFollowedSet() {
        artistIdsFollowedSet.forEach(artistId -> System.out.println("Artist ID: " + artistId));
    }

    public  void printPlaylist(){
        playlists.forEach(playlist -> System.out.println("Playlist: "+playlist));
    }

    //Sobreescribir
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", name='" + name + '\'' +
            ", lastname='" + lastname + '\'' +
            ", age=" + age +
            '}';
    }


    public String toCSV(String delimiter) {
        String[] artistIdsArray = this.artistIdsFollowedSet.stream()
            .map(UUID::toString)
            .toArray(String[]::new);
        String artistIds = "{" + String.join(",",artistIdsArray ) + "}";
        return
            id + delimiter +
            username + delimiter +
            password + delimiter +
            name + delimiter +
            lastname + delimiter +
            age + delimiter +
            artistIds ;
    }

    public List<String> playlistToCSVLines(String delimiter) {
        return playlists.stream()
                .map(playlist -> {
                    String playlistId = playlist.getId().toString();
                    String playlistName = playlist.getName();
                    String userId = id.toString();
                    String songIds = playlist.getSongIdsList().stream()
                            .map(UUID::toString)
                            .collect(Collectors.joining(","));

                    return playlistId + delimiter + playlistName + delimiter + userId  + delimiter + "{" + songIds + "}";
                })
                .collect(Collectors.toList());

    }


    // Getters and setters for the Customer class attributes


    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }
    public int getAge() {
        return age;
    }

    public void setUsername(String username) {
        validateUsername(username);
        this.username = username;
    }


    public void setPassword(String password) {
        validatePassword(password);
        this.password = password;
    }


    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setId(String id) {
        validateId(id);
        this.id = UUID.fromString(id);
    }

    public void setAge(int age) {
        validateAge(age);
        this.age = age;
    }
}
