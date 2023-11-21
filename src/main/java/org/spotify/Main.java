package org.spotify;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.spotify.exceptions.ExitMethodException;
import org.spotify.exceptions.NotFoundException;
import org.spotify.exceptions.UserNameAlreadyTakenException;
import org.spotify.model.*;
import org.spotify.model.customers.Customer;
import org.spotify.services.ArtistService;
import org.spotify.services.CustomerService;
import org.spotify.services.FileService;
import org.spotify.services.SongService;
public class Main {
    private static final String DEFAULT_DELIMITER = ";";
    private static final String YES = "y";
    private static final String ARTIST_CSV_DEFAULT_PATH = "src/main/resources/artists.csv";
    private static final String ARTIST_BIN_DEFAULT_PATH = "src/main/resources/artists.bin";
    private static final String ARTIST_CSV_REPORT_DEFAULT_PATH = "src/main/resources/artist_report.csv";

    private static final String CUSTOMERS_CSV_DEFAULT_PATH = "src/main/resources/customers_2.csv";
    private static final String CUSTOMERS_BIN_DEFAULT_PATH = "src/main/resources/customers.bin";
    private static final String PLAYLISTS_CSV_DEFAULT_PATH = "src/main/resources/playLists.csv";
    private static final String SONGS_CSV_DEFAULT_PATH = "src/main/resources/songs_2.csv";
    private static final String SONGS_BIN_DEFAULT_PATH = "src/main/resources/songs.bin";
    public static final String DO_YOU_WANT_TO_SAVE_TO_BINARY_FILE_TEMPLATE =
            "Do you want to save %s to binary file? (y/n)";
    public static final String PLEASE_ENTER_THE_PATH_OF_THE_CSV_FILE = "Please enter the path of the CSV file";

