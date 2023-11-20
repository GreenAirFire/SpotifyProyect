package org.spotify.model;
import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;
import java.util.UUID;

public class Song implements Serializable,Playable {


    private UUID id;
    private String name;
    private Set<UUID> artistIds; // A list to store associated artist
    private String genre;
    private int duration;
    private String album;


    // Constructor 1: Takes four parameters (overloaded)
    public Song(String id, String name, String genre, int duration, String album) {
        this.id = UUID.fromString(id);
        this.name = name;
        this.artistIds = new HashSet<>();
        this.genre = genre;
        this.duration = duration;
        this.album = album;
    }

    public Song(UUID id, String name, Set<UUID> artistIds, String genre, int duration, String album) {
        this.id = id;
        this.name = name;
        this.artistIds = artistIds;
        this.genre = genre;
        this.duration = duration;
        this.album = album;
    }

    public Song(UUID id, String name, String genre, int duration, String album) {
        this.id = id;
        this.name = name;
        this.artistIds = new HashSet<>();
        this.genre = genre;
        this.duration = duration;
        this.album = album;
    }


    public void addArtist(UUID artistID){
        artistIds.add(artistID);}
    public void  addArtists(Set<UUID> artistIds){this.artistIds.addAll(artistIds);}

    public void printIdsSinger() {
        artistIds.forEach(artistId -> System.out.println("Artist ID: " + artistId));
    }



    private void validateConstructor(String id, String name, String genre, int duration, String album) {

        validateId(id);
        validateName(name);
        validateGenre(genre);
        validateDuration(duration);
        validateAlbum(album);

    }

    private void validateDuration(int duration) {
        if( duration < 0) {
         throw new IllegalArgumentException("duration in seconds cannot be negative or zero");
         }
    }

    private void validateAlbum(String album) {
        if (album == null ) {
            throw new IllegalArgumentException("Genre cannot be null");
        }
        if(album.isEmpty()) {
            throw new IllegalArgumentException("Genre cannot be empty");
        }
    }


    private void validateGenre(String genre) {
        if (genre == null ) {
            throw new IllegalArgumentException("Genre cannot be null");
        }
        if(genre.isEmpty()) {
            throw new IllegalArgumentException("Genre cannot be empty");
        }
    }

    private void validateId(String id) {

        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
    }

    private void validateName(String name) {

        if (name == null ) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if(name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public UUID getID() {
        return id;
    }

    public Set<UUID> getArtistIds() {
        return new HashSet<>(artistIds) ;
    }






    @Override
    public String toString() {
        return "Song{" + "Song ID" + id +
            " name =" + name +
            ", genre =" + genre +
            ", duration =" + duration +
            ", album =" + album +
            '}';
    }

    public String toCSV(String delimiter){
        String[] artistIdsArray = this.artistIds.stream()
                .map(UUID::toString)
                .toArray(String[]::new);
        String artistIds = "{" + String.join(",",artistIdsArray) + "}";
        return id + delimiter + name + delimiter + artistIds + delimiter + genre + delimiter + duration + delimiter + album;
    }

    @Override
    public String play() {
        return "Playing song: "+ name;
    }
}
