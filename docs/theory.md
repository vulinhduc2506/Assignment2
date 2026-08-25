Java Core, OOP và Spring Boot 

JC-01:
- Class: là một bản thiết kế để tạo ra đối tượng
- Object: là đối tượng được tạo ra từ bàn thiết kế (Class)
- Constructor: là hàm khởi tạo trong class, đươc gọi khi tạo một đối tượng (Object) bằng từ khóa new
- Encapsulation: tính đóng gói là việc che giấu dữ liệu trong class bằng từ khóa private, chỉ 
có thể truy cập bằng các phương thức getter/setter

JC-03:
- ArrayList: Lưu các phần tử theo thứ tự thêm vào, cho phép các phần tử trùng lặp, cho phép truy cập các phần tử qua index
- HashMap: Lưu theo cặp key-value, khóa key không trùng lặp

JC-04:
- Khi so sánh String, "==" chỉ để so sánh địa chỉ bộ nhớ của biến, trong khi equals() được dùng để so sánh giá trị nội dung thực tế của chuỗi String.

JC-05:
- Abstract Class:
 + Cung cấp định nghĩa chung làm base class để chia sẻ code giữa các class họ hàng
 + không thể tạo trực tiếp, có chứa hàm tạo
 + dùng từ khóa extends, chỉ được kế thừa một lớp cha duy nhất
- Interface:
 + Là một "bản hợp đồng" tách biệt "Cái gì" (what) khỏi "Làm như thế nào" (how). Dùng cho các lớp không hề liên quan đến nhau.
 + không thể tạo trực tiếp, không chứa hàm tạo
 + Dùng từ khóa implements. Có thể triển khai vô số Interface

JC-06:
- OverLoading: định ngĩa 2 hoặc nhiều hàm cùng tên nhưng khác tham số truyền vào
  VD: find(Long id) và find(String code)
- Overriding: định nghĩa 1 hàm đã có sẵn ở lớp cha cùng tên, cùng tham số
  VD: @Override hàm toString()

JC-07:
- throw được dùng trực tiếp bên trong thân hàm để chủ động ném ra một ngoại lệ cụ thể (VD: throw new ConflictException("Hết sách")).
- throws được gắn ở chữ ký của hàm để khai báo với trình biên dịch rằng hàm này có nguy cơ ném ra những loại ngoại lệ nào.

JC-08:
- Nếu Service trả về null, thì sẽ dễ bị xảy ra lỗi NullPointerException do controller liên tục phải kiểm tra null, giải pháp là ném
  ra custom exception sau đó dùng @RestControllerAdvice bắt lỗi và trả về mã lỗi.

JC-09:
- HTTP request từ Client đi vào Controller. Controller nhận, validate dữ liệu và gọi xuống Service. 
Service chứa các logic tính toán nghiệp vụ (kiểm tra điều kiện sách, tạo phiếu) và trả đối tượng lại cho Controller. Cuối cùng, Controller bọc dữ liệu vào DTO và trả về response dưới dạng JSON.

JC-10:
- @Valid dùng để kích hoạt các quy tắc kiểm tra (như @NotBlank, @NotNull) trên DTO request.
- @RequestBody dùng để chuyển đổi JSON body từ request thành đối tượng Java.
- @PathVariable trích xuất giá trị trực tiếp trên đường dẫn URL (VD: /books/{code}).
- @RequestParam trích xuất giá trị sau dấu ? trong URL (VD: ?keyword=).

JC-11:
- 201 Created là chuẩn RESTful để báo hiệu tài nguyên (ở đây là phiếu mượn mới hoặc sách mới) đã được tạo thành công.
- Khi mượn sách đã hết, request có định dạng hợp lệ nhưng không thể thực hiện do xung đột với trạng thái nghiệp vụ, nên dùng 409 Conflict là phản ánh chính xác nhất.