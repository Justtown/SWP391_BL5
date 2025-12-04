# Argo Machine Management System

Dự án quản lý máy móc sử dụng Java Servlets (Jakarta EE 10) với MySQL.

## Công nghệ sử dụng

- **Backend**: Java Servlets (Jakarta EE 10)
- **Database**: MySQL 8.0
- **Build Tool**: Maven
- **Server**: Apache Tomcat 10.1.x
- **Java Version**: JDK 11
- **Frontend**: JSP, Bootstrap 5

## Yêu cầu hệ thống

- JDK 11 hoặc cao hơn
- Apache Tomcat 10.1.x
- MySQL 8.0
- Maven 3.6+
- IntelliJ IDEA (khuyến nghị) hoặc IDE khác

## Cài đặt và chạy project

### 1. Clone project và mở trong IntelliJ

```bash
git clone <repository-url>
cd SWP391_BL5
```

Mở IntelliJ IDEA → File → Open → chọn thư mục project

### 2. Cấu hình Database

1. Mở MySQL Workbench hoặc Command Line
2. Chạy file `db.sql` để tạo database và dữ liệu mẫu:
   ```sql
   source db.sql
   ```
3. Kiểm tra database `argo_managerment_system` đã được tạo

### 3. Cấu hình Database Connection

Mở file `src/main/java/com/mycompany/argomachinemanagement/src/dal/DBContext.java` và sửa thông tin kết nối nếu cần:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/argo_managerment_system";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "123456";
```

### 4. Cấu hình Tomcat trong IntelliJ

1. **Run → Edit Configurations...**
2. Bấm `+` → **Tomcat Server → Local**
3. Tab **Server**:
   - **Tomcat Home**: Chọn thư mục Tomcat 10.1.x của bạn
   - **JRE**: Chọn JDK 11
4. Tab **Deployment**:
   - Bấm `+` → **Artifact...**
   - Chọn `ArgoMachineManagement:war exploded`
   - **Application context**: `/ArgoMachineManagement`
5. **Apply → OK**

### 5. Build và chạy

1. **Build → Rebuild Project** (hoặc `Ctrl+Shift+F9`)
2. Chọn cấu hình Tomcat → **Run** (▶) hoặc `Shift+F10`
3. Đợi Tomcat start xong

### 6. Truy cập ứng dụng

- **Trang List User**: `http://localhost:8080/ArgoMachineManagement/list-user`
- **Trang Login**: `http://localhost:8080/ArgoMachineManagement/view/authen/login.jsp`

## Cấu trúc project

```
SWP391_BL5/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/mycompany/argomachinemanagement/
│   │   │       ├── src/
│   │   │       │   ├── controller/     # Servlets controllers
│   │   │       │   ├── dal/            # Data Access Layer (DAO)
│   │   │       │   ├── dto/            # Data Transfer Objects
│   │   │       │   └── entity/         # Entity classes
│   │   ├── resources/
│   │   └── webapp/
│   │       ├── view/                   # JSP pages
│   │       └── WEB-INF/
│   │           └── web.xml            # Web configuration
├── db.sql                              # Database script
└── pom.xml                             # Maven configuration
```

## Các tính năng hiện có

- ✅ List Users với role
- ✅ Search Users theo keyword
- ✅ Hiển thị status (Active/Deactive)
- ✅ Database connection management

## Troubleshooting

### Lỗi 404 khi truy cập servlet

**Nguyên nhân chính:** Ứng dụng chưa được deploy vào Tomcat

**Các bước khắc phục:**

1. **Kiểm tra Artifact đã được tạo chưa:**
   - **File → Project Structure** (hoặc `Ctrl+Alt+Shift+S`)
   - Tab **Artifacts**
   - Kiểm tra xem có `ArgoMachineManagement:war exploded` không
   - Nếu chưa có:
     - Bấm `+` → **Web Application: Exploded** → **From Modules...**
     - Chọn module `ArgoMachineManagement`
     - **OK**

2. **Kiểm tra Run Configuration:**
   - **Run → Edit Configurations...**
   - Chọn cấu hình Tomcat của bạn
   - Tab **Deployment**
   - Kiểm tra xem có `ArgoMachineManagement:war exploded` trong danh sách **Deploy at the server startup** không
   - Nếu chưa có:
     - Bấm `+` → **Artifact...**
     - Chọn `ArgoMachineManagement:war exploded`
     - **Application context**: `/ArgoMachineManagement` (không có dấu `/` ở cuối)
     - **Apply → OK**

3. **Rebuild và Redeploy:**
   - **Build → Rebuild Project** (hoặc `Ctrl+Shift+F9`)
   - **Build → Build Artifacts → ArgoMachineManagement:war exploded → Rebuild**
   - Stop Tomcat nếu đang chạy
   - Start lại Tomcat

4. **Kiểm tra log Tomcat:**
   - Sau khi start, kiểm tra log có dòng tương tự:
     ```
     Deploying web application directory [...ArgoMachineManagement...]
     ```
   - Nếu không thấy dòng này → ứng dụng chưa được deploy

5. **Kiểm tra thư mục deployment:**
   - Thư mục deployment thường ở: `C:\Users\LOQ\AppData\Local\JetBrains\IntelliJIdea2025.2\tomcat\<id>\webapps\ArgoMachineManagement`
   - Kiểm tra xem có file `WEB-INF/web.xml` và các servlet classes không

### Lỗi kết nối Database

1. Kiểm tra MySQL đang chạy
2. Kiểm tra username/password trong `DBContext.java`
3. Kiểm tra database `argo_managerment_system` đã được tạo chưa

### Lỗi ClassNotFoundException

1. Maven → Reload All Maven Projects
2. Build → Rebuild Project
3. Kiểm tra dependencies trong `pom.xml`

## Tác giả

SWP391 - Block 5

## License

Internal use only
