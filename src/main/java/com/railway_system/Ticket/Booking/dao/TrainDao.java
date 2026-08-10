package com.railway_system.Ticket.Booking.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.railway_system.Ticket.Booking.entity.Train;
import com.railway_system.Ticket.Booking.repository.TrainRepository;

@Repository
public class TrainDao {

    private final TrainRepository repository;

    public TrainDao(TrainRepository repository) {
        this.repository = repository;
    }

    public Train addTrain(Train train) {
        return repository.save(train);
    }

    public Optional<Train> getTrainById(int id) {
        return repository.findById(id);
    }

    public List<Train> getAllTrains() {
        return repository.findAll();
    }

    public Train updateTrain(Train train) {
        return repository.save(train);
    }

    public void deleteTrain(int id) {
        repository.deleteById( id);
    }

    public List<Train> searchTrain(String source, String destination) {
        return repository.findBySourceAndDestination(source, destination);
    }

	

	

}
