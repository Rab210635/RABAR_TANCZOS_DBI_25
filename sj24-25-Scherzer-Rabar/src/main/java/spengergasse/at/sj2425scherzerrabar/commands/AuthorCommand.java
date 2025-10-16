package spengergasse.at.sj2425scherzerrabar.commands;

import java.util.List;

public record AuthorCommand(
        String apiKey,
        String penname,
        List<String> address,
        String firstname,
        String lastname,
        String emailAddress
)
{}
