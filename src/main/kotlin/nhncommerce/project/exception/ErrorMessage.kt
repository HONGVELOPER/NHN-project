package nhncommerce.project.exception

enum class ErrorMessage(
    val message: String,
    val href: String,
) {
    // 권한
    WRONG_ACCESS("잘못된 접근입니다.", "/user"),

    // 유저
    DUPLICATE_EMAIL("중복된 이메일입니다.", "/users/joinForm"),
    INCORRECT_PASSWORD("비밀번호가 일치하지 않습니다.", "/users/joinForm"),
    INCORRECT_ORIGIN_PASSWORD("기존 비밀번호가 일치하지 않습니다.", "/api/users/updatePasswordForm"),
    CHANGE_TO_ORIGIN_PASSWORD("기존 비밀번호와 일치하는 비밀번호로 수정할 수 없습니다.", "/api/users/updatePasswordForm"),
    INCORRECT_NEW_PASSWORD("새로운 비밀번호가 일치하지 않습니다.", "/api/users/updatePasswordForm"),
    NOT_EXIST_USER_PHONE("연락처가 없습니다. 연락처를 등록해주세요.", "/api/users/updateProfileForm"),

    //이미지 업로드
    IMAGE_UPLOAD_FAILED("이미지 업로드 실패 입니다.","/admin/products"),
    IMAGE_DELETE_FAILED("이미지 삭제 실패 입니다.","/admin/products"),

    // 배송지
    NOT_EXIST_ADDRESS("배송지가 없습니다. 배송지를 등록해주세요.","/api/delivers/createForm"),

    //재고
    OUT_OF_STOCK("선택하신 재고수량보다 남은 수량이 부족합니다.","/user"),

}