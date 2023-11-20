package org.spotify.services;

import java.io.IOException;
import java.util.*;

import org.spotify.exceptions.NotFoundException;
import org.spotify.model.Artist;

public class ArtistService {
    Map<UUID,Artist> artistById;

    public ArtistService(){
        this.artistById = new HashMap<>();
    }

    public boolean addArtistToDatabase(
            String name) throws  IllegalArgumentException {

        Artist artist = new Artist(name);
        //put method returns null if the key is not present in the map
        return addArtistToDatabase(artist);

    }


    public boolean addArtistToDatabase(Artist artist) throws IllegalArgumentException{
        if(artist==null){
            throw new IllegalArgumentException("Artist cannot be null");
        }
        return artistById.put(artist.getId(),artist)==null;
    }

    public Artist getArtistById(UUID id){
        return artistById.get(id);
    }

    public Optional<Artist> getArtistByName(String name){
        return artistById.values().stream()
                .filter(artist -> artist.getName().equalsIgnoreCase(name))
                .findFirst();
    }


    public boolean readArtistFromCSVFile(String path, String delimiter, FileService fileService) throws IOException,NotFoundException {
        List<Artist> artists = fileService.readArtistsFromCSV(path,delimiter);

        for (Artist artist : artists){
            this.artistById.put(artist.getId(),artist);
        }
        return true;
        //return addArtistsToDatabase(artists);

    }

    public boolean loadArtistFromCSV(String path, String delimiter, FileService fileService)
            throws IOException, NotFoundException {

        List<Artist> artists = fileService.readArtistsFromCSV(path,delimiter);

        return addArtistsToDatabase(artists);
    }

    private boolean addArtistsToDatabase(List<Artist> artists) {
        return artists.stream().allMatch(
                artist -> addArtistToDatabase(artist)
        );
    }


    public void saveArtistsToBinaryFileUsingTheEntireList(String path, FileService fileService) throws IOException {
        List<Artist> artists = new ArrayList<>(artistById.values());
        fileService.saveArtistToBinaryFileUsingTheEntireList(path, artists);
    }

    public Artist createArtist(String id, String name) {
        try {
            Artist artist = new Artist(id, name);
            boolean added = addArtistToDatabase(artist);
            if (added) {
                return artist;
            } else {
                throw new IllegalArgumentException("No se pudo agregar el artista a la base de datos.");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No se pudo crear el artista. Detalles: " + e.getMessage());
        }
    }

    // Método para actualizar un artista existente
    public boolean updateArtist(UUID id, String newName)throws IOException, NotFoundException, IllegalArgumentException {
        Artist artistToUpdate = artistById.get(id);

        if (artistToUpdate == null) {
            // El artista con el ID especificado no existe
            throw new NotFoundException("Error while deleting artistArtist with id " + id + " not found");
        }

        // Validar el nuevo nombre del artista
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("El nuevo nombre no puede ser nulo o vacío");
        }

        // Actualizar el nombre del artista
        artistToUpdate.setName(newName);

        // Guardar los cambios en el mapa
        artistById.put(id, artistToUpdate);

        return true;
    }

    public boolean deleteArtist(UUID artistId)throws IOException, NotFoundException, IllegalArgumentException{

        Artist artistToRemove = artistById.get(artistId);

        if (artistToRemove != null) {
            artistById.remove(artistId);
            return true; // Éxito al eliminar el artista
        } else {
            throw new IllegalArgumentException("El nuevo nombre no puede ser nulo o vacío");

        }

    }

    public void loadArtistToBinaryFileUsingTheEntireList(String filePath,
                                                         FileService fileService) throws IOException, ClassNotFoundException {

        List<Artist> artists =
                fileService.loadArtistFromBinaryFileUsingTheEntireList(filePath);
        clearDatabase();

        addArtistsToDatabase(artists);

    }
    protected void clearDatabase() {
        artistById.clear();
    }

    public void printAllArtists() {
        artistById.values().stream()
                .forEach(artist -> System.out.println(artist));
    }



}
