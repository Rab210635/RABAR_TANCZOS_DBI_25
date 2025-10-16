package spengergasse.at.sj2425scherzerrabar.commands;

import java.util.List;

public record CustomerCommand(
        String apiKey, List<String> addresses, String firstName,
        String lastName, String emailAddress) {
}
