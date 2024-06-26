package ticket.booking.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserBookingService{

    private final ObjectMapper objectMapper = new ObjectMapper();


    private List<User> userList;

    private User user;

    private final String USER_FILE_PATH = "src/main/java/ticket/booking/localdb/user.json";

    public UserBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();
    }

    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

    private void loadUserListFromFile() throws IOException {
        userList = objectMapper.readValue(new File(USER_FILE_PATH), new TypeReference<List<User>>() {});
    }

    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USER_FILE_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public void fetchBookings(){
        Optional<User> userFetched = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashedPassword());
        }).findFirst();
        userFetched.ifPresent(User::printTickets);
    }

    public Boolean cancelBooking(String ticketId){
        // todo: Complete this function
        return Boolean.FALSE;
    }

    public List<Train> getTrains(String source, String destination){
        try{
            TrainService trainService = new TrainService();
            return TrainService.searchTrains(source, destination);
        }catch(IOException ex){
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train) {
        if (train == null || train.getSeats() == null) {
            return new ArrayList<>(); // or throw an exception, depending on your application logic
        }
//        for (List<Integer> row: seats){
//            for (Integer val: row){
//                System.out.print(val+" ");
//            }
//            System.out.println();
//        }
        return train.getSeats();
    }

//    public Boolean bookTrainSeat(Train train, int row, int seat) {
//        try{
//            TrainService trainService = new TrainService();
//            List<List<Integer>> seats = train.getSeats();
//            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
//                if (seats.get(row).get(seat) == 0) {
//                    seats.get(row).set(seat, 1);
//                    train.setSeats(seats);
//                    trainService.addTrain(train);
//                    return true; // Booking successful
//                } else {
//                    return false; // Seat is already booked
//                }
//            } else {
//                return false; // Invalid row or seat index
//            }
//        }catch (IOException ex){
//            return Boolean.FALSE;
//
//
//        }
//    }
    public Boolean bookTrainSeat(Train train, int row, int seat) {


        try {

            if (train.getSeats() == null) {
                return Boolean.FALSE; // or handle this case according to your application logic
            }

            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();


//            System.out.println(seats.getFirst());
            // Check if the row index is valid
            if (row < 0 || row >= seats.size()) {
                return Boolean.FALSE;
            }

            // Check if the seat index is valid
            List<Integer> rowSeats = seats.get(row);
            if (rowSeats == null || seat < 0 || seat >= rowSeats.size()) {
                return Boolean.FALSE;
            }

            // Check if the seat is already booked
            if (rowSeats.get(seat) != 0) {
                return Boolean.FALSE;
            }

            // Book the seat
            rowSeats.set(seat, 1);
            train.setSeats(seats);
            trainService.addTrain(train);
            return Boolean.TRUE; // Booking successful
        } catch (IOException ex) {
            return Boolean.FALSE;
        }
    }
}