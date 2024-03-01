package com.phonecompany.billing.data;

import java.time.LocalDateTime;

public record Call(String phoneNumber, LocalDateTime start, LocalDateTime end) {
}
