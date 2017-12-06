package com.sebastiandagostino.callcenter.domain;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultCallAttendStrategyTest {

    private CallAttendStrategy callAttendStrategy;

    public DefaultCallAttendStrategyTest() {
        this.callAttendStrategy = new DefaultCallAttendStrategy();
    }

    @Test
    public void testAssignToOperator() {
        Employee operator = Employee.buildOperator();
        Employee supervisor = Employee.buildSupervisor();
        Employee director = Employee.buildDirector();
        List<Employee> employeeList = Arrays.asList(operator, supervisor, director);

        Employee employee = this.callAttendStrategy.findEmployee(employeeList);

        assertNotNull(employee);
        assertEquals(EmployeeType.OPERATOR, employee.getEmployeeType());
    }

    @Test
    public void testAssignToSupervisor() {
        Employee operator = mock(Employee.class);
        when(operator.getEmployeeState()).thenReturn(EmployeeState.BUSY);
        Employee supervisor = Employee.buildSupervisor();
        Employee director = Employee.buildDirector();
        List<Employee> employeeList = Arrays.asList(operator, supervisor, director);

        Employee employee = this.callAttendStrategy.findEmployee(employeeList);

        assertNotNull(employee);
        assertEquals(EmployeeType.SUPERVISOR, employee.getEmployeeType());
    }

    @Test
    public void testAssignToDirector() {
        Employee operator = mockBusyEmployee(EmployeeType.OPERATOR);
        Employee supervisor = mockBusyEmployee(EmployeeType.SUPERVISOR);
        Employee director = Employee.buildDirector();
        List<Employee> employeeList = Arrays.asList(operator, supervisor, director);

        Employee employee = this.callAttendStrategy.findEmployee(employeeList);

        assertNotNull(employee);
        assertEquals(EmployeeType.DIRECTOR, employee.getEmployeeType());
    }

    @Test
    public void testAssignToNone() {
        Employee operator = mockBusyEmployee(EmployeeType.OPERATOR);
        Employee supervisor = mockBusyEmployee(EmployeeType.SUPERVISOR);
        Employee director = mockBusyEmployee(EmployeeType.DIRECTOR);
        List<Employee> employeeList = Arrays.asList(operator, supervisor, director);

        Employee employee = this.callAttendStrategy.findEmployee(employeeList);

        assertNull(employee);
    }

    private static Employee mockBusyEmployee(EmployeeType employeeType) {
        Employee employee = mock(Employee.class);
        when(employee.getEmployeeType()).thenReturn(employeeType);
        when(employee.getEmployeeState()).thenReturn(EmployeeState.BUSY);
        return employee;
    }

}
