package com.railway_system.Ticket.Booking.dao;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.railway_system.Ticket.Booking.entity.Payment;
import com.railway_system.Ticket.Booking.repository.PaymentRepository;

@Repository
public class PaymentDao {

    private final PaymentRepository repository;

    public PaymentDao(PaymentRepository repository) {
        this.repository = repository;
    }

    public Payment savePayment(Payment payment) {
        return repository.save(payment);
    }

    public Optional<Payment> getPaymentById(int id) {
        return repository.findById(id);
    }

    public List<Payment> getAllPayments() {
        return repository.findAll();
    }

    public Payment updatePayment(Payment payment) {
        return repository.save(payment);
    }

    public void deletePayment(int id) {
        repository.deleteById(id);
    }

	

	

}
