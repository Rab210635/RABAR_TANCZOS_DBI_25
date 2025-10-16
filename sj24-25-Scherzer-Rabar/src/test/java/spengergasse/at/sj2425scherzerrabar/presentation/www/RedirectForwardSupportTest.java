package spengergasse.at.sj2425scherzerrabar.presentation.www;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RedirectForwardSupportTest {

    // Dummy-Implementierung des Interfaces, um die default-Methoden zu testen
    private final RedirectForwardSupport support = new RedirectForwardSupport() {};

    @Test
    void redirectTo_should_prepend_redirect_and_slash_if_needed() {
        assertEquals("redirect:/foo", support.redirectTo("foo"));
        assertEquals("redirect:/bar", support.redirectTo("/bar"));
    }

    @Test
    void redirectTo_should_throw_exception_when_path_is_null_or_blank() {
        assertThrows(IllegalArgumentException.class, () -> support.redirectTo(null));
        assertThrows(IllegalArgumentException.class, () -> support.redirectTo(""));
        assertThrows(IllegalArgumentException.class, () -> support.redirectTo("   "));
    }

    @Test
    void forwardTo_should_prepend_forward_and_slash_if_needed() {
        assertEquals("forward:/foo", support.forwardTo("foo"));
        assertEquals("forward:/bar", support.forwardTo("/bar"));
    }

    @Test
    void forwardTo_should_throw_exception_when_path_is_null_or_blank() {
        assertThrows(IllegalArgumentException.class, () -> support.forwardTo(null));
        assertThrows(IllegalArgumentException.class, () -> support.forwardTo(""));
        assertThrows(IllegalArgumentException.class, () -> support.forwardTo("   "));
    }
}
