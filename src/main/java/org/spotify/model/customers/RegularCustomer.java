package org.spotify.model;

import org.spotify.exceptions.MaxSongsInPlayList;
import org.spotify.exceptions.NotFoundException;
import org.spotify.exceptions.UnsupportedOperationException;

import java.util.*;

public class Regular extends Customer {

    private static final int BOUND = 1000;  // Límite para el número aleatorio
    private static final String DEFAULT_PLAYLIST_NAME = "DefaultPlaylist";


    public Regular(String id,
                   String username,
                   String password,
                   String name,
                   String lastname,
                   int age,
                   String[] artistIds){
        super(id,username,password,name,lastname,age,artistIds);
    }


    public Regular(String username,
                   String password,
                   String name,
                   String lastname,
                   int age){
        super(username,password,name,lastname,age);
        int random = new Random().nextInt(BOUND);
        Playlist defaultPlaylist = new Playlist(DEFAULT_PLAYLIST_NAME + random);
        playlists.add(defaultPlaylist);
    }

    public Regular(UUID id,
                   String username,
                   String password,
                   String name,
                   String lastname,
                   int age){
        super(id,username,password,name,lastname,age);
        int random = new Random().nextInt(BOUND);
        Playlist defaultPlaylist = new Playlist(DEFAULT_PLAYLIST_NAME + random);
        playlists.add(defaultPlaylist);
    }

    public Regular(UUID id,
                   String username,
                   String password,
                   String name,
                   String lastName,
                   int age,
                   Set<UUID> artistIdsFollowedSet){
        super(id,username,password,name,lastName,age,artistIdsFollowedSet);
    }



    public void addPlaylist(String namePlaylist) {

        if (this.playlists.size() == 0) {
            Playlist playlist = new Playlist(namePlaylist);
            this.playlists.add(playlist); //Add the playlist to the list}
            if (this.playlists.size() >= 1) {
                try {
                    throw new UnsupportedOperationException("Error while adding playlist " + " due to Regular user");
                } catch (UnsupportedOperationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public List<Playlist> getPlaylists() {
        return new ArrayList<>(playlists); // Return a copy of the playlist list to prevent external modification
    }


    public void addPlayLists(List<Playlist> playlists) {
        this.playlists.addAll(playlists);
    }

    public void addPlaylistt(Playlist playlist) {
        this.playlists.add(playlist);
    }

    public void removePlaylist(UUID playlistId) {
        playlists.remove(getPlaylistById(playlistId));
    }


    public void removeSongFromPlaylist(UUID playlistId, UUID songId) throws NotFoundException {
        Playlist playlist = getPlaylistById(playlistId).get();

        if (playlist == null) {
            // La playlist no se encontró
            throw new NotFoundException("Playlist not found");
        }

        if (!getPlaylists().contains(playlist)) {
            throw new NotFoundException("Playlist not found for the user");
        }

        if (playlist.getSongIdsList().remove(songId)) {
            // Song was found and removed successfully
        } else {
            // If remove() returns false, the song was not found
            throw new NotFoundException("Song not found in the playlist");
        }
    }


    public List<UUID> getSongsFromPlaylist(UUID playlistId) throws NotFoundException {
        if (!getPlaylists().isEmpty()) {
            Playlist userPlaylist = getPlaylists().get(0); //There is only one playlist for a Regular user
            if (userPlaylist.getId().equals(playlistId)) {
                return userPlaylist.getSongIdsList();
            } else {
                throw new NotFoundException("Invalid playlist ID for the Regular user.");
            }
        } else {
            throw new NotFoundException("Regular user does not have any playlists.");
        }
    }


    public void addSongToPlaylist(UUID playlistId, UUID songId) throws MaxSongsInPlayList {
        Playlist playlist = getPlaylistById(playlistId).get();
        if (playlist.getSongIdsList().size() < 10) {
            playlist.addSongId(songId);
        } else {
            throw new MaxSongsInPlayList("The maximum is 10 songs for regular user");
        }

    }

    public String toCSV(String delimiter){

        return "Regular" + delimiter + super.toCSV(delimiter);
    }


}
