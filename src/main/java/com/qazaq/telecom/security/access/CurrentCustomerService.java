package com.qazaq.telecom.security.access;

import com.qazaq.telecom.account.Account;
import com.qazaq.telecom.customer.Customer;
import com.qazaq.telecom.customer.CustomerRepository;
import com.qazaq.telecom.payment.Payment;
import com.qazaq.telecom.simcard.SimCard;
import com.qazaq.telecom.subscription.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class CurrentCustomerService {

    private final CustomerRepository customerRepository;

    public Customer getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Unauthorized");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            throw new AuthenticationCredentialsNotFoundException("Unauthorized");
        }

        return customerRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Unauthorized"));
    }

    public Customer requireCustomer(Long customerId) {
        Customer currentCustomer = getCurrentCustomer();
        if (!currentCustomer.getId().equals(customerId)) {
            throw new AccessDeniedException("Access denied");
        }
        return currentCustomer;
    }

    public Account requireAccount(Long customerId) {
        return requireCustomer(customerId).getAccount();
    }

    public void ensureSimCardOwner(SimCard simCard) {
        Customer currentCustomer = getCurrentCustomer();
        if (simCard == null || simCard.getCustomer() == null || simCard.getCustomer().getId() == null
                || !currentCustomer.getId().equals(simCard.getCustomer().getId())) {
            throw new AccessDeniedException("Access denied");
        }
    }

    public void ensureSubscriptionOwner(Subscription subscription) {
        if (subscription == null || subscription.getSimCard() == null || subscription.getSimCard().getCustomer() == null) {
            throw new AccessDeniedException("Access denied");
        }
        requireCustomer(subscription.getSimCard().getCustomer().getId());
    }

    public void ensurePaymentOwner(Payment payment) {
        if (payment == null || payment.getAccount() == null || payment.getAccount().getCustomer() == null) {
            throw new AccessDeniedException("Access denied");
        }
        requireCustomer(payment.getAccount().getCustomer().getId());
    }
}
