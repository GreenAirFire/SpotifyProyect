package org.spotify.model;

import org.spotify.exceptions.MaxSongsInPlayList;
import org.spotify.exceptions.NotFoundException;
import org.spotify.exceptions.UnsupportedOperationException;

import java.util.*;

public class Premium extends Customer{
    public Premium(String id,
                   String username,
                   String password,
                   String name,
                   String lastname,
                   int age,
                   String[] artistIds){
        super(id,username,password,name,lastname,age,artistIds);
    }


    public Premium(String username,
                   String password,
                   String name,
                   String lastname,
                   int age){
        super(username,password,name,lastname,age);
    }

    public Premium(UUID id,
                   String username,
                   String password,
                   String name,
                   String lastname,
                   int age){
        super(id,username,password,name,lastname,age);
    }

    public Premium(UUID id,
                   String username,
                   String password,
                   String name,
                   String lastName,
                   int age,
                   Set<UUID> artistIdsFollowedSet){
        super(id,username,password,name,lastName,age,artistIdsFollowedSet);
    }

    public void addPlaylist(String namePlaylist) {
        Playlist playlist = new Playlist(namePlaylist);
        this.playlists.add(playlist); //Add the playlist to the list
    }

    public List<Playlist> getPlaylists(){
        return new ArrayList<>(playlists); // Return a copy of the playlist list to prevent external modification
    }


    public void addPlayLists(List<Playlist> playlists) {
        this.playlists.addAll(playlists);
    }

    public void addPlaylistt(Playlist playlist){
    this.playlists.add(playlist);}

    public void removePlaylist(UUID playlistId){
        playlists.remove(getPlaylistById(playlistId));
    }

    public void addSongToPlaylist(UUID playlistId, UUID songId) throws MaxSongsInPlayList, NotFoundException {
        Playlist playlist = getPlaylistById(playlistId).get();
        if (playlist == null) {
            // La playlist no se encontró
            throw new NotFoundException("Playlist not found");
        }
        playlist.addSongId(songId);

    }

    public void removeSongFromPlaylist(UUID playlistId, UUID songId) throws NotFoundException {
        Playlist playlist = getPlaylistById(playlistId).get();
        if (playlist == null) {
            // La playlist no se encontró
            throw new NotFoundException("Playlist not found");
        }

        if (playlist.getSongIdsList().remove(songId)) {
            // Song was found and removed successfully
        } else {
            // If remove() returns false, the song was not found
            throw new NotFoundException("Song not found in the playlist");
        }
    }


    public List<UUID> getSongsFromPlaylist(UUID playlistId) throws NotFoundException {
        Optional<Playlist> matchingPlaylist = getPlaylists().stream()
                .filter(playlist -> playlist.getId().equals(playlistId))
                .findFirst();

        return matchingPlaylist
                .map(Playlist::getSongIdsList)
                .orElseThrow(() -> new NotFoundException("Invalid playlist ID for the Premium user."));
    }

    public String toCSV(String delimiter){
        return "Premium" + delimiter + super.toCSV(delimiter);
    }
}
