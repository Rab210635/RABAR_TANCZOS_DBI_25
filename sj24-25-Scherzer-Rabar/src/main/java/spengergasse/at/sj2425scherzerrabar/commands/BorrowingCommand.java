package spengergasse.at.sj2425scherzerrabar.commands;

import java.time.LocalDate;
import java.util.List;

public record BorrowingCommand(
        String apiKey, String customerApiKey, List<String> copyApiKeys,
        LocalDate fromDate, Integer extendedByDays) { }