package com.sebastiandagostino.callcenter.domain;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

/**
 * Models the Employee Domain Objects
 */
public class Employee implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Employee.class);

    private EmployeeType employeeType;

    private EmployeeState employeeState;

    private ConcurrentLinkedDeque<Call> incomingCalls;

    private ConcurrentLinkedDeque<Call> attendedCalls;

    public Employee(EmployeeType employeeType) {
        Validate.notNull(employeeType);
        this.employeeType = employeeType;
        this.employeeState = EmployeeState.AVAILABLE;
        this.incomingCalls = new ConcurrentLinkedDeque<>();
        this.attendedCalls = new ConcurrentLinkedDeque<>();
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public synchronized EmployeeState getEmployeeState() {
        return employeeState;
    }

    private synchronized void setEmployeeState(EmployeeState employeeState) {
        logger.info("Employee " + Thread.currentThread().getName() + " changes its state to " + employeeState);
        this.employeeState = employeeState;
    }

    public synchronized List<Call> getAttendedCalls() {
        return new ArrayList<>(attendedCalls);
    }

    /**
     * Queues a call to be attended by the employee
     *
     * @param call call to be attended
     */
    public synchronized void attend(Call call) {
        logger.info("Employee " + Thread.currentThread().getName() + " queues a call of " + call.getDurationInSeconds() + " seconds");
        this.incomingCalls.add(call);
    }

    public static Employee buildOperator() {
        return new Employee(EmployeeType.OPERATOR);
    }

    public static Employee buildSupervisor() {
        return new Employee(EmployeeType.SUPERVISOR);
    }

    public static Employee buildDirector() {
        return new Employee(EmployeeType.DIRECTOR);
    }

    /**
     * This is the method that runs on the thread.
     * If the incoming calls queue is not empty, then it changes its state from AVAILABLE to BUSY, takes the call
     * and when it finishes it changes its state from BUSY back to AVAILABLE. This allows a Thread Pool to decide
     * to dispatch another call to another employee.
     */
    @Override
    public void run() {
        logger.info("Employee " + Thread.currentThread().getName() + " starts to work");
        while (true) {
            if (!this.incomingCalls.isEmpty()) {
                Call call = this.incomingCalls.poll();
                this.setEmployeeState(EmployeeState.BUSY);
                logger.info("Employee " + Thread.currentThread().getName() + " starts working on a call of " + call.getDurationInSeconds() + " seconds");
                try {
                    TimeUnit.SECONDS.sleep(call.getDurationInSeconds());
                } catch (InterruptedException e) {
                    logger.error("Employee " + Thread.currentThread().getName() + " was interrupted and could not finish call of " + call.getDurationInSeconds() + " seconds");
                } finally {
                    this.setEmployeeState(EmployeeState.AVAILABLE);
                }
                this.attendedCalls.add(call);
                logger.info("Employee " + Thread.currentThread().getName() + " finishes a call of " + call.getDurationInSeconds() + " seconds");
            }
        }
    }

}
