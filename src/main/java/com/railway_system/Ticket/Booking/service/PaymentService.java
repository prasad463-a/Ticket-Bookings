package com.railway_system.Ticket.Booking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.railway_system.Ticket.Booking.dao.BookingDao;
import com.railway_system.Ticket.Booking.dao.PaymentDao;
import com.railway_system.Ticket.Booking.entity.Booking;
import com.railway_system.Ticket.Booking.entity.Payment;
import com.railway_system.Ticket.Booking.enums.BookingStatus;
import com.railway_system.Ticket.Booking.enums.PaymentStatus;
import com.railway_system.Ticket.Booking.exception.BookingNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class PaymentService {
     private final PaymentDao paymentdao;
     private final BookingDao bookingdao;
     public PaymentService(PaymentDao paymentdao ,BookingDao bookingdao) {
    	  this.paymentdao=paymentdao;
    	  this.bookingdao=bookingdao;
     }
     // Save Payment
     @Transactional
     public Payment savePayment( Payment payment,int bookingId) {

         Optional<Booking> optionalBooking =
                 bookingdao.getBookingById(bookingId);

         if (optionalBooking.isEmpty()) {
             throw new BookingNotFoundException("Booking Not Found");
         }

         Booking booking = optionalBooking.get();

         payment.setPaymentStatus(PaymentStatus.SUCCESS);;

         Payment savedPayment = paymentdao.savePayment(payment);

         booking.setPayment(savedPayment);

         booking.setStatus(BookingStatus.CONFIRMED);

         bookingdao.updateBooking(booking);

         return savedPayment;
     }

     // Get Payment By Id
     public Optional<Payment> getPaymentById(int id) {
         return paymentdao.getPaymentById(id);
     }

     // Get All Payments
     public List<Payment> getAllPayments() {
         return paymentdao.getAllPayments();
     }

     // Update Payment
     public Payment updatePayment(Payment payment) {
         return paymentdao.updatePayment(payment);
     }

     // Delete Payment
     public void deletePayment(int id) {
         paymentdao.deletePayment(id);
     }
	

}
