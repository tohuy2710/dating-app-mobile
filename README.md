# dating-app-mobile

Ứng dụng Android này dùng Jetpack Compose, ViewModel, Retrofit và Room để xây dựng luồng đăng nhập đơn giản. Phần dưới đây mô tả nhanh cấu trúc thư mục trong `app/src` để bạn dễ tìm code và mở rộng sau này.

## Cấu trúc `src`

### `app/src/main/java/com/example/dating`
Đây là mã nguồn chính của ứng dụng.

- `MainActivity.kt`: điểm vào của UI, khởi tạo theme và gọi composable gốc.
- `DatingApplication.kt`: lớp `Application`, tạo container dependency cho toàn app.
- `di/AppContainer.kt`: lớp DI thủ công, cung cấp repository dùng chung.
- `core/network/RetrofitClient.kt`: cấu hình Retrofit, OkHttp và JSON converter.
- `core/utils/ValidationUtils.kt`: các hàm tiện ích kiểm tra dữ liệu đầu vào.
- `data/remote/AuthApiService.kt`: khai báo API đăng nhập.
- `data/repository/AuthRepository.kt`: abstraction và implementation cho luồng đăng nhập, lưu token, xoá token.
- `data/local/DatingDatabase.kt`: cấu hình Room database.
- `data/local/TokenDao.kt`: DAO thao tác với bảng token.
- `data/local/TokenEntity.kt`: entity lưu token đăng nhập.
- `data/model/`: các model request/response cho API.
- `ui/AppRoot.kt`: composable gốc của app.
- `ui/auth/AuthViewModel.kt`: quản lý state và logic đăng nhập.
- `ui/auth/LoginScreen.kt`: màn hình đăng nhập.
- `ui/common/CommonComposables.kt`: các composable dùng chung.
- `ui/theme/`: theme, màu sắc, kiểu chữ và shape của app.

### `app/src/main/res`
Chứa toàn bộ tài nguyên giao diện và cấu hình Android.

- `drawable/`: icon, ảnh loading, ảnh lỗi và các vector drawable.
- `mipmap*/`: icon launcher theo nhiều mật độ màn hình.
- `values/`: strings, theme và các giá trị cấu hình giao diện.
- `xml/`: cấu hình mạng, ví dụ `network_security_config.xml`.

### `app/src/main/AndroidManifest.xml`
Khai báo `DatingApplication`, `MainActivity`, quyền `INTERNET` và các cấu hình ứng dụng cơ bản.

## Luồng hoạt động chính

1. `MainActivity` mở app và gọi `AppRoot`.
2. `AppRoot` tạo `AuthViewModel` thông qua factory.
3. `LoginScreen` nhận state từ ViewModel và hiển thị form đăng nhập.
4. `AuthViewModel` gọi `AuthRepository` để gửi request qua Retrofit.
5. Khi đăng nhập thành công, token được lưu vào Room database thông qua `TokenDao`.

## Ghi chú nhanh

- App đang dùng DI thủ công, chưa dùng Hilt/Koin.
- Backend được cấu hình trong `RetrofitClient.BASE_URL`; nếu đổi server thì cập nhật tại đây.
- Tên package hiện tại là `com.example.dating`, nhưng một số class/theme vẫn còn tên cũ từ mẫu Mars Photos.
