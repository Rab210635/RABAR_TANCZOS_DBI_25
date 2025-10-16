package spengergasse.at.sj2425scherzerrabar.commands;

import java.time.LocalDate;
import java.util.List;

public record BookCommand(
        String apiKey, String name, LocalDate releaseDate,
        Boolean availableOnline, List<String> types, Integer wordCount,
        String description, List<String> authorIds, List<String> genre) {}



