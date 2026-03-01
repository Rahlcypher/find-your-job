package com.fij.controllers;

import com.fij.dto.ReportRequest;
import com.fij.models.Report;
import com.fij.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<Void> reportUser(@RequestBody ReportRequest request) {
        reportService.reportUser(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-reports")
    public ResponseEntity<List<Report>> getMyReports() {
        return ResponseEntity.ok(reportService.getMyReports());
    }
}
