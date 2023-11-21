package org.spotify.services;

import org.spotify.exceptions.NotFoundException;
import org.spotify.exceptions.UnsupportedOperationException;
import org.spotify.exceptions.UserNameAlreadyTakenException;
import org.spotify.model.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.spotify.model.customers.Customer;
import org.spotify.model.customers.PremiumCustomer;
import org.spotify.model.customers.RegularCustomer;

public class CustomerService {
    private List<Customer> customerList ;

    private List<Playlist> playlistList;

    public CustomerService() {

        this.customerList = new ArrayList<>();
        this.playlistList = new ArrayList<>();
    }
    public Optional<Playlist> getPlaylistById(UUID playlistId) {
        return playlistList.stream()
                .filter(playlist -> playlist.getId().equals(playlistId))
                .findFirst();
    }
    public Customer findCustomerById(UUID id) {
        for (Customer customer : customerList) {
            if (customer.getId().equals(id)) {
                return customer;
            }
        }

        // This implementation could be improved using Optional.
        return null;
    }
    public boolean loadCustomerFromCSVFile(String path,
                                           String delimiter,
                                           FileService fileService)throws IOException,NotFoundException {

        List<Customer> customers = fileService.readCustomersFromCSV(path,delimiter);


        return customerList.addAll(customers);
    }

    public boolean loadCustomerWithPlaylistsFromCSVFile(String customersPath,
                                           String playlistsPath,
                                           String delimiter,
                                           FileService fileService)
        throws IOException, NotFoundException, UnsupportedOperationException {

        List<Customer> customers =
            fileService.readCustomersWithPlayListsFromCSV(customersPath,delimiter,playlistsPath);

        return customerList.addAll(customers);
    }

    public boolean loadPlaylistFromCSVFile(String path,
                                           String delimiter,
                                           FileService fileService)
            throws IOException, NotFoundException {

        List<Playlist> playlists = fileService.loadPlaylistsFromCSV(path,delimiter);


        return playlistList.addAll(playlists);
    }

    /***public boolean loadPlaylistFromCSVFile(String path,
                                           String delimiter,
                                           FileService fileService)
            throws IOException, NotFoundException {

        Map<UUID, List<Playlist>> playlistByCustomerId =
                fileService.readPlayListFromCSV(path,delimiter);

        // search custumer by id and add playlist to the customer
        for (Map.Entry<UUID, List<Playlist>>entry:playlistByCustomerId.entrySet()){
            Customer customer = findCustomerById(entry.getKey());

            System.out.println(entry.getKey());
            System.out.println(entry.getValue());

            if (customer==null){
                throw new NotFoundException(String.format("Error while assigning playlist to customer"+
                        "Playlist with id %s not found", entry.getKey()));
            }

            boolean playlistAdded = customer.addPlaylists(entry.getValue());

            if (!playlistAdded){
                    return false;
            }
        }
        return true;
    }***/

    public void saveCustomersToBinaryFileUsingTheEntireList(String path, FileService fileService) throws IOException{

        fileService.saveCustomerToBinaryFileUsingTheEntireList(path, customerList);

    }

    public boolean addCustomerToDatabase(String username, String password, String name, String lastname, int age)
            throws  IllegalArgumentException, UserNameAlreadyTakenException {
        if (usernameIsTaken(username)) {
            throw new UserNameAlreadyTakenException(String.format("Username %s is already taken", username));
        }

        /***Customer newcustomer = new Customer(username,password,name,lastname,age);
        //put method returns null if the key is not present in the map
        customerList.add(newcustomer);***/
        return true;
    }
    private boolean addCustomerToDatabase(Customer customer) throws IllegalArgumentException {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        return customerList.add(customer);
    }

    private boolean usernameIsTaken(String username) {
        return customerList.stream()
                .allMatch(owner -> owner.getUsername().equalsIgnoreCase(username));
    }

