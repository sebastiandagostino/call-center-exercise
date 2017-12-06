package com.sebastiandagostino.callcenter.domain;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Dispatcher implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    public static final Integer MAX_THREADS = 10;

    private Boolean active;

    private ExecutorService executorService;

    private ConcurrentLinkedDeque<Employee> employees;

    private ConcurrentLinkedDeque<Call> incomingCalls;

    private CallAttendStrategy callAttendStrategy;

    public Dispatcher(List<Employee> employees) {
        this(employees, new DefaultCallAttendStrategy());
    }

    public Dispatcher(List<Employee> employees, CallAttendStrategy callAttendStrategy) {
        Validate.notNull(employees);
        Validate.notNull(callAttendStrategy);
        this.employees = new ConcurrentLinkedDeque(employees);
        this.callAttendStrategy = callAttendStrategy;
        this.incomingCalls = new ConcurrentLinkedDeque<>();
        this.executorService = Executors.newFixedThreadPool(MAX_THREADS);
    }

    public synchronized void dispatch(Call call) {
        logger.info("Dispatch new call of " + call.getDurationInSeconds() + " seconds");
        this.incomingCalls.add(call);
    }

    /**
     * Starts the employee threads and allows the dispatcher run method to execute
     */
    public synchronized void start() {
        this.active = true;
        for (Employee employee : this.employees) {
            this.executorService.execute(employee);
        }
    }

    /**
     * Stops the employee threads and the dispatcher run method immediately
     */
    public synchronized void stop() {
        this.active = false;
        this.executorService.shutdown();
    }

    public synchronized Boolean getActive() {
        return active;
    }

    /**
     * This is the method that runs on the thread.
     * If the incoming calls queue is not empty, then it searches for and available employee to take the call.
     * Calls will queue up until some workers becomes available.
     */
    @Override
    public void run() {
        while (getActive()) {
            if (this.incomingCalls.isEmpty()) {
                continue;
            } else {
                Employee employee = this.callAttendStrategy.findEmployee(this.employees);
                if (employee == null) {
                    continue;
                }
                Call call = this.incomingCalls.poll();
                try {
                    employee.attend(call);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    this.incomingCalls.addFirst(call);
                }
            }
        }
    }

}
