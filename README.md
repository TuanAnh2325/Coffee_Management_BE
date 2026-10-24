# Coffee Management Backend (Hệ Thống Quản Lý Quán Cà Phê)

Dự án Backend xây dựng bằng **Spring Boot 3.2.5** (Java 17), **Spring Data JPA**, **MySQL 8.0**.

---

## 🚀 Hướng Dẫn Cài Đặt & Khởi Chạy

### 1. Yêu cầu môi trường
* **Java 17** trở lên
* **Maven 3.8+**
* **MySQL 8.0**

### 2. Khởi tạo Cơ sở dữ liệu (Database)
Hệ thống sử dụng bảng mã **UTF-8 (`utf8mb4_unicode_ci`)** để hiển thị chuẩn tiếng Việt có dấu.

Để khởi tạo database và nạp toàn bộ dữ liệu mẫu chuẩn tiếng Việt:

**Cách 1: Sử dụng MySQL Command Line**
```powershell
# Chuyển cửa sổ lệnh sang UTF-8 (nếu dùng Windows)
chcp 65001

# Import file khởi tạo
mysql -u root -p --default-character-set=utf8mb4 < coffee_management_init.sql
```

**Cách 2: Sử dụng DBeaver / MySQL Workbench**
* Mở file [coffee_management_init.sql](coffee_management_init.sql) với encoding **UTF-8**.
* Bấm **Execute Script (Ctrl + Shift + Enter)** để tạo toàn bộ bảng và dữ liệu mẫu.

### 3. Cấu hình Biến môi trường
Tạo file `.env` tại thư mục gốc của dự án (tham khảo file [.env.example](.env.example)):
```env
SERVER_PORT=8080
DB_HOST=localhost
DB_PORT=3306
DB_NAME=coffee_management
DB_USERNAME=root
DB_PASSWORD=your_password
```

### 4. Khởi chạy ứng dụng
```bash
mvn clean spring-boot:run
```
Backend sẽ khởi chạy tại: `http://localhost:8080`

### 5. Tài khoản đăng nhập mẫu
* **Admin (Chủ quán):** `admin` / `123456`
* **Nhân viên thu ngân:** `cashier` / `123456`