    private Customer findCustomerByName(String nameOfAnimal) {
        for (Customer customer : customerList) {
            if (customer.getName().equals(nameOfAnimal)) {
                    return customer;
            }
        }

        // This implementation could be improved using Optional.
        return null;
    }
    public boolean addPlaylistToCustomer(String username, UUID idPlaylist)
            throws IOException, NotFoundException, IllegalArgumentException {

        Optional<Customer> optionalCustomer = getCustomerByUsername(username);
        if (!optionalCustomer.isPresent()) {
            System.out.println("Customer not found");
            throw new NotFoundException("Error while detecting customer "+ " not found");
        }

        Customer customer = optionalCustomer.get(); // Get the customer if it exists

        Optional<Playlist> optionalPlaylist = getPlaylistById(idPlaylist);

        if (!optionalPlaylist.isPresent()){
            System.out.println("Playlist not found");
            throw new NotFoundException("Error while detecting playlist not found");
        }

        Playlist playlistToAdd = optionalPlaylist.get();

        //Use polymorphism to add playlist to customer, the instance of is not necessary,
        // that is the idea of polymorphism
        if (customer instanceof PremiumCustomer){
            ((PremiumCustomer) customer).addPlaylist(playlistToAdd);
        } else if (customer instanceof RegularCustomer){
            ((RegularCustomer)customer).addPlaylist(playlistToAdd);
        }

        return true;
    }
    public void loadCustomersToBinaryFileUsingTheEntireList(String path, FileService fileService) throws IOException, ClassNotFoundException{
        List<Customer> customers =
                fileService.loadCustomersFromBinaryFileUsingTheEntireList(path);
        clearCustomerList();
        customerList.addAll(customers);
    }

