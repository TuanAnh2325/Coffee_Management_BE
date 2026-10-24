-- ============================================================
-- COFFEE SHOP MANAGEMENT SYSTEM - DATABASE INITIALIZATION SCRIPT
-- Bang ma: UTF-8 (utf8mb4_unicode_ci)
-- Ho tro day du tieng Viet co dau, emoji va ky tu dac biet
-- ============================================================

SET NAMES 'utf8mb4';
SET CHARACTER SET utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `coffee_management`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `coffee_management`;

-- 1. Bang Vai tro (roles)
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính vai trò',
  `name` VARCHAR(50) NOT NULL COMMENT 'Tên vai trò (ROLE_ADMIN, ROLE_STAFF)',
  `description` VARCHAR(255) DEFAULT NULL COMMENT 'Mô tả quyền hạn vai trò',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_roles_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh sách vai trò người dùng trong hệ thống';

-- 2. Bang Tai khoan nguoi dung (users)
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính tài khoản',
  `username` VARCHAR(50) NOT NULL COMMENT 'Tên đăng nhập hệ thống',
  `password_hash` VARCHAR(255) NOT NULL COMMENT 'Mật khẩu đã mã hóa BCrypt',
  `full_name` VARCHAR(100) NOT NULL COMMENT 'Họ và tên nhân viên / quản lý',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT 'Số điện thoại liên hệ',
  `email` VARCHAR(100) DEFAULT NULL COMMENT 'Địa chỉ email',
  `role_id` INT NOT NULL COMMENT 'Mã vai trò của người dùng',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'Trạng thái hoạt động (1: Hoạt động, 0: Khóa)',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo tài khoản',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời điểm cập nhật',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_username` (`username`),
  KEY `fk_users_roles` (`role_id`),
  CONSTRAINT `fk_users_roles` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh sách tài khoản nhân viên và quản lý';

-- 3. Bang Danh muc san pham (categories)
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính danh mục',
  `code` VARCHAR(50) NOT NULL COMMENT 'Mã danh mục (COFFEE, TEA, JUICE, CAKE)',
  `name` VARCHAR(100) NOT NULL COMMENT 'Tên danh mục hiển thị',
  `description` VARCHAR(255) DEFAULT NULL COMMENT 'Mô tả ngắn gọn về danh mục',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'Trạng thái kinh doanh (1: Đang bán, 0: Tạm ngưng)',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo danh mục',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_categories_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh mục đồ uống và bánh ngọt';

-- 4. Bang Mon / San pham (products)
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính món',
  `category_id` INT NOT NULL COMMENT 'Thuộc danh mục nào',
  `code` VARCHAR(50) NOT NULL COMMENT 'Mã món (CF-DEN, CF-SUA, TRA-DAO)',
  `name` VARCHAR(150) NOT NULL COMMENT 'Tên món đồ uống hoặc bánh',
  `base_price` DECIMAL(12,2) NOT NULL COMMENT 'Giá bán niêm yết chuẩn size M',
  `image_url` VARCHAR(255) DEFAULT NULL COMMENT 'Đường dẫn ảnh minh họa món',
  `is_available` TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'Trạng thái phục vụ (1: Sẵn sàng, 0: Hết món)',
  `description` TEXT DEFAULT NULL COMMENT 'Mô tả chi tiết thành phần, hương vị món',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo món',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời điểm cập nhật',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_products_code` (`code`),
  KEY `fk_products_categories` (`category_id`),
  CONSTRAINT `fk_products_categories` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh sách sản phẩm đồ uống và đồ ăn nhẹ';

