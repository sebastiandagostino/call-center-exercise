package com.sebastiandagostino.callcenter.domain;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Models the Call Domain Objects
 */
public class Call {

    private Integer durationInSeconds;

    /**
     * Creates a new Call with duration measured in seconds
     *
     * @param durationInSeconds duration in seconds must be equal or greater than zero
     */
    public Call(Integer durationInSeconds) {
        Validate.notNull(durationInSeconds);
        Validate.isTrue(durationInSeconds >= 0);
        this.durationInSeconds = durationInSeconds;
    }

    public Integer getDurationInSeconds() {
        return durationInSeconds;
    }

    /**
     * Builds a new random call
     *
     * @param minDurationInSeconds minimum duration in seconds must be equal or greater than zero
     * @param maxDurationInSeconds maximun duration in seconds must be equal or greater than minDurationInSeconds
     * @return A new random call with a random duration value between minimum and maximum duration
     */
    public static Call buildRandomCall(Integer minDurationInSeconds, Integer maxDurationInSeconds) {
        Validate.isTrue(maxDurationInSeconds >= minDurationInSeconds && minDurationInSeconds >= 0);
        return new Call(ThreadLocalRandom.current().nextInt(minDurationInSeconds, maxDurationInSeconds + 1));
    }

    /**
     * Builds a new random call list
     *
     * @param listSize             amount of random calls to be created
     * @param minDurationInSeconds minimum duration in seconds of each call must be equal or greater than zero
     * @param maxDurationInSeconds maximun duration in seconds of each call must be equal or greater than minDurationInSeconds
     * @return A new list of random calls each with a random duration value between minimum and maximum duration
     */
    public static List<Call> buildListOfRandomCalls(Integer listSize, Integer minDurationInSeconds, Integer maxDurationInSeconds) {
        Validate.isTrue(listSize >= 0);
        List<Call> callList = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            callList.add(buildRandomCall(minDurationInSeconds, maxDurationInSeconds));
        }
        return callList;
    }

}
