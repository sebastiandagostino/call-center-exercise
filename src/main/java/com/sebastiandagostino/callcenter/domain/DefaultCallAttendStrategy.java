package com.sebastiandagostino.callcenter.domain;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DefaultCallAttendStrategy
 * <p>
 * This strategy returns the first available operator employee.
 * If all operator employees are busy, it returns the first available supervisor employee.
 * If all supervisor employees are busy, it returns the first available director employee.
 * If all employees are busy, it returns null.
 */
public class DefaultCallAttendStrategy implements CallAttendStrategy {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCallAttendStrategy.class);

    @Override
    public Employee findEmployee(Collection<Employee> employeeList) {
        Validate.notNull(employeeList);
        List<Employee> availableEmployees = employeeList.stream().filter(e -> e.getEmployeeState() == EmployeeState.AVAILABLE).collect(Collectors.toList());
        logger.info("Available operators: " + availableEmployees.size());
        Optional<Employee> employee = availableEmployees.stream().filter(e -> e.getEmployeeType() == EmployeeType.OPERATOR).findAny();
        if (!employee.isPresent()) {
            logger.info("No available operators found");
            employee = availableEmployees.stream().filter(e -> e.getEmployeeType() == EmployeeType.SUPERVISOR).findAny();
            if (!employee.isPresent()) {
                logger.info("No available supervisors found");
                employee = availableEmployees.stream().filter(e -> e.getEmployeeType() == EmployeeType.DIRECTOR).findAny();
                if (!employee.isPresent()) {
                    logger.info("No available directors found");
                    return null;
                }
            }
        }
        logger.info("Employee of type " + employee.get().getEmployeeType() + " found");
        return employee.get();
    }

}
