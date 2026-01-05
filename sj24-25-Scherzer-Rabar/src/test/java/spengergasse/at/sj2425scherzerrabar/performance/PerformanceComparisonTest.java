package spengergasse.at.sj2425scherzerrabar.performance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import spengergasse.at.sj2425scherzerrabar.service.PerformanceComparisonService;

@SpringBootTest
// Falls du ein spezielles Profil für Tests hast (z.B. für Testcontainers), aktiviere es hier:
// @ActiveProfiles("test")
public class PerformanceComparisonTest {

    @Autowired
    private PerformanceComparisonService performanceComparisonService;

    @Test
    void runPerformanceComparison() {
        System.out.println("----------------------------------------------------------------");
        System.out.println("STARTING PERFORMANCE COMPARISON");
        System.out.println("----------------------------------------------------------------");

        // Führt den Vergleich durch und gibt die Tabelle zurück/aus
        String resultTable = performanceComparisonService.runComparisonTable();

        System.out.println("\n\n" + resultTable + "\n\n");

        System.out.println("----------------------------------------------------------------");
        System.out.println("FINISHED PERFORMANCE COMPARISON");
        System.out.println("----------------------------------------------------------------");
    }
}