    public static void main(String[] args) {

        // Initialize a scanner for user input and an AnimalService instance
        Scanner scanner = new Scanner(System.in);
        ArtistService artistService = new ArtistService();
        CustomerService customerService = new CustomerService();
        SongService songService = new SongService();
        FileService fileService = new FileService();


        // Display a welcome message and present a menu to the user
        System.out.println("Welcome to the Spotify app");
        int option = -1;
        do {

            printMenu();

            // Read the user's choice

            try {
                option = scanner.nextInt();
                //Reference: https://docs.oracle.com/javase/7/docs/api/java/util/Scanner.html#nextLine()
                //Reference: https://www.freecodecamp.org/news/java-scanner-nextline-call-gets-skipped-solved/
                scanner.nextLine();



                // Perform actions based on the user's choice using a switch statement
                //Reference: https://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html
                //Reference: https://medium.com/@javatechie/the-evolution-of-switch-statement-from-java-7-to-java-17-4b5eee8d29b7
                switch(option) {
                    case 0 -> loadProgramStateFromBinaryFiles(scanner,customerService,artistService,songService,fileService);
                    case 1 -> addCustomerToDatabase(scanner,customerService);
                    case 2 -> editCustomer(scanner,customerService);
                    case 3 -> editArtistFollowedByCustomer(scanner,customerService,artistService);
                    case 4 -> removeUser(scanner,customerService);
                    case 5 -> addSong(scanner,songService);
                    case 6 -> editSong(scanner,songService);
                    case 7 -> listSongByKey(scanner,songService);
                    case 8 -> removeSong(scanner,songService,customerService);
                    case 9 -> CreateNewArtist( scanner,  artistService);
                    case 10 -> EditArtist( scanner, artistService);
                    case 11 -> RemoveArtist(scanner,artistService);
                    case 12 -> listArtistPerGenre(scanner,customerService,songService,artistService);
                    case 13 -> printReportArtistsWithFollowers(artistService, customerService);
                    case 14 -> printReportArtistPopularity(artistService,customerService);
                    case 15 -> printReportSongsPrsent(scanner,customerService,songService,artistService);
                    case 16 -> addPlaylist(scanner,customerService);
                    case 17 -> editPlaylist(scanner,customerService);

                    case 18 -> loadCustomerAndPlaylistFromCSV(scanner, customerService, fileService);
                    case 19 -> loadArtistFromCSV(scanner, artistService, fileService);
                    case 20 -> loadSongFromCSV(scanner, songService, fileService);
                    case 21 -> saveStateToBinaryFile(scanner, customerService, artistService, songService, fileService);
                    case 22 -> printTypeCustomers(scanner,customerService,fileService);

                    case 23 -> System.out.println("Exiting the program");

                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid option, please enter a number");
                scanner.nextLine(); // Consume the newline character
            } catch (ExitMethodException e) {
                System.out.println("Going back to main menu");
            }
        } while(option != 23); // Continue looping until the user selects option 8 (Exit)

        // Close the scanner when done
        scanner.close();
    }


    private static void printCustomers(CustomerService customerService) {
        customerService.printAllCustomers();
    }

    private static void printMenu() {
        //Reference: https://docs.oracle.com/en/java/javase/17/text-blocks/index.html
        System.out.println("""
                    Home:
                    0. Cargar progreso desde archivo binario
                    
                    1. Crear usuario
                    2. Editar usuario existente
                    3. Editar artistas seguidos de usuario existente
                    4. Eliminar un usuario existente con el username
                                        
                    5. Agregar nueva cancion a lista de canciones
                    6. Editar una canción existente de acuerdo a su nombre
                    7. Listar canciones que contengan la palabra clave
                    8. Eliminar una cancion existente de acuedo con un id
                    
                    9. Agregar nuevo artista
                    10. Modificar un artista   
                    11. Eliminar un artista existente   
                    12. Listar artistas por genero
                    
                    13. Generar reporte de seguidores de los artistas
                    14. Generar reporte de popularidad de artistas
                    15. Generar reporte canciones presentes en todas las playlist
                    
                    16. Agregar playlist 
                    17. Editar playlist existente 
                    
                    18. load customers and playlist from CSV
                    19. load artist from CSV
                    20. load songs from CSV
                    21. save program state to binary file
                    22. print premium or regular customer
                    
                    23. Exit
                    
                    Please enter your option
                    """);
    }


    //===========CASE 0 - LOAD PROGRAM STATE FROM BINARY FILE===========

    private static void loadProgramStateFromBinaryFiles(Scanner scanner,
                                                        CustomerService customerService,
                                                        ArtistService artistService,
                                                        SongService songService,
                                                        FileService fileService) throws ExitMethodException {

        System.out.println("""
              Do you want to load state from binary file? (y/n)
              THIS OPERATION WILL OVERWRITE THE CURRENT LIST OF ARTIST, CUSTOMERS AND SONGS 
              """);
        String loadState = scanner.nextLine();

        if(!loadState.equalsIgnoreCase(YES)) {
            throw new ExitMethodException();
        }

        loadSongsFromBinaryFile(scanner, songService, fileService);

        loadCustomersFromBinaryFile(scanner, customerService, fileService);

        loadArtistFromBinaryFile(scanner, artistService, fileService);

    }

    private static void loadCustomersFromBinaryFile(Scanner scanner,
                                                    CustomerService customerService,
                                                    FileService fileService) {
        System.out.println("Please enter the path of the customers binary file");
        String path = getPath(scanner, CUSTOMERS_BIN_DEFAULT_PATH);

        try{
            // Attempt to load customers from the specified binary file
            customerService.loadCustomersToBinaryFileUsingTheEntireList(path,fileService);

            System.out.println("Customers loaded successfully");

        } /*We can catch multiple exceptions using | */
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading Customers due to error: " + e.getMessage());
        }
    }

