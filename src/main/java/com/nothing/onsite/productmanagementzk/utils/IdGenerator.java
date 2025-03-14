package com.nothing.onsite.productmanagementzk.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class để sinh ID cho entities
 */
public class IdGenerator {
    private static final AtomicLong deviceIdCounter = new AtomicLong(1);
    private static final AtomicLong deviceDataIdCounter = new AtomicLong(1);
    private static final AtomicLong deviceAlarmIdCounter = new AtomicLong(1);
    
    /**
     * Sinh ID cho Device
     */
    public static long generateDeviceId() {
        return deviceIdCounter.getAndIncrement();
    }
    
    /**
     * Sinh ID cho DeviceData
     */
    public static long generateDeviceDataId() {
        return deviceDataIdCounter.getAndIncrement();
    }
    
    /**
     * Sinh ID cho DeviceAlarm
     */
    public static long generateDeviceAlarmId() {
        return deviceAlarmIdCounter.getAndIncrement();
    }
} 