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
    private final com.coffeeshop.repository.OrderRepository orderRepository;
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

    @Transactional
    public DiningTable createTable(DiningTable table) {
        if (table == null || table.getTableNumber() == null || table.getTableNumber().trim().isEmpty()) {
            throw new BadRequestException("Số hiệu bàn không được để trống");
        }
        if (diningTableRepository.findByTableNumber(table.getTableNumber().trim()).isPresent()) {
            throw new BadRequestException("Số hiệu bàn '" + table.getTableNumber() + "' đã tồn tại");
        }
        if (table.getArea() == null || table.getArea().trim().isEmpty()) {
            table.setArea("Tầng 1 (Trong nhà)");
        }
        if (table.getStatus() == null || !VALID_STATUSES.contains(table.getStatus().toUpperCase())) {
            table.setStatus("EMPTY");
        }
        if (table.getCapacity() == null || table.getCapacity() <= 0) {
            table.setCapacity(4);
        }
        return diningTableRepository.save(table);
    }

    @Transactional
    public DiningTable updateTable(Integer id, DiningTable details) {
        DiningTable table = getTableById(id);
        if (details.getTableNumber() != null && !details.getTableNumber().trim().isEmpty()) {
            String newNumber = details.getTableNumber().trim();
            if (!newNumber.equalsIgnoreCase(table.getTableNumber()) &&
                diningTableRepository.findByTableNumber(newNumber).isPresent()) {
                throw new BadRequestException("Số hiệu bàn '" + newNumber + "' đã được sử dụng bởi bàn khác");
            }
            table.setTableNumber(newNumber);
        }
        if (details.getCapacity() != null && details.getCapacity() > 0) {
            table.setCapacity(details.getCapacity());
        }
        if (details.getArea() != null && !details.getArea().trim().isEmpty()) {
            table.setArea(details.getArea().trim());
        }
        if (details.getStatus() != null && VALID_STATUSES.contains(details.getStatus().toUpperCase())) {
            table.setStatus(details.getStatus().toUpperCase());
        }
        return diningTableRepository.save(table);
    }

    @Transactional
    public void deleteTable(Integer id) {
        DiningTable table = getTableById(id);
        if (orderRepository.existsByTableId(id)) {
            throw new BadRequestException("Không thể xóa bàn '" + table.getTableNumber() + "' vì đã có đơn hàng liên kết. Hãy đổi trạng thái bàn sang trạng thái khác nếu muốn ngưng sử dụng.");
        }
        diningTableRepository.delete(table);
    }
}
