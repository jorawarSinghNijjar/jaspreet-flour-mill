package com.jaspreetFlourMill.accountManagement.util;

import com.jaspreetFlourMill.accountManagement.model.Customer;

public interface NavigationHandler {
    void handleShowHome();
    void handleShowWheatDeposit(Integer id);
    void handleShowCustomers();
    void handleShowAddTransaction();
    void handleRegisterCustomer();
    void handleEditCustomer(Customer customer);
}
