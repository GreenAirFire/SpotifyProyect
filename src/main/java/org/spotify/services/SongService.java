package org.spotify.services;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.spotify.exceptions.NotFoundException;
import org.spotify.model.Customer;
import org.spotify.model.Song;
import org.spotify.model.Playlist;

public class SongService {

    private List<Song> songList ;

    public SongService() {

        this.songList = new ArrayList<>();

    }
    public boolean loadSongFromCSVFile(String path, String delimiter, FileService fileService)throws IOException, NotFoundException {
        List<Song> songs = fileService.readSongsFromCSV(path,delimiter);
        return songList.addAll(songs);
    }

    public void saveSongsToBinaryFileUsingTheEntireList(String path, FileService fileService) throws IOException {
        fileService.saveSongsToBinaryFileUsingTheEntireList(path, songList);
    }

    public void loadSongsToBinaryFileUsingTheEntireList(String path, FileService fileService) throws IOException, ClassNotFoundException{
        List<Song> songs =
                fileService.loadSongsFromBinaryFileUsingTheEntireList(path);
        clearSongList();
        songList.addAll(songs);
    }
    private void clearSongList() {
        songList.clear();
    }

    public void addSongToDatabase(Song newSong) {
        songList.add(newSong);

    }

    public void printAllSongs() {
        songList.stream()
                .forEach(song -> System.out.println(song));
    }

    public Optional<Song> getSongById(UUID idSong) {
        return songList.stream()
                .filter(song -> song.getID().equals(idSong))
                .findFirst(); // Returns an Optional<Customer>

    }
    public Song getSongByIdd(UUID songId) {
        return songList.stream()
                .filter(song -> song.getID().equals(songId))
                .findFirst()
                .orElse(null); // Return null if the song is not found
    }

    public boolean updateSong(String newName, UUID idSong) throws IOException, NotFoundException, IllegalArgumentException{
        Optional<Song> optionalSong = getSongById(idSong);
        if (!optionalSong.isPresent()) {
            System.out.println("Song not found");
            throw new NotFoundException("Error while detecting song "+ " not found");
        }


        // Validar el nuevo nombre del song
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("El nuevo nombre no puede ser nulo o vacío");
        }

        Song songToUpdate = optionalSong.get(); // Get the customer if it exists
        System.out.println("Song found: " + songToUpdate);

        songToUpdate.setName(newName);
        return true;
    }

    public void printIDandNameSongOnly(){
        songList.stream()
                .map(song -> "Song ID: " + song.getID() + ", Name: " + song.getName())
                .forEach(System.out::println);
    }


    public List<Song> getSongsFilteredBy(Set<Integer> searchCriterias, String searchTerm) {
        return songList.stream()
                .filter(song -> meetsSearchCriteria(song, searchCriterias, searchTerm))
                .collect(Collectors.toList());
    }

    private boolean meetsSearchCriteria(Song song, Set<Integer> searchCriterias, String searchTerm) {
        if (searchCriterias.isEmpty()) {
            return true; // If no criteria are selected, include all songs
        }

        return searchCriterias.stream().allMatch(criteria -> meetsCriteria(song, criteria, searchTerm));
    }

    private boolean meetsCriteria(Song song, Integer criteria, String searchTerm) {
        switch (criteria) {
            case 1: // Name
                return song.getName().toLowerCase().contains(searchTerm.toLowerCase());
            case 2: // Genre
                return song.getGenre().toLowerCase().contains(searchTerm.toLowerCase());
            case 3: // Artist Ids
                return song.getArtistIds().stream()
                        .anyMatch(artistId -> artistId.toString().toLowerCase().contains(searchTerm.toLowerCase()));
            case 4: // Album
                return song.getAlbum().toLowerCase().contains(searchTerm.toLowerCase());
            default:
                throw new IllegalArgumentException("Invalid search criteria: " + criteria);
        }
    }

    public boolean deleteSong(UUID idSongToRemove) throws IOException,NotFoundException,IllegalArgumentException{
        Optional<Song> optionalSong = getSongById(idSongToRemove);
        if (!optionalSong.isPresent()) {
            System.out.println("Song not found");
            throw new NotFoundException("Error while detecting song "+ " not found");
        }
        Song songToRemove = optionalSong.get(); // Get the customer if it exists
        songList.remove(songToRemove);
        return true;
    }

    public boolean deleteSongFromPlaylists(UUID songId, CustomerService customerService) throws IOException, NotFoundException {
        boolean songDeleted = false;

        // Iterate through all customers
        for (Customer customer : customerService.getAllCustomers()) {
            // Iterate through the playlists of each customer
            for (Playlist playlist : customer.getPlaylists()) {
                // Remove the song from the playlist if it exists
                if (playlist.getSongIdsList().remove(songId)) {
                    songDeleted = true;
                }
            }
        }

        // After updating all playlists, delete the song from the main song list
        songDeleted = deleteSong(songId) || songDeleted;

        return songDeleted;
    }

    /***public List<Song> listSongsBy(Set<SongAttributesEnum> genre, String genre1) {

    }

    public Optional<Song> getSongById(UUID songId) {

    }***/
}
