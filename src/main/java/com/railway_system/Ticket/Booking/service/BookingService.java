package com.railway_system.Ticket.Booking.service;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.railway_system.Ticket.Booking.dao.BookingDao;
import com.railway_system.Ticket.Booking.dao.TrainDao;
import com.railway_system.Ticket.Booking.dao.UserDao;
import com.railway_system.Ticket.Booking.entity.Booking;
import com.railway_system.Ticket.Booking.entity.Train;
import com.railway_system.Ticket.Booking.entity.User;
import com.railway_system.Ticket.Booking.enums.BookingStatus;
import com.railway_system.Ticket.Booking.enums.PaymentStatus;
import com.railway_system.Ticket.Booking.exception.BookingNotFoundException;
import com.railway_system.Ticket.Booking.exception.SeatNotAvailableException;
import com.railway_system.Ticket.Booking.exception.TrainNotFoundException;
import com.railway_system.Ticket.Booking.exception.UserNotFoundException;

import jakarta.transaction.Transactional;
@Service
public class BookingService {
	
	  private final UserDao userdao;
	  private final TrainDao traindao;
      private final BookingDao bookingdao;
      public BookingService(BookingDao bookingdao, UserDao userdao,TrainDao traindao) {
    	  this.bookingdao=bookingdao;
    	  this.userdao=userdao;
    	  this.traindao=traindao;
     }
      
      @Transactional
     public Booking bookTicket(Booking booking) {

         // Check User
         Optional<User> optionalUser = userdao.getUserById(booking.getUser().getId());

         if (optionalUser.isEmpty()) {
        	 throw new UserNotFoundException("User Not Found");
         }

         // Check Train
         Optional<Train> optionalTrain =traindao.getTrainById(booking.getTrain().getTrainId());

         if (optionalTrain.isEmpty()) {
        	  throw new TrainNotFoundException("Train Not Found");
         }

         Train train = optionalTrain.get();

         // Check Seats
         if (train.getAvailableSeats() < booking.getNumSeatBooked()) {
        	 throw new SeatNotAvailableException("Seats Not Available");
         }

         // Reduce Seats
         train.setAvailableSeats(
                 train.getAvailableSeats() - booking.getNumSeatBooked());

         // Update Train
         traindao.updateTrain(train);

         // Set Managed Objects
//         booking.setStatus(BookingStatus.CONFIRMED);
         booking.setUser(optionalUser.get());
         booking.setTrain(train);

         // Save Booking
         return bookingdao.bookTicket(booking);

     }


     public Optional<Booking> getBookingById(int id) {
         return bookingdao.getBookingById(id);
     }

     public List<Booking> getAllBookings() {
         return bookingdao.getAllBookings();
     }

     public List<Booking> getBookingsByUserId(int userId) {
         return bookingdao.getBookingsByUserId(userId);
     }

     public Booking updateBooking(Booking booking) {
         return bookingdao.updateBooking(booking);
     }

     public void deleteBooking(int id) {
         bookingdao.deleteBooking(id);
     }
     
     @Transactional
     public Booking cancelBooking(int bookingId) {

    	    Optional<Booking> optionalBooking =bookingdao.getBookingById(bookingId);

    	    if (optionalBooking.isEmpty()) {
    	    	    throw new BookingNotFoundException("Booking Not Found");
    	    }

    	    Booking booking = optionalBooking.get();

    	    Train train = booking.getTrain();

    	    train.setAvailableSeats(train.getAvailableSeats() + booking.getNumSeatBooked());

    	    traindao.updateTrain(train);

    	    booking.setStatus(BookingStatus.CANCELLED);

    	    if (booking.getPayment() != null) {
    	        booking.getPayment().setPaymentStatus(PaymentStatus.REFUNDED);
    	    }

    	    return bookingdao.updateBooking(booking);
    	}
}