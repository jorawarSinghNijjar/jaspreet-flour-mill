package com.jaspreetFlourMill.accountManagement.util;

import com.jaspreetFlourMill.accountManagement.model.Customer;
import com.jaspreetFlourMill.accountManagement.model.Employee;

public interface NavigationHandler {
    void handleShowHome();
    void handleShowWheatDeposit(Integer id);
    void handleShowCustomers();
    void handleShowAddTransaction();
    void handleRegisterCustomer();
    void handleEditCustomer(Customer customer);
    void handleEditEmployee(Employee employee);
    void handleShowEmployees();
}
