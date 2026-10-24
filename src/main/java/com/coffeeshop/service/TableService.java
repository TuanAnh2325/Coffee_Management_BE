package com.coffeeshop.service;

import com.coffeeshop.entity.DiningTable;
import com.coffeeshop.exception.BadRequestException;
import com.coffeeshop.exception.ResourceNotFoundException;
import com.coffeeshop.repository.DiningTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TableService {

    private final DiningTableRepository diningTableRepository;
    private static final Set<String> VALID_STATUSES = Set.of("EMPTY", "SERVING", "RESERVED");

    public List<DiningTable> getAllTables(String status, String area) {
        if (status != null && !status.isBlank()) {
            return diningTableRepository.findByStatus(status.toUpperCase());
        }
        if (area != null && !area.isBlank()) {
            return diningTableRepository.findByArea(area);
        }
        return diningTableRepository.findAll();
    }

    public DiningTable getTableById(Integer id) {
        return diningTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bàn với ID: " + id));
    }

    @Transactional
    public DiningTable updateTableStatus(Integer id, String status) {
        if (status == null || !VALID_STATUSES.contains(status.toUpperCase())) {
            throw new BadRequestException("Trạng thái bàn không hợp lệ. Chỉ chấp nhận: EMPTY, SERVING, RESERVED");
        }

        DiningTable table = getTableById(id);
        table.setStatus(status.toUpperCase());
        return diningTableRepository.save(table);
    }
}
