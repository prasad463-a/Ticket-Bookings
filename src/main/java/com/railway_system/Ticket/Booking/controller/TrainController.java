package com.railway_system.Ticket.Booking.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.railway_system.Ticket.Booking.entity.Train;
import com.railway_system.Ticket.Booking.service.TrainService;

@RestController
@RequestMapping("/train")
public class TrainController {
        private final TrainService service;
        public TrainController(TrainService service) {
        	 this.service=service;
        }

        // Add Train
        @PostMapping("/add")
        public ResponseEntity<Train> addTrain(@RequestBody Train train) {

            Train savedTrain = service.addTrain(train);

            return new ResponseEntity<>(savedTrain, HttpStatus.CREATED);
        }
        @GetMapping("/get/{id}")
        public ResponseEntity<Optional<Train>> getTrain(@PathVariable int id) {

            Optional<Train> train = service.getTrainById(id);

            return new ResponseEntity<>(train, HttpStatus.OK);
        }

        // Get All Trains
        @GetMapping("/getall")
        public ResponseEntity<List<Train>> getAllTrains() {

            List<Train> trains = service.getAllTrains();

            return new ResponseEntity<>(trains, HttpStatus.OK);
        }

        // Update Train
        @PutMapping("/update")
        public ResponseEntity<Train> updateTrain(@RequestBody Train train) {

            Train updatedTrain = service.updateTrain(train);

            return new ResponseEntity<>(updatedTrain, HttpStatus.OK);
        }

        // Delete Train
        @DeleteMapping("/delete/{id}")
        public ResponseEntity<String> deleteTrain(@PathVariable int id) {

            service.deleteTrain(id);

            return new ResponseEntity<>("Train Deleted Successfully", HttpStatus.OK);
        }

        // Search Train
        @GetMapping("/search")
        public ResponseEntity<List<Train>> searchTrain(
                @RequestParam String source,
                @RequestParam String destination) {

            List<Train> trains = service.searchTrain(source, destination);

            return new ResponseEntity<>(trains, HttpStatus.OK);
        }
//        @GetMapping("/search")
//        public ResponseEntity<List<Train>> searchTrain(
//                @RequestParam String source,
//                @RequestParam String destination) {
//
//            List<Train> trains = service.searchTrain(source, destination);
//
//            return ResponseEntity.ok(trains);
//        }
}
