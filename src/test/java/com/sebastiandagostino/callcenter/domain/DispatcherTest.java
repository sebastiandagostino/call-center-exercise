package com.sebastiandagostino.callcenter.domain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DispatcherTest {

    private static final int CALL_AMOUNT = 10;

    private static final int MIN_CALL_DURATION = 5;

    private static final int MAX_CALL_DURATION = 10;

    @Test(expected = NullPointerException.class)
    public void testDispatcherCreationWithNullEmployees() {
        new Dispatcher(null);
    }

    @Test(expected = NullPointerException.class)
    public void testDispatcherCreationWithNullStrategy() {
        new Dispatcher(new ArrayList<>(), null);
    }

    @Test
    public void testDispatchCallsToEmployees() throws InterruptedException {
        List<Employee> employeeList = buildEmployeeList();
        Dispatcher dispatcher = new Dispatcher(employeeList);
        dispatcher.start();
        TimeUnit.SECONDS.sleep(1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(dispatcher);
        TimeUnit.SECONDS.sleep(1);

        buildCallList().stream().forEach(call -> {
            dispatcher.dispatch(call);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                fail();
            }
        });

        executorService.awaitTermination(MAX_CALL_DURATION * 2, TimeUnit.SECONDS);
        assertEquals(CALL_AMOUNT, employeeList.stream().mapToInt(employee -> employee.getAttendedCalls().size()).sum());
    }

    private static List<Employee> buildEmployeeList() {
        Employee operator1 = Employee.buildOperator();
        Employee operator2 = Employee.buildOperator();
        Employee operator3 = Employee.buildOperator();
        Employee operator4 = Employee.buildOperator();
        Employee operator5 = Employee.buildOperator();
        Employee operator6 = Employee.buildOperator();
        Employee supervisor1 = Employee.buildSupervisor();
        Employee supervisor2 = Employee.buildSupervisor();
        Employee supervisor3 = Employee.buildSupervisor();
        Employee director = Employee.buildDirector();
        return Arrays.asList(operator1, operator2, operator3, operator4, operator5, operator6,
                supervisor1, supervisor2, supervisor3, director);
    }

    private static List<Call> buildCallList() {
        return Call.buildListOfRandomCalls(CALL_AMOUNT, MIN_CALL_DURATION, MAX_CALL_DURATION);
    }

}
