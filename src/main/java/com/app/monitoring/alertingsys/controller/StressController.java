package com.app.monitoring.alertingsys.controller;

import com.app.monitoring.alertingsys.cpu.CpuStressService;
import com.app.monitoring.alertingsys.memory.MemoryStressService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stress")
public class StressController {

    private final CpuStressService cpuStressService;
    private final MemoryStressService memoryStressService;

    public StressController(CpuStressService cpuStressService, MemoryStressService memoryStressService) {
        this.cpuStressService = cpuStressService;
        this.memoryStressService = memoryStressService;
    }

    @PostMapping("/cpu/start")
    public void startCpu(@RequestParam int threads) {
        cpuStressService.start(threads);
    }

    @PostMapping("/cpu/stop")
    public void stopCpu() {
        cpuStressService.stop();
    }

    @PostMapping("/memory/allocate")
    public void allocateMemory(@RequestParam int mb) {
        memoryStressService.allocate(mb);
    }

    @PostMapping("/memory/clear")
    public void clearMemory() {
        memoryStressService.clear();
    }
}