-- 5. Bang Kich co ly (product_sizes)
DROP TABLE IF EXISTS `product_sizes`;
CREATE TABLE `product_sizes` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính kích cỡ ly',
  `size_name` VARCHAR(10) NOT NULL COMMENT 'Tên kích cỡ (S, M, L)',
  `extra_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Phụ thu cộng thêm theo size',
  `description` VARCHAR(100) DEFAULT NULL COMMENT 'Định lượng ly (Nhỏ 350ml, Vừa 500ml, Lớn 700ml)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Các loại kích cỡ ly đồ uống và giá phụ thu';

-- 6. Bang Ban phuc vu (dining_tables)
DROP TABLE IF EXISTS `dining_tables`;
CREATE TABLE `dining_tables` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính bàn',
  `table_number` VARCHAR(20) NOT NULL COMMENT 'Mã hoặc tên bàn (Bàn 01, Bàn VIP 01)',
  `area` VARCHAR(50) NOT NULL COMMENT 'Khu vực đặt bàn (Tầng 1, Tầng 2, Sân vườn)',
  `status` VARCHAR(20) NOT NULL DEFAULT 'EMPTY' COMMENT 'Trạng thái bàn (EMPTY: Trống, SERVING: Có khách, RESERVED: Đã đặt trước)',
  `capacity` INT NOT NULL DEFAULT 4 COMMENT 'Số lượng chỗ ngồi tối đa',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dining_tables_number` (`table_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Danh sách bàn phục vụ khách tại quán';

-- 7. Bang Kho nguyen lieu / San pham (inventory_items)
DROP TABLE IF EXISTS `inventory_items`;
CREATE TABLE `inventory_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính kho',
  `product_id` BIGINT NOT NULL COMMENT 'Thuộc sản phẩm nào',
  `quantity_in_stock` INT NOT NULL DEFAULT 100 COMMENT 'Số lượng tồn kho thực tế hiện tại',
  `min_stock_threshold` INT NOT NULL DEFAULT 10 COMMENT 'Định mức tồn tối thiểu cần nhập thêm',
  `unit` VARCHAR(20) NOT NULL COMMENT 'Đơn vị tính (ly, bánh, phần)',
  `last_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời điểm cập nhật tồn kho',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inventory_product` (`product_id`),
  CONSTRAINT `fk_inventory_products` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Quản lý kho tồn món pha chế';

-- 8. Bang Don hang (orders)
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính đơn hàng',
  `order_code` VARCHAR(50) NOT NULL COMMENT 'Mã hóa đơn duy nhất (VD: ORD-20261024-001)',
  `user_id` BIGINT NOT NULL COMMENT 'Nhân viên thu ngân lập đơn',
  `table_id` INT DEFAULT NULL COMMENT 'Bàn phục vụ (NULL nếu khách mua mang về)',
  `order_type` VARCHAR(20) NOT NULL DEFAULT 'DINE_IN' COMMENT 'Hình thức (DINE_IN: Dùng tại quán, TAKE_AWAY: Mang về)',
  `order_status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'Trạng thái đơn (PENDING: Chờ xử lý, COMPLETED: Hoàn thành, CANCELLED: Đã hủy)',
  `payment_status` VARCHAR(20) NOT NULL DEFAULT 'UNPAID' COMMENT 'Thanh toán (UNPAID: Chưa trả tiền, PAID: Đã thanh toán)',
  `payment_method` VARCHAR(30) NOT NULL DEFAULT 'CASH' COMMENT 'Phương thức (CASH: Tiền mặt, BANKING: Chuyển khoản QR, CARD: Thẻ POS)',
  `total_amount` DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT 'Tổng số tiền khách phải thanh toán',
  `note` VARCHAR(255) DEFAULT NULL COMMENT 'Ghi chú cho đơn hàng',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm tạo đơn',
  `completed_at` TIMESTAMP NULL DEFAULT NULL COMMENT 'Thời điểm hoàn thành thanh toán',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_orders_order_code` (`order_code`),
  KEY `fk_orders_users` (`user_id`),
  KEY `fk_orders_tables` (`table_id`),
  CONSTRAINT `fk_orders_tables` FOREIGN KEY (`table_id`) REFERENCES `dining_tables` (`id`),
  CONSTRAINT `fk_orders_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Thông tin hóa đơn bán lẻ tại quầy';

-- 9. Bang Chi tiet don hang (order_items)
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính chi tiết món trong đơn',
  `order_id` BIGINT NOT NULL COMMENT 'Thuộc đơn hàng nào',
  `product_id` BIGINT NOT NULL COMMENT 'Mã món được chọn',
  `product_name` VARCHAR(150) NOT NULL COMMENT 'Snapshot tên món tại thời điểm gọi',
  `size_name` VARCHAR(10) NOT NULL DEFAULT 'M' COMMENT 'Snapshot kích cỡ ly (S, M, L)',
  `unit_price` DECIMAL(12,2) NOT NULL COMMENT 'Snapshot đơn giá tại thời điểm gọi',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT 'Số lượng ly/món gọi',
  `subtotal` DECIMAL(12,2) NOT NULL COMMENT 'Thành tiền = quantity * unit_price',
  `item_note` VARCHAR(255) DEFAULT NULL COMMENT 'Ghi chú pha chế: Ít ngọt, nhiều đá, không sữa...',
  PRIMARY KEY (`id`),
  KEY `fk_order_items_orders` (`order_id`),
  KEY `fk_order_items_products` (`product_id`),
  CONSTRAINT `fk_order_items_orders` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_order_items_products` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Chi tiết danh sách món ăn/thức uống theo từng đơn hàng';

-- ============================================================
-- DỮ LIỆU MẪU BAN ĐẦU (SEED DATA CHUẨN TIẾNG VIỆT UTF-8)
-- ============================================================

-- 1. Vai trò
INSERT INTO `roles` (`id`, `name`, `description`) VALUES
(1, 'ROLE_ADMIN', 'Quản trị viên / Chủ quán có toàn quyền hệ thống'),
(2, 'ROLE_STAFF', 'Nhân viên thu ngân / Bán hàng tại quầy');

-- 2. Người dùng mẫu (Mật khẩu mặc định: 123456)
INSERT INTO `users` (`id`, `username`, `password_hash`, `full_name`, `phone`, `email`, `role_id`, `is_active`) VALUES
(1, 'admin', '$2a$10$7ZeqR2H1L7jWd8lX1YJdwe8tVl4r8FqG1bZ4uYjW8lX1YJdwe8tVl', 'Quản trị viên', '0901234567', 'admin@coffeeshop.com', 1, 1),
(2, 'cashier', '$2a$10$7ZeqR2H1L7jWd8lX1YJdwe8tVl4r8FqG1bZ4uYjW8lX1YJdwe8tVl', 'Nguyễn Văn Thu Ngân', '0908765432', 'thungan@coffeeshop.com', 2, 1);

-- 3. Danh mục sản phẩm
INSERT INTO `categories` (`id`, `code`, `name`, `description`, `is_active`) VALUES
(1, 'COFFEE', 'Cà phê truyền thống & Pha máy', 'Cà phê Robusta & Arabica nguyên chất thơm đậm đà', 1),
(2, 'TEA', 'Trà trái cây nhiệt đới', 'Trà hoa quả thanh mát kết hợp trái cây tươi giòn ngọt', 1),
(3, 'FREEZE', 'Đá xay & Sinh tố', 'Thức uống đá xay béo ngậy kèm kem tươi whipping', 1),
(4, 'CAKE', 'Bánh ngọt & Điểm tâm', 'Bánh nướng tươi mỗi ngày ăn kèm đồ uống', 1);

-- 4. Kích cỡ ly
INSERT INTO `product_sizes` (`id`, `size_name`, `extra_price`, `description`) VALUES
(1, 'S', 0.00, 'Ly nhỏ 350ml'),
(2, 'M', 6000.00, 'Ly vừa 500ml (Tiêu chuẩn)'),
(3, 'L', 10000.00, 'Ly lớn 700ml');

-- 5. Danh sách Món
INSERT INTO `products` (`id`, `category_id`, `code`, `name`, `base_price`, `image_url`, `is_available`, `description`) VALUES
(1, 1, 'CF-DEN', 'Cà phê Đen đá', 25000.00, 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=400', 1, 'Cà phê Robusta nguyên chất đậm vị truyền thống'),
(2, 1, 'CF-SUA', 'Cà phê Sữa đá', 29000.00, 'https://images.unsplash.com/photo-1517701604599-bb29b565090c?w=400', 1, 'Cà phê hòa quyện sữa đặc Ông Thọ thơm ngọt béo ngậy'),
(3, 1, 'BAC-XIU', 'Bạc xỉu 3 tầng', 32000.00, 'https://images.unsplash.com/photo-1541167760496-1628856ab772?w=400', 1, 'Nhiều sữa ít cà phê, béo ngậy thơm ngon'),
(4, 2, 'TRA-DAO', 'Trà Đào Cam Sả', 39000.00, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400', 1, 'Trà đào thơm nồng kết hợp hương cam vàng và sả tươi'),
(5, 2, 'TRA-VAI', 'Trà Vải Hoa Lài', 39000.00, 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400', 1, 'Trà hoa lài thanh tao cùng những quả vải giòn ngọt mọng nước'),
(6, 3, 'FREEZE-MATCHA', 'Matcha Đá Xay Thạch', 45000.00, 'https://images.unsplash.com/photo-1536256263959-770b48d82b0a?w=400', 1, 'Trà xanh Nhật Bản đá xay phủ kem tươi béo ngậy và thạch giòn'),
(7, 4, 'CAKE-TIRAMISU', 'Bánh Tiramisu Ý', 35000.00, 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400', 1, 'Bánh mềm mịn thơm hương cà phê và phô mai mascarpone');

-- 6. Danh sách Bàn phục vụ
INSERT INTO `dining_tables` (`id`, `table_number`, `area`, `status`, `capacity`) VALUES
(1, 'Bàn 01', 'Tầng 1 (Trong nhà)', 'SERVING', 4),
(2, 'Bàn 02', 'Tầng 1 (Trong nhà)', 'SERVING', 4),
(3, 'Bàn 03', 'Tầng 1 (Trong nhà)', 'SERVING', 4),
(4, 'Bàn 04', 'Tầng 1 (Trong nhà)', 'EMPTY', 2),
(5, 'Bàn 05', 'Tầng 2 (Máy lạnh)', 'RESERVED', 6),
(6, 'Bàn 06', 'Tầng 2 (Máy lạnh)', 'EMPTY', 4),
(7, 'Bàn 07', 'Tầng 2 (Máy lạnh)', 'EMPTY', 4),
(8, 'Bàn 08', 'Sân vườn ngoài trời', 'EMPTY', 4),
(9, 'Bàn 09', 'Sân vườn ngoài trời', 'EMPTY', 8),
(10, 'Bàn 10', 'Sân vườn ngoài trời', 'SERVING', 6);

-- 7. Kho nguyên liệu
INSERT INTO `inventory_items` (`id`, `product_id`, `quantity_in_stock`, `min_stock_threshold`, `unit`) VALUES
(1, 1, 150, 20, 'ly'),
(2, 2, 200, 20, 'ly'),
(3, 3, 120, 15, 'ly'),
(4, 4, 95, 15, 'ly'),
(5, 5, 80, 15, 'ly'),
(6, 6, 60, 10, 'ly'),
(7, 7, 30, 5, 'phần');

SET FOREIGN_KEY_CHECKS = 1;
