package com.railway_system.Ticket.Booking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.railway_system.Ticket.Booking.dao.TrainDao;
import com.railway_system.Ticket.Booking.entity.Train;

@Service
public class TrainService {
	 
     private TrainDao traindao;
     public TrainService(TrainDao traindao) {
    	     this.traindao=traindao;
     }
     public Train addTrain(Train train) {
         return traindao.addTrain(train);
     }
     public Optional<Train> getTrainById(int id) {
         return traindao.getTrainById(id);
     }

     // Get All Trains
     public List<Train> getAllTrains() {
         return traindao.getAllTrains();
     }

     // Update Train
     public Train updateTrain(Train train) {
         return traindao.updateTrain(train);
     }

     // Delete Train
     public void deleteTrain(int id) {
         traindao.deleteTrain(id);
     }

     // Search Train
     public List<Train> searchTrain(String source, String destination) {

    	    List<Train> trains = traindao.searchTrain(source, destination);

    	    return trains.stream()
    	            .filter(train -> train.getAvailableSeats() > 0)
    	            .toList();
    	}
}
