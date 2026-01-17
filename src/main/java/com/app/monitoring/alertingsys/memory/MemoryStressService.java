package com.app.monitoring.alertingsys.memory;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemoryStressService {

    private final List<byte[]> heap = new ArrayList<>();

    public void allocate(int mb) {
        heap.add(new byte[mb * 1024 * 1024]);
    }

    public void clear() {
        heap.clear();
    }
}
