package org.spotify.services;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.spotify.model.Artist;
import org.spotify.model.Customer;
import org.spotify.model.Playlist;
import org.spotify.model.Song;
import org.spotify.services.enums.*;

public class FileService {

    public static final String COMMA_DELIMITER = ",";
    public static final String OPEN_CURLY_BRACE = "{";
    public static final String CLOSE_CURLY_BRACE = "}";
    public static final String EMPTY_STRING = "";

    /*Method that reads a csv file of songs with following structure
     * id,name,artist_ids,genre,duration,album
     * */

    public void saveCustomerToBinaryFileUsingTheEntireList(String path, List<Customer> customerList) throws IOException{

        // Create a file object representing the binary file to be written.
        File file = new File(path);

        //try with resources will close the file automatically
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)){
            // Write the list of animals to the file.
            oos.writeObject(customerList);
        }
    }

    public List<Customer> loadCustomersFromBinaryFileUsingTheEntireList(String path)
            throws IOException, ClassNotFoundException {

        // Create a file object representing the binary file to be read.
        File file = new File(path);

        //Here we are not using try with resources
        try(FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            // Read the list of animals from the file.
            return (ArrayList<Customer>) ois.readObject();

        }
    }


    public List<Customer> readCustomersFromCSV(String path, String delimiter) throws IOException {

        File file = new File(path);

        // Reference: https://funnelgarden.com/java_read_file/#1b_FilesreadAllLines_Explicit_Encoding
        List<String> lines =
                Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        List<Customer> customers = new ArrayList<>();

        // Iterate through each line in the CSV file and parse animal data.
        for (String line : lines) {
            // Split the line into values using the specified delimiter.
            String[] values = line.split(delimiter);

            // Extract customer data from the CSV line.
            String type =
                    values[CustomerAttributesEnum.TYPE.getIndex()];

            // Create a customer object and add it to the customers list.

            CustomerTypesEnum customerType = CustomerTypesEnum.fromString(type);

            // Extract customer data from the CSV line.
            Customer customer = getCustomerFromCSVLine(values);

            customers.add(customer);


        }
        return customers;
    }

    private Customer getCustomerFromCSVLine(String[] values) {

        CustomerTypesEnum customerType = CustomerTypesEnum.fromString(
                values[CustomerAttributesEnum.TYPE.getIndex()]);

        String id = values[CustomerAttributesEnum.ID.getIndex()];
        String username = values[CustomerAttributesEnum.USERNAME.getIndex()];
        String password = values[CustomerAttributesEnum.PASSWORD.getIndex()];
        String name = values[CustomerAttributesEnum.NAME.getIndex()];
        String lastName = values[CustomerAttributesEnum.LAST_NAME.getIndex()];
        int age = Integer.valueOf(values[CustomerAttributesEnum.AGE.getIndex()]);
        String artistFollowedIds = values[CustomerAttributesEnum.ARTIST_FOLLOWED_IDS.getIndex()];

        String[] artistIdsArray = extractArrayFromValue(artistFollowedIds);

        return Customer.getCustomerFromType(customerType,id, username, password, name, lastName, age, artistIdsArray);
    }

    private String[] extractArrayFromValue(String value){
        String setString = extractElementsBetweenCurlyBraces(value);

        return splitAndDeleteSpaces(setString, COMMA_DELIMITER);

    }

    private String extractElementsBetweenCurlyBraces(String setString) {

        return setString
                // Remove the curly braces from the string
                .replace(OPEN_CURLY_BRACE, EMPTY_STRING)
                .replace(CLOSE_CURLY_BRACE, EMPTY_STRING);
    }

    private String[] splitAndDeleteSpaces(String stringToSplit, String delimiter) {

        return Stream.of(stringToSplit.split(delimiter))
                .map(String::trim)
                .toArray(String[]::new);
    }

    public List<Playlist> loadPlaylistsFromCSV(String path, String delimiter) throws IOException {

        File file = new File(path);

        // Reference: https://funnelgarden.com/java_read_file/#1b_FilesreadAllLines_Explicit_Encoding
        List<String> lines =
                Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        List<Playlist> playlists = new ArrayList<>();

        // Iterate through each line in the CSV file and parse animal data.
        for (String line : lines) {
            // Split the line into values using the specified delimiter.
            String[] values = line.split(delimiter);

            String id = values[PlaylistAttributesEnum.ID.getIndex()];
            String name = values[PlaylistAttributesEnum.NAME.getIndex()];
            String customerId = values[PlaylistAttributesEnum.CUSTOMER_ID.getIndex()];
            String songIds = values[PlaylistAttributesEnum.SONG_IDS.getIndex()];

            String[] songIdsArray = extractArrayFromValue(songIds);


            Playlist playlist = new Playlist(id,name,customerId,songIdsArray);

            playlists.add(playlist);


        }
        return playlists;
    }

    public Map<UUID, List<Playlist>> readPlayListFromCSV(String path, String delimiter)
            throws IOException {


        File file = new File(path);
        // Reference: https://funnelgarden.com/java_read_file/#1b_FilesreadAllLines_Explicit_Encoding
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        Map<UUID,List<Playlist>> playListByCustomerId = new HashMap<>();


        for(String line : lines) {
            String[] values = line.split(delimiter);

            //Map.Entry<UUID,Playlist> playlistEntry = getPlayListFromCSVLine(values);
            String id = values[PlaylistAttributesEnum.ID.getIndex()];
            String name = values[PlaylistAttributesEnum.NAME.getIndex()];
            String customerId = values[PlaylistAttributesEnum.CUSTOMER_ID.getIndex()];
            String songIds = values[PlaylistAttributesEnum.SONG_IDS.getIndex()];

            String[] songIdsArray = getArrayFromStringSet(songIds);

            List<UUID> songIdsList = Stream.of(songIdsArray)
                    .map(UUID::fromString)
                    .collect(Collectors.toList());


            if(!playListByCustomerId.containsKey(customerId)) {
                playListByCustomerId.put(UUID.fromString(customerId), new ArrayList<>());

            }

            Playlist playlist = new Playlist(id,name, songIdsList);

            playListByCustomerId.get(UUID.fromString(customerId)).add(playlist);
            //List<Playlist> customerPlayList = playListByCustomerId.get(playlistEntry.getKey());

            //customerPlayList.add(playlistEntry.getValue());

            //playListByCustomerId.put(playlistEntry.getKey(),customerPlayList);

        }

        return playListByCustomerId;


    }

    public Map<UUID, List<Playlist>> readPlayListFromCSVUsingStreams(String path, String delimiter)
            throws IOException {

        return Files.lines(new File(path).toPath())
                .map(line -> line.split(delimiter))
                .map(this::getPlayListFromCSVLine)
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }

    private Map.Entry<UUID,Playlist> getPlayListFromCSVLine(String[] values) {

        String id = values[PlaylistAttributesEnum.ID.getIndex()];
        String name = values[PlaylistAttributesEnum.NAME.getIndex()];
        String customerId = values[PlaylistAttributesEnum.CUSTOMER_ID.getIndex()];
        String songIds = values[PlaylistAttributesEnum.SONG_IDS.getIndex()];

        String[] songIdsArray = getArrayFromStringSet(songIds);

        List<UUID> songIdsList = Stream.of(songIdsArray)
                .map(UUID::fromString)
                .collect(Collectors.toList());

        Playlist playlist = new Playlist(UUID.fromString(id),name, songIdsList);

        return Map.entry(UUID.fromString(customerId),playlist);

    }

    private String[] getArrayFromStringSet(String valueFromCSV) {

        String valueWithoutCurlyBraces = extractElementsBetweenCurlyBraces(valueFromCSV);
        return splitAndDeleteSpaces(valueWithoutCurlyBraces, COMMA_DELIMITER);

    }

    public List<Artist> readArtistsFromCSV(String path, String delimiter) throws IOException {

        File file = new File(path);

        // Reference: https://funnelgarden.com/java_read_file/#1b_FilesreadAllLines_Explicit_Encoding
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        List<Artist> artists = new ArrayList<>();

        // Iterate through each line in the CSV file and parse animal data.
        for (String line : lines) {
            // Split the line into values using the specified delimiter.
            String[] values = line.split(delimiter);

            // Extract song data from the CSV line.
            Artist artist = getArtistFromCSVLine(values);

            artists.add(artist);


        }
        return artists;
    }

    public List<Artist> loadArtistFromBinaryFileUsingTheEntireList(String filePath)
            throws IOException, ClassNotFoundException {

        File file = new File(filePath);

        //Here we are not using try with resources
        try(FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            // Read the list of animals from the file.
            return (ArrayList<Artist>) ois.readObject();

        }

    }

    public List<Song> readSongsFromCSV(String path, String delimiter) throws IOException {

        File file = new File(path);

        // Reference: https://funnelgarden.com/java_read_file/#1b_FilesreadAllLines_Explicit_Encoding
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

        List<Song> songList = new ArrayList<>();

        // Iterate through each line in the CSV file and parse animal data.
        for (String line : lines) {
            // Split the line into values using the specified delimiter.
            String[] values = line.split(delimiter);

            // Extract song data from the CSV line.
            Song song = getSongFromCSVLine(values);

            songList.add(song);


        }
        return songList;
    }

    public List<Song> readSongsFromCSVUsingStreams(String path, String delimiter)
            throws IOException {
        return Files.lines(new File(path).toPath())
                .map(line -> line.split(delimiter))
                .map(this::getSongFromCSVLine)
                .collect(Collectors.toList());
    }

    private Song getSongFromCSVLine(String[] values) {
        // Extract song data from the CSV line.
        String id = values[SongAttributesEnum.ID.getIndex()];
        String name = values[SongAttributesEnum.NAME.getIndex()];
        String artistsIdsValueFromCSV = values[SongAttributesEnum.ARTIST_IDS.getIndex()];
        String genre = values[SongAttributesEnum.GENRE.getIndex()];
        int duration = Integer.parseInt(values[SongAttributesEnum.DURATION.getIndex()]);
        String album = values[SongAttributesEnum.ALBUM.getIndex()];

        // Create a song without the artist ids
        Song song = new Song(UUID.fromString(id), name, genre, duration, album);

        // Extract artist IDs from the CSV line.
        // ids in the CSV file: {id1,id2,id3}
        String artistsIds =
                extractElementsBetweenCurlyBraces(artistsIdsValueFromCSV);

        String[] artistsIdsArray = splitAndDeleteSpaces(artistsIds, COMMA_DELIMITER);


        //add the artist ids to the song
        for (String ownerId : artistsIdsArray) {
            song.addArtist(UUID.fromString(ownerId));
        }

        Set<UUID> uuidSet = new HashSet<>();

        for (String ownerId : artistsIdsArray) {
            uuidSet.add(UUID.fromString(ownerId));
        }

        //Alternative

        Set<UUID> artistIds = Stream.of(artistsIdsArray)
                .map(UUID::fromString)
                .collect(Collectors.toSet());

        //We are using the constructor that takes a set of artist ids
        Song alternativeSong = new Song(UUID.fromString(id),
                name,
                artistIds,
                genre,
                duration,
                album);

        return song;
    }



    public List<Artist> readArtistsFromCSVUsingStreams(String path, String delimiter)
            throws IOException {
        return Files.lines(new File(path).toPath())
                .map(line -> line.split(delimiter))
                .map(this::getArtistFromCSVLine)
                .collect(Collectors.toList());
    }

    private Artist getArtistFromCSVLine(String[] values) {

        String id = values[ArtistAttributesEnum.ID.getIndex()];
        String name = values[ArtistAttributesEnum.ARTIST_NAME.getIndex()];

        Artist artist = new Artist(UUID.fromString(id), name);

        return artist;


    }

    public List<Customer> readCustomersFromCSVUsingStreams(String path, String delimiter)
            throws IOException {
        return Files.lines(new File(path).toPath())
                .map(line -> line.split(delimiter))
                .map(this::getCustomerFromCSVLine)
                .collect(Collectors.toList());
    }





    public List<Customer> readCustomersWithPlayListsFromCSV(
            String customersPath,
            String delimiter,
            String playlistsPath) throws IOException {

        File customersFile = new File(customersPath);
        List<String> linesFromCustomers = Files.readAllLines(customersFile.toPath(), StandardCharsets.UTF_8);

        Map<UUID, List<Playlist>> playlistsByCustomerId = readPlayListFromCSV(playlistsPath, delimiter);

        List<Customer> customers = new ArrayList<>();

        for(String line: linesFromCustomers) {
            String[] values = line.split(delimiter);
            Customer newCustomer = getCustomerFromCSVLine(values);

            if(playlistsByCustomerId.containsKey(newCustomer.getId())) {
                List<Playlist> playlistsAdd = playlistsByCustomerId.get(newCustomer.getId());
                newCustomer.addPlayLists(playlistsAdd);
            }
            customers.add(newCustomer);

        }

        return customers;

    }

    public List<Customer> readCustomersWithPlayListsFromCSVUsingStreams(
            String customersPath,
            String delimiter,
            String playlistsPath) throws IOException {

        Map<UUID, List<Playlist>> playlistsByCustomerId = readPlayListFromCSVUsingStreams(playlistsPath, delimiter);

        return Files.lines(new File(customersPath).toPath())
                .map(line -> line.split(delimiter))
                .map(this::getCustomerFromCSVLine)
                .peek(customer -> {
                    if(playlistsByCustomerId.containsKey(customer.getId())) {
                        List<Playlist> playlistsAdd = playlistsByCustomerId.get(customer.getId());
                        customer.addPlayLists(playlistsAdd);
                    }
                })
                .collect(Collectors.toList());
    }


    public void saveArtistToBinaryFileUsingTheEntireList(String path, List<Artist> artists)throws IOException {
        // Create a file object representing the binary file to be written.
        File file = new File(path);

        //try with resources will close the file automatically
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)){
            // Write the list of animals to the file.
            oos.writeObject(artists);
        }
    }

    public void saveSongsToBinaryFileUsingTheEntireList(String path, List<Song> songList)throws IOException {
        // Create a file object representing the binary file to be written.
        File file = new File(path);

        //try with resources will close the file automatically
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)){
            // Write the list of animals to the file.
            oos.writeObject(songList);
        }
    }


    public List<Song> loadSongsFromBinaryFileUsingTheEntireList(String path) throws IOException, ClassNotFoundException {

        // Create a file object representing the binary file to be read.
        File file = new File(path);

        //Here we are not using try with resources
        try(FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            // Read the list of animals from the file.
            return (ArrayList<Song>) ois.readObject();

        }
    }

    public void writeTextFile(String path,
                              List<String> linesToWrite)
            throws IOException {

        //https://www.baeldung.com/java-write-to-file
        File file = new File(path);

        Files.write(file.toPath(), linesToWrite, StandardCharsets.UTF_8);
    }

}
