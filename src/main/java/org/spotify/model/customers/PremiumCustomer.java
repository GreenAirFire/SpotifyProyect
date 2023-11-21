package org.spotify.model.customers;

import org.spotify.exceptions.MaxSongsInPlayList;
import org.spotify.exceptions.NotFoundException;

import java.util.*;
import org.spotify.model.Playlist;

public class PremiumCustomer extends Customer{

    private List<Playlist> playlists;
    public PremiumCustomer(String id,
                           String username,
                           String password,
                           String name,
                           String lastname,
                           int age,
                           String[] artistIds){
        super(id,username,password,name,lastname,age,artistIds);
        this.playlists = new ArrayList<>();
    }


    public PremiumCustomer(String username,
                           String password,
                           String name,
                           String lastname,
                           int age){
        super(username,password,name,lastname,age);
        this.playlists = new ArrayList<>();
    }

    public PremiumCustomer(UUID id,
                           String username,
                           String password,
                           String name,
                           String lastname,
                           int age){
        super(id,username,password,name,lastname,age);
        this.playlists = new ArrayList<>();
    }

    public PremiumCustomer(UUID id,
                           String username,
                           String password,
                           String name,
                           String lastName,
                           int age,
                           Set<UUID> artistIdsFollowedSet){
        super(id,username,password,name,lastName,age,artistIdsFollowedSet);
        this.playlists = new ArrayList<>();
    }

    @Override
    public Optional<Playlist> getPlaylistById(UUID playlistId) {
        return playlists.stream()
                .filter(playlist -> playlist.getId().equals(playlistId))
                .findFirst();
    }

    public void addPlaylist(String namePlaylist) {
        Playlist playlist = new Playlist(namePlaylist);
        this.playlists.add(playlist); //Add the playlist to the list
    }

    public List<Playlist> getPlaylists() {
        return new ArrayList<>(playlists); // Return a copy of the playlist list to prevent external modification
    }


    public void addPlayLists(List<Playlist> playlists) {
        this.playlists.addAll(playlists);
    }


    public void removePlaylist(UUID playlistId){
        playlists.remove(getPlaylistById(playlistId));
    }

    public void addSongToPlaylist(UUID playlistId, UUID songId) throws MaxSongsInPlayList, NotFoundException {
        Optional<Playlist> optionalPlaylist = getPlaylistById(playlistId);

        if (optionalPlaylist.isEmpty()) {
            // La playlist no se encontró
            throw new NotFoundException("Playlist not found");
        }

        Playlist playlist = optionalPlaylist.get();
        playlist.addSongId(songId);

    }

    public void removeSongFromPlaylist(UUID playlistId, UUID songId) throws NotFoundException {
        Optional<Playlist> optionalPlaylist = getPlaylistById(playlistId);

        if (optionalPlaylist.isEmpty()) {
            // La playlist no se encontró
            throw new NotFoundException("Playlist not found");
        }

        Playlist playlist = optionalPlaylist.get();

        if (!playlist.getSongIdsList().remove(songId)) {
            throw new NotFoundException("Song not found in the playlist");
        }

    }


    public List<UUID> getSongsFromPlaylist(UUID playlistId) throws NotFoundException {
        Optional<Playlist> optionalPlaylist = getPlaylistById(playlistId);

        if (optionalPlaylist.isEmpty()) {
            // La playlist no se encontró
            throw new NotFoundException("Playlist not found");
        }

        Playlist playlist = optionalPlaylist.get();

        return playlist.getSongIdsList();
    }

    @Override
    public List<String> playlistToCSVLines(String delimiter) {
        return playlists.stream()
                .map(playlist -> playlist.toCSV(delimiter))
                .toList();
    }

    @Override
    public void addPlaylist(Playlist playlist) {
        this.playlists.add(playlist);
    }

    @Override
    public Optional<Playlist> updatePlaylist(String newName, UUID playlistId) {
        Optional<Playlist> optionalPlaylist = getPlaylistById(playlistId);

        if (optionalPlaylist.isEmpty()) {
            // La playlist no se encontró
            return Optional.empty();
        }

        Playlist playlist = optionalPlaylist.get();
        playlist.setName(newName);

        return Optional.of(playlist);
    }

    public String toCSV(String delimiter){
        return "Premium" + delimiter + super.toCSV(delimiter);
    }
}
