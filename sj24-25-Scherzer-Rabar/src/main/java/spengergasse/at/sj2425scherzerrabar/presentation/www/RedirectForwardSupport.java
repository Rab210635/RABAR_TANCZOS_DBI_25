package spengergasse.at.sj2425scherzerrabar.presentation.www;

public interface RedirectForwardSupport {
    default  String redirectTo(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Redirect-Pfad must not be empty");
        }
        return "redirect:" + ensureLeadingSlash(path);
    }

    default  String forwardTo(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Forward-Pfad must not be empty");
        }
        return "forward:" + ensureLeadingSlash(path);
    }

    private static String ensureLeadingSlash(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }
}
