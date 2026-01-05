package spengergasse.at.sj2425scherzerrabar.presentation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spengergasse.at.sj2425scherzerrabar.service.PerformanceComparisonService;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceComparisonController {

    private final PerformanceComparisonService performanceService;

    @GetMapping("/run")
    public String runTest() {
        return performanceService.runComparisonTable();
    }
}