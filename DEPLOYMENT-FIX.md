# 🔧 HƯỚNG DẪN FIX LỖI 404 - DEPLOYMENT

## ⚠️ VẤN ĐỀ
Ứng dụng chưa được deploy vào Tomcat. Log Tomcat chỉ thấy manager app, không thấy ArgoMachineManagement.

## ✅ CODE ĐÃ ĐÚNG
- ✅ ListUserController đã được cấu hình đúng
- ✅ web.xml đã có servlet mapping
- ✅ Không có lỗi compile
- ✅ JSP đã sẵn sàng

## 🚀 CÁCH FIX - THỰC HIỆN TỪNG BƯỚC

### BƯỚC 1: Tạo Artifact (QUAN TRỌNG NHẤT)

1. **Mở Project Structure:**
   - Nhấn `Ctrl+Alt+Shift+S` 
   - Hoặc: **File → Project Structure**

2. **Tab Artifacts:**
   - Chọn tab **Artifacts** ở bên trái
   - Kiểm tra xem có `ArgoMachineManagement:war exploded` không

3. **Nếu CHƯA CÓ artifact:**
   - Bấm nút **`+`** ở góc trên bên trái
   - Chọn **Web Application: Exploded**
   - Chọn **From Modules...**
   - Trong popup, chọn module **`ArgoMachineManagement`**
   - Bấm **OK**
   - Đảm bảo trong **Available Elements** có:
     - ✅ `ArgoMachineManagement compile output`
     - ✅ Tất cả file trong `src/main/webapp` (WEB-INF, META-INF, view, index.html, etc.)
   - Bấm **Apply → OK**

### BƯỚC 2: Cấu hình Run Configuration

1. **Mở Edit Configurations:**
   - Nhấn `Shift+Alt+F10` rồi chọn **Edit Configurations...**
   - Hoặc: **Run → Edit Configurations...**

2. **Chọn hoặc tạo Tomcat Server:**
   - Nếu chưa có: Bấm **`+`** → **Tomcat Server → Local**
   - Đặt tên: `Tomcat 10.1 - ArgoMachineManagement`

3. **Tab Server:**
   - **Application server**: Chọn Tomcat 10.1.49 của bạn
   - **JRE**: Chọn JDK 11

4. **Tab Deployment (QUAN TRỌNG):**
   - Trong phần **"Deploy at the server startup"**
   - Kiểm tra xem có **`ArgoMachineManagement:war exploded`** không
   
   **Nếu CHƯA CÓ:**
   - Bấm **`+`** ở góc trên bên phải
   - Chọn **Artifact...**
   - Chọn **`ArgoMachineManagement:war exploded`**
   - **Application context**: `/ArgoMachineManagement` (KHÔNG có dấu `/` ở cuối)
   - Bấm **OK**
   
   **Nếu ĐÃ CÓ nhưng Application context SAI:**
   - Chọn artifact đó
   - Sửa **Application context** thành `/ArgoMachineManagement`
   - Đảm bảo KHÔNG có dấu `/` ở cuối

5. **Apply → OK**

### BƯỚC 3: Build và Deploy

1. **Build Artifact:**
   - **Build → Build Artifacts → ArgoMachineManagement:war exploded → Rebuild**
   - Đợi build xong (kiểm tra console xem có lỗi không)

2. **Stop Tomcat (nếu đang chạy):**
   - Bấm nút **Stop** (■) trong IntelliJ

3. **Deploy và Run:**
   - Chọn cấu hình Tomcat từ dropdown (góc trên bên phải)
   - Bấm nút **Run** (▶) hoặc nhấn `Shift+F10`
   - Đợi Tomcat start xong

### BƯỚC 4: Kiểm tra Deployment

1. **Kiểm tra log Tomcat:**
   - Sau khi start, tìm trong console log dòng:
     ```
     Deploying web application directory [...ArgoMachineManagement...]
     ```
   - Nếu THẤY dòng này → ✅ Deployment thành công!
   - Nếu KHÔNG THẤY → ❌ Ứng dụng chưa được deploy, kiểm tra lại BƯỚC 1 và 2

2. **Test URL:**
   - Mở browser: `http://localhost:8080/ArgoMachineManagement/list-user`
   - Nếu thấy trang list user → ✅ Thành công!

## 🔍 TROUBLESHOOTING

### Nếu vẫn lỗi 404:

1. **Kiểm tra Artifact đã được build:**
   - Vào thư mục `target/ArgoMachineManagement-1.0-SNAPSHOT`
   - Kiểm tra xem có file `WEB-INF/web.xml` và các servlet classes không

2. **Kiểm tra deployment directory:**
   - Thư mục deployment thường ở:
     `C:\Users\LOQ\AppData\Local\JetBrains\IntelliJIdea2025.2\tomcat\<id>\webapps\ArgoMachineManagement`
   - Kiểm tra xem có thư mục này không
   - Nếu có, kiểm tra xem có file `WEB-INF/web.xml` không

3. **Undeploy và deploy lại:**
   - Stop Tomcat
   - Xóa thư mục deployment (nếu có)
   - Build lại artifact
   - Start lại Tomcat

### Nếu có lỗi ClassNotFoundException:

1. **Rebuild Project:**
   - **Build → Rebuild Project** (`Ctrl+Shift+F9`)

2. **Reload Maven:**
   - **Maven → Reload All Maven Projects**

3. **Clean và rebuild:**
   - **Build → Clean Project**
   - **Build → Rebuild Project**

## 📝 LƯU Ý QUAN TRỌNG

- **Application context PHẢI là `/ArgoMachineManagement`** (có dấu `/` ở đầu, KHÔNG có ở cuối)
- **Artifact PHẢI là `war exploded`**, không phải `war` thường
- **Phải build artifact TRƯỚC KHI run** Tomcat
- **Kiểm tra log Tomcat** để xác nhận deployment thành công

