package com.learn.kidstinyworld.enums;

public enum Status {
    PENDING,    // Tapşırıq təyin edilib, amma başlamayıb/bitməyib
    COMPLETED,  // Tapşırıq bitirilib
    MISSED      // Məsələn, vaxtı keçib (Kafka tərəfindən idarə oluna bilər)
}