# ĐỒ ÁN #1 – SLANG DICTIONARY  
**Môn học:** CSC13002 – LẬP TRÌNH ỨNG DỤNG JAVA  
**Giảng viên hướng dẫn:** Nguyễn Văn Khiết – Nguyễn Đức Huy – Hồ Tuấn Thanh  
**Sinh viên thực hiện:** Nguyễn Nhật Nam – MSSV 23127092  
**IDE:** IntelliJ IDEA 2025.2  
**Ngôn ngữ:** Java 25 (OpenJDK 25 Temurin)  
**Thư viện:** JavaFX SDK 25.0.1  
**Thời gian thực hiện:** 10 giờ  

---

## 1. Giới thiệu đề tài

Ứng dụng Slang Dictionary giúp người dùng tra cứu, chỉnh sửa và chơi trò đố vui với các slang word (tiếng lóng tiếng Anh).  
Đồ án được phát triển theo hướng JavaFX (GUI), tuân thủ các yêu cầu về OOP, Java IO, Collection Framework, và Exception Handling trong Java.

---

## 2. Mục tiêu và chuẩn đầu ra đạt được

### Mục tiêu
- Vận dụng Java IO để xử lý đọc/ghi file dữ liệu slang.txt.  
- Ứng dụng Collections (HashMap, List) để lưu trữ và tra cứu nhanh.  
- Thiết kế chương trình theo hướng đối tượng (OOP) rõ ràng, có phân tầng.  
- Sử dụng JavaFX để tạo giao diện thân thiện, có menu, toolbar, và bảng dữ liệu.  

### Chuẩn đầu ra đạt được
- G4.2, G4.4, G5.2, G5.6, G5.8: Sử dụng cấu trúc dữ liệu, IO, Exception.  
- G6.1 – G6.3: Phân tích, thiết kế và cài đặt bài toán.  
- G7.1: Sử dụng IDE (IntelliJ IDEA) hiệu quả.  

---

## 3. Kiến trúc & Cấu trúc thư mục

```
23KTPM1-Java-PJ1/
├─ data/
│  └─ slang.txt
├─ storage/
│  └─ history.log
├─ lib/
│  └─ javafx-sdk-25.0.1/lib/*.jar
└─ src/
   ├─ app/
   │  └─ Main.java
   ├─ core/
   │  ├─ SlangDictionary.java
   │  ├─ HistoryService.java
   │  └─ QuizService.java
   ├─ io/
   │  └─ FileManager.java
   ├─ model/
   │  └─ Entry.java
   └─ ui/
      ├─ MainView.java
      └─ QuizView.java
```

---

## 4. Cách chạy chương trình

### Yêu cầu
- Java 25 (Temurin / OpenJDK 25)  
- JavaFX SDK 25.0.1 đã được tải và thêm vào project  

### Thêm thư viện JavaFX
1. Giải nén SDK vào thư mục `lib/javafx-sdk-25.0.1/`
2. Vào File → Project Structure → Modules → Dependencies
   - Add Library → JavaFX lib folder → Scope = Compile  

### Cấu hình VM options (bắt buộc)
```
--module-path "lib" --add-modules javafx.controls,javafx.fxml
```

### Run Configuration
- Main class: `app.Main`
- Working directory: thư mục gốc project (để load `data/slang.txt`)

---

## 5. Giao diện chính (JavaFX)

- MenuBar: File / View / Help  
- ToolBar: Tìm kiếm, Random, Quiz, Dark mode  
- SplitPane:  
  - Trái: Search Slang / Definition  
  - Giữa: Bảng hiển thị slang + nghĩa (TableView)  
  - Phải: Thêm, sửa, xoá, lưu dữ liệu  
- Status bar: Hiển thị tổng số slang, thời gian load, trạng thái thao tác  

---

## 6. Các chức năng chính

| STT | Chức năng | Mô tả | Đạt yêu cầu |
|:--:|:--|:--|:--:|
| 1 | Tìm kiếm theo slang | Nhập slang → tra nhanh | Cập nhật sau |
| 2 | Tìm theo definition | Nhập từ khóa trong nghĩa | Cập nhật sau |
| 3 | Lưu history | Ghi lại các slang đã tìm | Cập nhật sau |
| 4 | Thêm slang | Thêm slang mới, cảnh báo trùng | Cập nhật sau |
| 5 | Sửa slang | Sửa nghĩa slang | Cập nhật sau |
| 6 | Xóa slang | Xoá slang có confirm | Cập nhật sau |
| 7 | Reset data | Phục hồi từ file gốc | Cập nhật sau |
| 8 | Random slang | Hiển thị ngẫu nhiên 1 slang | Cập nhật sau |
| 9 | Quiz S→D | Đố vui slang → nghĩa | Cập nhật sau |
| 10 | Quiz D→S | Đố vui nghĩa → slang | Cập nhật sau |

---

## 7. Dữ liệu & Lưu trữ
- File chính: `data/slang.txt`  
- Lịch sử tìm kiếm: `storage/history.log`  
- Cấu trúc dữ liệu: `HashMap<String, List<String>>`  
- Tự động ghi lại khi Add / Edit / Delete  
- Có thể Save thủ công từ Menu hoặc Toolbar  

---

## 8. Git Commit & Quản lý mã nguồn

- Repository: [https://github.com/ngnam2012/23KTPM1-Java-PJ1](https://github.com/ngnam2012/23KTPM1-Java-PJ1)
- Tối thiểu 10 commits, phân bố nhiều ngày.  
- Mỗi commit có nội dung cụ thể: Add Search UI, Implement Quiz logic, Fix IO bug, v.v.

---

## 9. Video demo

- Video YouTube: (cập nhật sau)  
- Nội dung video gồm:  
  - Giới thiệu cấu trúc dữ liệu và mô hình  
  - Demo ít nhất 1 chức năng (ví dụ: Search hoặc Quiz)  
  - Nêu phần trăm code tự viết và phần tham khảo (nếu có)  

---

## 10. Tài liệu tham khảo
- Slide bài giảng môn Lập trình ứng dụng Java – FIT HCMUS  
- Tài liệu chính thức: [OpenJFX Documentation](https://openjfx.io/) 

---

## 11. Quy định điểm
- Mỗi chức năng: 10 điểm  
- Nếu chạy chậm (>1s) → trừ 50% điểm  
- Nếu giao diện không tiện dụng → trừ 30%  
- Nếu <10 commits → 0 điểm toàn bài  
- Nếu copy code → 0 điểm toàn bộ phần thực hành  

---

## 12. Tác giả & Liên hệ
Nguyễn Nhật Nam – 23127092  
Trường Đại học Khoa học Tự nhiên – ĐHQG TP.HCM  
Email: 23127092@student.hcmus.edu.vn