    private void clearCustomerList() {
        customerList.clear();
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customerList);
    }

    public List<Playlist> getAllPlaylists() {
        return new ArrayList<>(playlistList);
    }

    public boolean findCustomerByUsername(String username) throws NotFoundException {
        Optional<Customer> customerToUpdate = getCustomerByUsername(username);
        if (customerToUpdate == null) {
            // El artista con el ID especificado no existe
            throw new NotFoundException("Error while update customer with username " + username + " not found");
        }

        return true;
    }

    public Optional<Customer> getCustomerByUsername(String username) {
        return customerList.stream()
                .filter(customer -> customer.getUsername().equals(username))
                .findFirst(); // Returns an Optional<Customer>
    }



    public boolean updateUser(String newName, String username)throws IOException, NotFoundException, IllegalArgumentException {
        Optional<Customer> optionalCustomer = getCustomerByUsername(username);
        if (!optionalCustomer.isPresent()) {
            System.out.println("Customer not found");
            throw new NotFoundException("Error while detecting customer "+ " not found");
        }


        // Validar el nuevo nombre del customer
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("El nuevo nombre no puede ser nulo o vacío");
        }

        Customer customerToUpdate = optionalCustomer.get(); // Get the customer if it exists
        System.out.println("Customer found: " + customerToUpdate);

        customerToUpdate.setName(newName);
        return true;

    }
    public boolean updatePlaylist(String newName, String username, String idPl) throws IOException, NotFoundException, IllegalArgumentException {
        Optional<Customer> optionalCustomer = getCustomerByUsername(username);

        if (!optionalCustomer.isPresent()) {
            System.out.println("Customer not found");
            throw new NotFoundException("Error while detecting customer " + username + " not found");
        }

        // Validar el nuevo nombre del playlist
        if (newName == null || newName.isEmpty()) {
            throw new IllegalArgumentException("El nuevo nombre no puede ser nulo o vacío");
        }

        Customer customer = optionalCustomer.get(); // Get the customer if it exists

        UUID playlistId = UUID.fromString(idPl);

        Optional<Playlist> optionalPlaylist = customer.updatePlaylist(newName, playlistId);

        return optionalPlaylist.isPresent();
    }



    public void printAllCustomers() {
        customerList.stream()
                .forEach(customer -> System.out.println(customer));
    }

    public boolean deleteUser(String username) throws IOException,NotFoundException,IllegalArgumentException{
        Optional<Customer> optionalCustomer = getCustomerByUsername(username);
        if (!optionalCustomer.isPresent()) {
            System.out.println("Customer not found");
            throw new NotFoundException("Error while detecting customer "+ " not found");
        }
        Customer customerToRemove = optionalCustomer.get(); // Get the customer if it exists
        customerList.remove(customerToRemove);
        return true;
    }


    public boolean followNewArtist(String username, UUID idArtist)throws IOException, NotFoundException, IllegalArgumentException {
        Optional<Customer> optionalCustomer = getCustomerByUsername(username);
        if (!optionalCustomer.isPresent()) {
            System.out.println("Customer not found");
            throw new NotFoundException("Error while detecting customer "+ " not found");
        }
        Customer customerToAddArtist = optionalCustomer.get(); // Get the customer if it exists
        customerToAddArtist.addArtistIdFollowed(idArtist);
        customerToAddArtist.printArtistIdsFollowedSet();
        return true;
    }


    public Set<UUID> getArtistIdsByGenre(List<Customer> customers, String genre, SongService songService)throws IOException,NotFoundException,IllegalArgumentException {
        Set<UUID> artistIds = customers.stream()
                .flatMap(customer -> customer.getPlaylists().stream())
                .flatMap(playlist -> playlist.getSongIdsList().stream())
                .map(songId -> songService.getSongByIdd(songId)) // Implement a method to get a Song by its ID
                .filter(song -> song.getGenre().equalsIgnoreCase(genre)) // Missing closing parenthesis here
                .flatMap(song -> song.getArtistIds().stream())
                .collect(Collectors.toSet());

        return artistIds;
    }

    public List<UUID> getSongsPresentInAllPlaylists(List<Customer> customers)throws IOException,NotFoundException,IllegalArgumentException {
        // Combine songs from all playlists
        List<UUID> allSongs = customers.stream()
                .flatMap(customer -> customer.getPlaylists().stream()
                        .flatMap(playlist -> playlist.getSongIdsList().stream()))
                .collect(Collectors.toList());

        // Count occurrences of each song
        Map<UUID, Long> songCounts = allSongs.stream()
                .collect(Collectors.groupingBy(songId -> songId, Collectors.counting()));

        // Filter songs that occur in all playlists
        long numCustomers = customers.size();
        List<UUID> songsPresentInAllPlaylists = songCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == numCustomers)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return songsPresentInAllPlaylists;
    }

    public void saveCustomersToCSVFile(String filePath, FileService fileService)throws IOException{
        List<String> customersListToCSV = this.customerList.stream()
                .map(customer -> customer.toCSV(";"))
                .toList();
        fileService.writeTextFile(filePath, customersListToCSV);
    }

    public List<Playlist> getAllPlaylistsFromCustomers() {
        return customerList.stream()
                .flatMap(customer -> customer.getPlaylists().stream())
                .distinct() // Remove duplicates
                .collect(Collectors.toList());
    }

    public void savePlaylistToCSVFile(String filePath, FileService fileService)throws IOException{
        List<String> playlistsListToCSV = this.customerList.stream()
                .flatMap(customer -> customer.getPlaylists().stream())
                .map(playlist -> playlist.toCSV(";"))
                .toList();
        fileService.writeTextFile(filePath, playlistsListToCSV);
    }

    public void printCustomersByTypeAndSort(String customerType) {
        List<Customer> filteredCustomers;

        if ("regular".equalsIgnoreCase(customerType)) {
            // Filtrar solo clientes regulares
            filteredCustomers = customerList.stream()
                    .filter(c -> c instanceof RegularCustomer)
                    .sorted(Comparator.comparing(Customer::getUsername))
                    .collect(Collectors.toList());
        } else if ("premium".equalsIgnoreCase(customerType)) {
            // Filtrar solo clientes premium
            filteredCustomers = customerList.stream()
                    .filter(c -> c instanceof PremiumCustomer)
                    .sorted(Comparator.comparing(Customer::getUsername))
                    .collect(Collectors.toList());
        } else {
            // Si el tipo no es ni "regular" ni "premium", imprimir todos los clientes ordenados por username
            filteredCustomers = customerList.stream()
                    .sorted(Comparator.comparing(Customer::getUsername))
                    .collect(Collectors.toList());
        }

        // Imprimir los clientes
        for (Customer customer : filteredCustomers) {
            System.out.println(customer);
        }


    }


    /***public List<UUID> getFollowedIdsOfAllCustomers() {

    }

    public List<UUID> getUniqueSongsIdsInAllCustomerPlayList() {

    }***/
}
