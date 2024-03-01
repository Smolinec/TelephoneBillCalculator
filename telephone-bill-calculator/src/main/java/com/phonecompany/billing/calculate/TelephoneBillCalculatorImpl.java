package com.phonecompany.billing.calculate;

import com.phonecompany.billing.data.Call;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator{

    public static final int PROMO_MIN_CALLS = 1;
    public static final String CALL_DATE_TIME_PATTERN = "dd-MM-yyyy HH:mm:ss";
    public static final int WORKING_HOURS_END = 16;
    public static final int WORKING_HOURS_START = 8;
    public static final double OFF_PEAK_RATE = 0.5;
    public static final double CALL_COST_DISCOUNT = 0.2;
    public static final int MAX_CALL_MINUTES = 5;
    public static final int DECIMAL_SCALE = 2;
    public static final LocalTime WORKING_HOURS_PEAK_TIME_START = LocalTime.of(8, 0);
    public static final LocalTime WORKING_HOURS_PEAK_END = LocalTime.of(16, 0);

    @Override
    public BigDecimal calculate(String phoneLog) {
        if (StringUtils.isEmpty(phoneLog)) return BigDecimal.ZERO;
        List<Call> calls = parseCalls(phoneLog);

        Map<String, Long> callDurations = new HashMap<>();
        calls.forEach(call -> callDurations.merge(call.phoneNumber(), calculateDuration(call), Long::sum));
        String mostCalled = Collections.max(callDurations.entrySet(), Map.Entry.comparingByValue()).getKey();
        boolean applyPromo = callDurations.size() > PROMO_MIN_CALLS;

        BigDecimal totalCost = calls.stream()
                .filter(call -> !applyPromo || !call.phoneNumber().equals(mostCalled))
                .map(this::calculateCallCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalCost;
    }

    private List<Call> parseCalls(String phoneLog) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CALL_DATE_TIME_PATTERN);

        return Arrays.stream(phoneLog.split("\n"))
                .map(entry -> entry.split(","))
                .map(parts -> new Call(parts[0],
                        LocalDateTime.parse(parts[1], formatter),
                        LocalDateTime.parse(parts[2], formatter)))
                .collect(Collectors.toList());
    }

    private BigDecimal calculateCallCost(Call call) {
        LocalDateTime start = call.start();
        LocalDateTime end = call.end();
        BigDecimal cost = BigDecimal.ZERO;

        long peakMinutes = 0;
        long offPeakMinutes = 0;

        if ((start.getHour() < WORKING_HOURS_END && end.getHour() >= WORKING_HOURS_END)
                || (start.getHour() >= WORKING_HOURS_START && end.getHour() < WORKING_HOURS_END)
                || (start.getHour() < WORKING_HOURS_START
                    && end.getHour() >= WORKING_HOURS_START
                    && end.getHour() < WORKING_HOURS_END)
        ) {
            LocalDateTime peakStart = start.getHour() < WORKING_HOURS_START ? LocalDateTime.of(start.toLocalDate(), WORKING_HOURS_PEAK_TIME_START) : start;
            LocalDateTime peakEnd = end.getHour() >= WORKING_HOURS_END ? LocalDateTime.of(end.toLocalDate(), WORKING_HOURS_PEAK_END) : end;

            peakMinutes = Duration.between(peakStart, peakEnd).toMinutes();
            if (start.getHour() < WORKING_HOURS_START) {
                offPeakMinutes += Duration.between(start, LocalDateTime.of(start.toLocalDate(), WORKING_HOURS_PEAK_TIME_START)).toMinutes();
            }
            if (end.getHour() >= WORKING_HOURS_END) {
                offPeakMinutes += Duration.between(LocalDateTime.of(end.toLocalDate(), WORKING_HOURS_PEAK_END), end).toMinutes();
            }
        } else {
            if (start.getHour() >= WORKING_HOURS_START && end.getHour() < WORKING_HOURS_END) {
                peakMinutes = Duration.between(start, end).toMinutes();
            } else {
                offPeakMinutes = Duration.between(start, end).toMinutes();
            }
        }

        cost = cost.add(BigDecimal.valueOf(peakMinutes).multiply(BigDecimal.ONE));
        cost = cost.add(BigDecimal.valueOf(offPeakMinutes).multiply(BigDecimal.valueOf(OFF_PEAK_RATE)));

        long totalMinutes = peakMinutes + offPeakMinutes;
        if (totalMinutes > MAX_CALL_MINUTES) {
            long discountedMinutes = totalMinutes - MAX_CALL_MINUTES;
            BigDecimal baseCost = BigDecimal.valueOf(MAX_CALL_MINUTES).multiply(BigDecimal.ONE);
            BigDecimal additionalCost = BigDecimal.valueOf(discountedMinutes).multiply(BigDecimal.valueOf(CALL_COST_DISCOUNT));
            cost = baseCost.add(additionalCost);
        }

        return cost.setScale(DECIMAL_SCALE);
    }

    private long calculateDuration(Call call) {
        LocalDateTime start = call.start();
        LocalDateTime end = call.end();
        return ChronoUnit.MINUTES.between(start, end);
    }
}