    private static void loadArtistFromBinaryFile(Scanner scanner,
                                                    ArtistService artistService,
                                                    FileService fileService) {
        System.out.println("Please enter the path of the customers binary file");
        String path = getPath(scanner, ARTIST_BIN_DEFAULT_PATH);

        try{
            // Attempt to load customers from the specified binary file
            artistService.loadArtistToBinaryFileUsingTheEntireList(path,fileService);

            System.out.println("Artist loaded successfully");

        } /*We can catch multiple exceptions using | */
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading Customers due to error: " + e.getMessage());
        }
    }

    private static void loadSongsFromBinaryFile(Scanner scanner,
                                                SongService songService,
                                                FileService fileService) {

        System.out.println("Please enter the path of the owners binary file");
        String path = getPath(scanner, SONGS_BIN_DEFAULT_PATH);

        try{
            // Attempt to load owners from the specified binary file
            songService.loadSongsToBinaryFileUsingTheEntireList(path, fileService);

            System.out.println("Songs loaded successfully");

        } /*We can catch multiple exceptions using | */
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading owners due to error: " + e.getMessage());
        }
    }


            //===========CASE 1 - CREATE A USER===========
    private static void addCustomerToDatabase(Scanner scanner, CustomerService customerService)throws ExitMethodException{
        try {
            System.out.println("Please enter the username ");
            String username = scanner.nextLine();
            System.out.println("Please enter the password ");
            String password = scanner.nextLine();
            System.out.println("Please enter the name ");
            String name = scanner.nextLine();
            System.out.println("Please enter the lastname ");
            String lastname = scanner.nextLine();
            System.out.println("Please enter the age, remember that minimum age is 18");
            int age = Integer.valueOf(scanner.nextLine());

            customerService.addCustomerToDatabase(username,password,name,lastname,age);

        } catch (IllegalArgumentException | UserNameAlreadyTakenException e) {
        System.out.println("Error creating owner due to :" + e.getMessage());
        }
        printCustomers(customerService);
    }

    //CASE 2 - EDIT A EXISTING USER
    private static void editCustomer (Scanner scanner, CustomerService customerService)throws ExitMethodException{
        try{
            System.out.println("Please enter the username");
            String username = scanner.nextLine();
            System.out.println("Please enter the correct name");
            String namu = scanner.nextLine();
            String newName = namu;
            boolean modificated = customerService.updateUser(newName,username);

            if (modificated) {
                System.out.println("customer actualizado con éxito");
            } else {
                System.out.println("El customer con el ID especificado no existe");
            }
        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading customer due to error: " + e.getMessage());

        }
        customerService.printAllCustomers();
    }

    //===========CASE 2 - ADD OWNER TO DATABASE===========




    //===========CASE 3 - EDIT ARTIST TO EXISTING CUSTOMER===========

    private static void editArtistFollowedByCustomer(Scanner scanner,CustomerService customerService,ArtistService artistService)throws ExitMethodException{
        try{
            System.out.println("Please enter the username");
            String username = scanner.nextLine();

            artistService.printAllArtists();

            System.out.println("Please enter the id of the artist to follow");
            String id = scanner.nextLine();
            UUID uid = UUID.fromString(id);

            boolean added = customerService.followNewArtist(username, uid);

            if (added) {
                System.out.println("Artista añadido con éxito.");

            } else {
                System.out.println("No se encontró un artista con el ID especificado.");
            }
        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading Artist due to error: " + e.getMessage());

        }

    };

    //===========CASE 4 - REMOVE USER BY USERNAME============
    private static void removeUser(Scanner scanner, CustomerService customerService)throws ExitMethodException{
        try{
            System.out.println("Please enter the username");
            String username = scanner.nextLine();

            boolean deleted = customerService.deleteUser(username);

            if (deleted) {
                System.out.println("Artista eliminado con éxito.");
            } else {
                System.out.println("No se encontró un artista con el ID especificado.");
            }


        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading Artist due to error: " + e.getMessage());

        }
        customerService.printAllCustomers();
    }



    //===========CASE 5 - ADD SONG TO DATABASE===========
    private static void addSong(Scanner scanner, SongService songService)throws ExitMethodException{
        try {
            UUID uid = UUID.randomUUID();
            String id = uid.toString();
            System.out.println("Please enter the name of the song");
            String name = scanner.nextLine();
            System.out.println("Please enter the genre of the song");
            String genre = scanner.nextLine();
            System.out.println("Please enter the album of the song");
            String album = scanner.nextLine();
            System.out.println("Please enter the duration of the song");
            int duration = scanner.nextInt();
            scanner.nextLine();

            Song newSong = new Song(id, name, genre, duration, album);
            songService.addSongToDatabase(newSong);

            System.out.println("Please enter the id of the artist of the song");
            String idA = scanner.nextLine();
            UUID ArtistId = UUID.fromString(idA);
            newSong.addArtist(ArtistId);

            System.out.println("Cancion creada: " + newSong);
            System.out.println("Por los Cantantes :");
            newSong.printIdsSinger();
        } catch (IllegalArgumentException e) {
            System.err.println("Error al crear la cancion: " + e.getMessage());
        }
        songService.printAllSongs();
    }




    //=========CASE 6 - EDIT NAME OF THE SONG==========

    private static void editSong(Scanner scanner, SongService songService)throws ExitMethodException{
        try{
            songService.printIDandNameSongOnly();
            System.out.println("Please enter the id of the song");
            String id = scanner.nextLine();
            UUID idSong = UUID.fromString(id);

            System.out.println("Please enter the correct name");
            String namu = scanner.nextLine();
            String newName = namu;

            boolean modificated = songService.updateSong (newName,idSong);

            if (modificated) {
                System.out.println("Cancion actualizada con éxito");
            } else {
                System.out.println("El artista con el ID especificado no existe");
            }
        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading Artist due to error: " + e.getMessage());

        }
        songService.printAllSongs();
    }

    //===========CASE 7 - LIST SONGS BY KEYWORD=========

    private static void listSongByKey(Scanner scanner, SongService songService) throws ExitMethodException {
        Set<Integer> searchCriterias = new HashSet<>();
        boolean allCriteriasSelected = false;

        do {
            System.out.println("Please select the search criteria:");
            System.out.println("1. Name");
            System.out.println("2. Genre");
            System.out.println("3. Artist");
            System.out.println("4. Album");

            try {
                int searchCriteria = Integer.parseInt(scanner.nextLine());

                if (searchCriterias.contains(searchCriteria)) {
                    System.out.println("Search criteria already selected");
                }

                if (searchCriteria < 1 || searchCriteria > 4) {
                    System.out.println("Invalid search criteria");
                } else {
                    searchCriterias.add(searchCriteria);
                }

                System.out.println("Do you want to select another search criteria? (y/n)");
                String answer = scanner.nextLine();

                allCriteriasSelected = !answer.equalsIgnoreCase("y");
            } catch (NumberFormatException e) {
                System.out.println("Enter a number");
            }

        } while (!allCriteriasSelected);

        System.out.println("Please enter the search term:");
        String searchTerm = scanner.nextLine();

        List<Song> filteredSongs = songService.getSongsFilteredBy(searchCriterias, searchTerm);

        // Print the filtered songs
        if (filteredSongs.isEmpty()) {
            System.out.println("No matching songs found.");
        } else {
            System.out.println("Matching songs:");

        }


        filteredSongs.stream()
                .forEach(song -> System.out.println(song));
    }


    //===============CASE 8 - REMOVE A SONG BY AN ID======
    // Helper method to print a report of animals pending on the next application
    private static void removeSong(Scanner scanner, SongService songService, CustomerService customerService)throws ExitMethodException{
        try{
            songService.printAllSongs();
            System.out.println("Please enter the id of the song");
            String id = scanner.nextLine();
            UUID idSongToRemove = UUID.fromString(id);

            boolean deletedFromPlaylist = songService.deleteSongFromPlaylists(idSongToRemove,customerService);


            if (deletedFromPlaylist) {
                System.out.println("Song deleted from all playlists and the main list.");
            } else {
                System.out.println("Song not found in any playlists or the main list.");
            }

            boolean deleted = songService.deleteSong(idSongToRemove);

            if (deleted) {
                System.out.println("Cancion eliminada con éxito.");
            } else {
                System.out.println("No se encontró la cancion con el ID especificado.");
            }



        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading song due to error: " + e.getMessage());

        }
        songService.printAllSongs();
    }

    //==============CASE 9 - ADD NEW ARTIST =============
    private static void CreateNewArtist(Scanner scanner, ArtistService artistService)throws ExitMethodException{
        try {
            UUID uid = UUID.randomUUID();
            String id = uid.toString();
            System.out.println("Please enter the name of the artist");
            String name = scanner.nextLine();
            Artist newArtist = artistService.createArtist(id, name);
            System.out.println("Artista creado: " + newArtist);
        } catch (IllegalArgumentException e) {
            System.err.println("Error al crear el artista: " + e.getMessage());
        }
        artistService.printAllArtists();
    }
    // Helper method to add a vaccine to an animal

    //=================CASE 10 - PRINT REPORT OF UNIQUE BRANDS==============
    // Helper method to print a report of unique brands
    /***private static void printReportOfUniqueBrands(AnimalService animalService) {
        List<String> reportOfUniqueBrands = animalService.getUniqueBrandsReport();
        System.out.println("The unique brands are:");
        printReport(reportOfUniqueBrands);
    }***/
    /***private static void printAnimalsForSelection(AnimalService animalService) {

        List<String> animalNames = animalService.getAnimalNamesInList();
        System.out.println("The current animals are:");
        for(int i = 0; i < animalNames.size(); i++) {
            System.out.println(i + ". " + animalNames.get(i));
        }
    }***/

    //===============CASE 11 - REMOVE EXISTING ARTIST=========

    private static void RemoveArtist(Scanner scanner, ArtistService artistService)throws ExitMethodException {

        try{
            System.out.println("Please enter the id of the SONG to delete");
            String id = scanner.nextLine();

            UUID songIdToDelete = UUID.fromString(id);

            boolean deleted = artistService.deleteArtist(songIdToDelete);

            if (deleted) {
                System.out.println("Artista eliminado con éxito.");
            } else {
                System.out.println("No se encontró un artista con el ID especificado.");
            }
        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading Playlist due to error: " + e.getMessage());
        }
        artistService.printAllArtists();
    }
    //===============CASE 10 - EDIT EXISTING ARTIST=========
    private static void EditArtist(Scanner scanner, ArtistService artistService)throws ExitMethodException{

        try{
            System.out.println("Please enter the ID of the existing artist");
            String id = scanner.nextLine();
            UUID artistIdToUpdate = UUID.fromString(id);
            System.out.println("Please enter the correct name of the artist");
            String namu = scanner.nextLine();
            String newArtistName = namu;
            boolean modificated = artistService.updateArtist(artistIdToUpdate, newArtistName);

            if (modificated) {
                System.out.println("Artista actualizado con éxito");
            } else {
                System.out.println("El artista con el ID especificado no existe");
            }
        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading Artist due to error: " + e.getMessage());

        }
        artistService.printAllArtists();
    }
    //===============CASE 12 - list ARTIST PER GENRE=========

    private static void listArtistPerGenre(Scanner scanner, CustomerService customerService, SongService songService, ArtistService artistService)throws ExitMethodException{

        try {
            System.out.println("Enter genre want to filter");
            String genre = scanner.nextLine();
            List<Customer> customers = customerService.getAllCustomers();
            Set<UUID> IdArtistsFilteredByGenre = customerService.getArtistIdsByGenre( customers,genre, songService);
            if (!IdArtistsFilteredByGenre.isEmpty()) {
                System.out.println("Artistas encontrsdos para ese genero con éxito.");
                IdArtistsFilteredByGenre.stream().forEach(uuid -> {
                    Artist artist = artistService.getArtistById(uuid);
                    System.out.println("Artist ID: " + artist.getId() + ", Artist Name: " + artist.getName());
                });

            } else {
                System.out.println("No se encontró un playlist con el ID especificado.");
            }
        } catch (IOException | NotFoundException | IllegalArgumentException e) {
        System.out.println("Error loading playlist due to error: " + e.getMessage());}

    }

    private static void printReportArtistsWithFollowers( ArtistService artistService, CustomerService customerService) {
        List<Customer> customers = customerService.getAllCustomers();
        Map<UUID, Long> artistFollowersCount = customers.stream()
                .flatMap(customer -> customer.getArtistIds().stream())
                .collect(Collectors.groupingBy(artistId -> artistId, Collectors.counting()));

        artistFollowersCount.forEach((artistId, followerCount) -> {
            Artist artist = artistService.getArtistById(artistId);
            System.out.println("Artist: " + artist.getName() + ", Followers: " + followerCount);
        });
    }


    //============CASE 14 - REPORT OF ARTIS'S POPULARITY ===========
    private static void printReportArtistPopularity(ArtistService artistService, CustomerService customerService){

        List<Customer> customers = customerService.getAllCustomers();

        // Create a map to count the number of followers for each artist
        Map<UUID, Long> artistFollowersMap = customers.stream()
                .flatMap(customer -> customer.getArtistIds().stream())
                .collect(Collectors.groupingBy(
                        artistId -> artistId,
                        Collectors.counting()
                ));

        // Sort the map by the number of followers in descending order
        artistFollowersMap = artistFollowersMap.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Calculate popularity and print the artists with their popularity
        long maxFollowers = artistFollowersMap.values().stream().findFirst().orElse(0L);

        artistFollowersMap.forEach((artistId, followers) -> {
            double popularity = (double) followers / maxFollowers * 100;
            Artist artist = artistService.getArtistById(artistId);
            System.out.println("Artist: " + artist.getName() + ", Popularity: " + popularity);
        });
    }

    //============CASE 15 - REPORT OF SONGS PRESENT IN ALL PLAYLIST ===========
    private static void printReportSongsPrsent(Scanner scanner, CustomerService customerService, SongService songService, ArtistService artistService)throws ExitMethodException{
        try {
            List<Customer> customerList = customerService.getAllCustomers();
            // Call the function to get songs present in all playlists
            List<UUID> songsInAllPlaylists = customerService.getSongsPresentInAllPlaylists(customerList);

            // Print the songs
            songsInAllPlaylists.forEach(System.out::println);
            if (!songsInAllPlaylists.isEmpty()) {
                System.out.println("Songs encontrsdos en todas las playlist con éxito.");
                songsInAllPlaylists.stream().forEach(uuid -> {
                    Song song = songService.getSongByIdd(uuid);
                    System.out.println("Artist ID: " + song.getID() + ", Artist Name: " + song.getName());
                });

            } else {
                System.out.println("No se encontró ninguna cancion que este en todas las playlist.");
            }
        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading playlist due to error: " + e.getMessage());}

    }


    //============CASE 16 - ADD A PLAYLIST ===========
    private static void addPlaylist(Scanner scanner, CustomerService customerService)throws ExitMethodException{

        try{
            System.out.println("Please enter the username");
            String username = scanner.nextLine();

            //List<Playlist> playlists = customerService.getAllPlaylistsFromCustomers();
            //playlists.stream().forEach(playlist -> System.out.println(playlist));

            customerService.getAllPlaylists().stream()
                    .forEach(playlist -> System.out.println(playlist.toCSV(";")));

            System.out.println("Please enter the id of the playlist to add");
            String id = scanner.nextLine();
            UUID uid = UUID.fromString(id);

            boolean added = customerService.addPlaylistToCustomer(username, uid);

            if (added) {
                System.out.println("Playlist añadida con éxito.");

            } else {
                System.out.println("No se encontró un playlist con el ID especificado.");
            }
        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading playlist due to error: " + e.getMessage());

        }


    }


    //============CASE 17 - EDIT A PLAYLIST ===========

    private static void editPlaylist (Scanner scanner, CustomerService customerService)throws ExitMethodException{

            System.out.println("Please enter the username");
            String username = scanner.nextLine();
            System.out.println("Please enter the id of the playlist to modificate");
            String idPl = scanner.nextLine();
            System.out.println("Please enter the correct name of the playlist");
            String namu = scanner.nextLine();
            String newName = namu;
        try{

            boolean playlistWasUpdated = customerService.updatePlaylist(newName,username,idPl);

            if (!playlistWasUpdated) {
                System.out.println("El playlist con el ID especificado no existe");

            }
            System.out.println("Playlist actualizado con éxito");


        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading playlist due to error: " + e.getMessage());

        }

    }

//============CASE 18 - loadCustomerAndPlaylistFromCSV ===========

    // Helper method to load animals and vaccines from CSV
    private static void loadCustomerAndPlaylistFromCSV(Scanner scanner,
                                                       CustomerService customerService,
                                                       FileService fileService) throws ExitMethodException {


            loadCustomerFromCSV(scanner, customerService, fileService);
            // Ask the user if they want to load Playlist from CSV
            System.out.println("Do you want to load playlists from CSV? (y/n)");
            String loadPlaylist = scanner.nextLine();

            if( loadPlaylist.equalsIgnoreCase(YES)) {
                loadPlaylistFromCSV(scanner, customerService, fileService);
            }
            customerService.getAllCustomers().stream()
                    .forEach(customer -> System.out.println(customer.toCSV(";")));




    }

    private static void loadCustomerFromCSV(Scanner scanner, CustomerService customerService, FileService fileService)
            throws ExitMethodException {

        // Ask the user for the CSV file path and delimiter
        String path = getPath(scanner, CUSTOMERS_CSV_DEFAULT_PATH);
        String delimiter = getDelimiter(scanner);

        //Reference: https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
        //Reference: https://www.geeksforgeeks.org/try-catch-throw-and-throws-in-java/
        try{
            // Attempt to load animals from the specified CSV file
            boolean loaded = customerService.loadCustomerFromCSVFile(path, delimiter, fileService);
            //loaded = true;
            if (loaded) {
                System.out.println("Customer loaded successfully");
            } else {
                System.out.println("Customer not loaded");
            }
       } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading customer due to error: " + e.getMessage());
            throw new ExitMethodException();
        }

    }


    /***
    // Helper method to load vaccines from CSV
    private static void loadPlaylistFromCSV(Scanner scanner,
                                            CustomerService customerService,
                                            FileService fileService)
            throws ExitMethodException {
        String path = getPath(scanner, PLAYLISTS_CSV_DEFAULT_PATH);
        String delimiter = getDelimiter(scanner);

        try{
            // Attempt to load vaccines from the specified CSV file
            boolean loaded = customerService.loadPlaylistFromCSVFile(path, delimiter, fileService );
            if (loaded) {
                System.out.println("Playlist loaded successfully");
            } else {
                System.out.println("Playlist not loaded");
            }
        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading Playlist due to error: " + e.getMessage());
            throw new ExitMethodException();
        }
    }***/

    private static void loadPlaylistFromCSV(Scanner scanner,
                                            CustomerService customerService,
                                            FileService fileService)
            throws ExitMethodException {

        String path = getPath(scanner, PLAYLISTS_CSV_DEFAULT_PATH);
        String delimiter = getDelimiter(scanner);

        try{
            // Attempt to load animals from the specified CSV file
            boolean loaded = customerService.loadPlaylistFromCSVFile(path, delimiter, fileService);
            /***for (Customer customer : customerService.getAllCustomers()){
                List<Playlist> listaPlaylist = new ArrayList<>();
                listaPlaylist = null;
                for (Playlist playlist : customerService.getAllPlaylists()){
                    if (customer.getId()==playlist.getCustomerId()){
                        listaPlaylist.add(playlist);
                    }
                }
                customer.addPlaylists(listaPlaylist);
            }***/

            if (loaded) {
                System.out.println("Playlist loaded successfully");
            } else {
                System.out.println("Playlist not loaded");
            }
        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading Playlist due to error: " + e.getMessage());
            throw new ExitMethodException();
        }

    }

    //=========CASE 13 - LOAD ARTIST FROM CSV============
    private static void loadArtistFromCSV(Scanner scanner, ArtistService artistService, FileService fileService)
            throws ExitMethodException {

        System.out.println("Do you want to load artist from CSV? (y/n)");
        String loadArtist = scanner.nextLine();

        if(!loadArtist.equalsIgnoreCase(YES)) {
            throw new ExitMethodException();
        }

        System.out.println(PLEASE_ENTER_THE_PATH_OF_THE_CSV_FILE);
        String path = getPath(scanner, ARTIST_CSV_DEFAULT_PATH);

        System.out.println("Please enter the delimiter");
        String delimiter = getDelimiter(scanner);

        try{
            // Attempt to load owners from the specified CSV file
            boolean loaded = artistService.loadArtistFromCSV(path, delimiter, fileService);
            if (loaded) {
                System.out.println("Artist loaded successfully");
            } else {
                System.out.println("Artist not loaded");
            }
        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading artists due to error: " + e.getMessage());
        }

    }

    //=========CASE 14 - LOAD SONG FROM CSV============
    private static void loadSongFromCSV(Scanner scanner, SongService songService, FileService fileService)
            throws ExitMethodException {

        // Ask the user for the CSV file path and delimiter
        String path = getPath(scanner, SONGS_CSV_DEFAULT_PATH);
        String delimiter = getDelimiter(scanner);

        //Reference: https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
        //Reference: https://www.geeksforgeeks.org/try-catch-throw-and-throws-in-java/
        try{
            // Attempt to load animals from the specified CSV file
            boolean loaded = songService.loadSongFromCSVFile(path, delimiter, fileService);
            if (loaded) {
                System.out.println("Song loaded successfully");
            } else {
                System.out.println("Song not loaded");
            }
        } catch (IOException | NotFoundException | IllegalArgumentException e) {
            System.out.println("Error loading song due to error: " + e.getMessage());
            throw new ExitMethodException();
        }

    }

    private static void printTypeCustomers(Scanner scanner, CustomerService customerService, FileService fileService) {
        System.out.println("Do you want to print premium customers? (y/n)");
        String premium = scanner.nextLine();
        if( premium.equalsIgnoreCase(YES)) {customerService.printCustomersByTypeAndSort("premium");}
        else {
            customerService.printCustomersByTypeAndSort("regular");
        }

        Song mySong = new Song("8fd96963-d89e-4de5-8f0d-3a6370ee4feb", "Something", "Pop", 200, "Album Name");

        // Play the song
        String playbackMessage = mySong.play();
        System.out.println(playbackMessage);

    }


    //========CASE 21 - SAVE PROGRAM STATE TO BINARY FILE==========
    private static void saveStateToBinaryFile(Scanner scanner,
                                              CustomerService customerService,
                                              ArtistService artistService,
                                              SongService songservice,
                                              FileService fileService) throws ExitMethodException {
        System.out.println("""
              Do you want to save state from binary file? (y/n)
              THIS OPERATION WILL OVERWRITE THE CURRENT LIST OF ANIMALS AND OWNERS THAT IS SAVED
              """);
        String saveState = scanner.nextLine();

        if(!saveState.equalsIgnoreCase(YES)) {
            throw new ExitMethodException();
        }

        saveCustomersToBinaryFile(scanner, customerService, fileService);
        saveArtistToBinaryFile(scanner, artistService, fileService);
        saveSongsToBinaryFile(scanner, songservice, fileService);
    }

    private static void saveSongsToBinaryFile(Scanner scanner, SongService songService, FileService fileService)
            throws ExitMethodException {
        System.out.println(
                String.format(DO_YOU_WANT_TO_SAVE_TO_BINARY_FILE_TEMPLATE, "songs"));
        String saveSongs = scanner.nextLine();

        if(!saveSongs.equalsIgnoreCase(YES)) {
            throw new ExitMethodException();
        }

        System.out.println("Please enter the path of the binary file");
        String path = getPath(scanner, SONGS_BIN_DEFAULT_PATH);

        try{
            // Attempt to save Customers to the specified binary file
            //If something is wrong it will throw an exception
            songService.saveSongsToBinaryFileUsingTheEntireList(path, fileService);

            System.out.println("Songs saved successfully");


        } catch (IOException e) {
            System.out.println("Error saving Songs due to error: " + e.getMessage());
        }
    }

    private static void saveCustomersToBinaryFile(Scanner scanner,
                                               CustomerService customerService,
                                               FileService fileService) throws ExitMethodException {

        System.out.println(
                String.format(DO_YOU_WANT_TO_SAVE_TO_BINARY_FILE_TEMPLATE, "customers"));
        String saveCustomers = scanner.nextLine();

        if(!saveCustomers.equalsIgnoreCase(YES)) {
            throw new ExitMethodException();
        }

        System.out.println("Please enter the path of the binary file");
        String path = getPath(scanner, CUSTOMERS_BIN_DEFAULT_PATH);

        try{
            // Attempt to save Customers to the specified binary file
            //If something is wrong it will throw an exception
            customerService.saveCustomersToBinaryFileUsingTheEntireList(path, fileService);

            System.out.println("Customers saved successfully");


        } catch (IOException e) {
            System.out.println("Error saving Customers due to error: " + e.getMessage());
        }
    }

    private static void saveArtistToBinaryFile(Scanner scanner,
                                                ArtistService artistService,
                                                FileService fileService) throws ExitMethodException {

        System.out.println(
                String.format(DO_YOU_WANT_TO_SAVE_TO_BINARY_FILE_TEMPLATE, "artist"));
        String saveArtist = scanner.nextLine();

        if(!saveArtist.equalsIgnoreCase(YES)) {
            throw new ExitMethodException();
        }

        System.out.println("Please enter the path of the binary file");
        String path = getPath(scanner, ARTIST_BIN_DEFAULT_PATH);

        try{
            // Attempt to save artist to the specified binary file
            //If something is wrong it will throw an exception
            artistService.saveArtistsToBinaryFileUsingTheEntireList(path, fileService);

            System.out.println("Artists saved successfully");


        } catch (IOException e) {
            System.out.println("Error saving artists due to error: " + e.getMessage());
        }


    }

    //OTHER METHODS
    // Helper method to get the file path files
    private static String getPath(Scanner scanner, String defaultPath) {
        System.out.println("Load using default path? (y/n) " + defaultPath + " is the default");
        String useDefaultPath = scanner.nextLine();

        if(!useDefaultPath.equalsIgnoreCase(YES)) {
            System.out.println("Please enter the path of the file");
        }

        //Reference: https://www.geeksforgeeks.org/java-ternary-operator-with-examples/
        return useDefaultPath.equalsIgnoreCase(YES)
                ? defaultPath
                : scanner.nextLine();
    }

    // Helper method to get the delimiter for CSV files
    private static String getDelimiter(Scanner scanner) {
        System.out.println("Load using default delimiter? (y/n) " + DEFAULT_DELIMITER + " is the default");
        String useDefaultDelimiter = scanner.nextLine();

        if (!useDefaultDelimiter.equalsIgnoreCase(YES)) {
            System.out.println("Please enter the delimiter");
        }

        return useDefaultDelimiter.equalsIgnoreCase(YES)
                ? DEFAULT_DELIMITER
                : scanner.nextLine();
    }













    // Helper method to print the names of current animals
    /***private static void printAnimalNames(AnimalService animalService) {
        System.out.println("The current animals are:");
        for(String animalName : animalService.getAnimalNamesInList()) {
            System.out.println(animalName);
        }
    }***/



    // Helper method to print a report
    private static void printReport(List<String> report) {
        for(String reportValue : report) {
            System.out.println(reportValue);
        }
    }
}


