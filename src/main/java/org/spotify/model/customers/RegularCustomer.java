package org.spotify.model.customers;

import org.spotify.exceptions.MaxSongsInPlayList;
import org.spotify.exceptions.NotFoundException;
import org.spotify.exceptions.UnsupportedOperationException;

import java.util.*;
import org.spotify.model.Playlist;

public class RegularCustomer extends Customer {

    private static final int BOUND = 1000;  // Límite para el número aleatorio
    private static final String DEFAULT_PLAYLIST_NAME = "DefaultPlaylist";

    private Playlist playlist;

    public RegularCustomer(String id,
                           String username,
                           String password,
                           String name,
                           String lastname,
                           int age,
                           String[] artistIds){
        super(id,username,password,name,lastname,age,artistIds);
        this.playlist = new Playlist(DEFAULT_PLAYLIST_NAME + new Random().nextInt(BOUND));
    }


    public RegularCustomer(String username,
                           String password,
                           String name,
                           String lastname,
                           int age){
        super(username,password,name,lastname,age);
        int random = new Random().nextInt(BOUND);
        this.playlist = new Playlist(DEFAULT_PLAYLIST_NAME + random);

    }

    public RegularCustomer(UUID id,
                           String username,
                           String password,
                           String name,
                           String lastname,
                           int age){
        super(id,username,password,name,lastname,age);
        int random = new Random().nextInt(BOUND);
        this.playlist = new Playlist(DEFAULT_PLAYLIST_NAME + random);

    }

    public RegularCustomer(UUID id,
                           String username,
                           String password,
                           String name,
                           String lastName,
                           int age,
                           Set<UUID> artistIdsFollowedSet){
        super(id,username,password,name,lastName,age,artistIdsFollowedSet);
        this.playlist = new Playlist(DEFAULT_PLAYLIST_NAME + new Random().nextInt(BOUND));
    }



    @Override
    public Optional<Playlist> getPlaylistById(UUID playlistId) {

        if (playlist == null || !playlist.getId().equals(playlistId)) {
            // La playlist no se encontró
            return Optional.empty();
        }

        return Optional.of(playlist);
    }


    public void addPlaylist(String namePlaylist) {
        /*debe recibir el nombre de la nueva playlist y lanzar una excepción UnsupportedOperationException.
        Esta es una excepción de java, por lo que no debe crearla.
        En esta excepción se debe incluir un mensaje que indique que el usuario no puede agregar playlist
        porque es usuario regular*/



    }


    public List<Playlist> getPlaylists() {
        /*debe retornar una lista de un elemento con la única playlist disponible. Usa List.of*/
        return null;
    }


    public void addPlayLists(List<Playlist> playlists) throws UnsupportedOperationException {
        /*debe lanzar una excepción si la lista de playlists tiene más de un elemento o ninguno.
        Si es válida se cambiará la playlist existente*/



    }

    public void addPlaylist(Playlist playlist) {
        /*debe lanzar una excepción si la lista de playlists tiene más de un elemento o ninguno.
        Si es válida se cambiará la playlist existente*/


    }

    @Override
    public Optional<Playlist> updatePlaylist(String newName, UUID playlistId) {
        if (playlist == null || !playlist.getId().equals(playlistId)) {
            // La playlist no se encontró
            return Optional.empty();
        }

        playlist.setName(newName);

        return Optional.of(playlist);
    }

    public void removePlaylist(UUID playlistId) {
        /*debe lanzar una excepción, ya que no se le permitirá al usuario eliminar la playlist que tiene*/

    }


    public void removeSongFromPlaylist(UUID playlistId, UUID songId) throws NotFoundException, MaxSongsInPlayList {
        /*Este método debe eliminar una canción de la playlist del usuario, debe lanzar una excepción
        si la playlist no corresponde con la del usuario o si la canción no existe dentro de la playlist.*/



        if (playlist == null || !playlist.getId().equals(playlistId)) {
            // La playlist no se encontró
            throw new NotFoundException("Playlist not found");
        }

        playlist.removeSongId(songId);

    }


    public List<UUID> getSongsFromPlaylist(UUID playlistId) throws NotFoundException {

        if (playlist == null || !playlist.getId().equals(playlistId)) {
            // La playlist no se encontró
            throw new NotFoundException("Playlist not found");
        }

        return playlist.getSongIdsList();
    }

    @Override
    public List<String> playlistToCSVLines(String delimiter) {

        return List.of(playlist.toCSV(delimiter));
    }


    public void addSongToPlaylist(UUID playlistId, UUID songId) throws MaxSongsInPlayList {
        /*Debe lanzar la excepcipon MaxSongsInPlaylist si la playlist tiene 10 canciones
        (a este tipo de usuario no se le permitirá agregar más de diez canciones).
         En caso contrario, solo se agregará la canción si el id que se envía
         a la función coincide con el de la playlist que tiene creada.*/


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
