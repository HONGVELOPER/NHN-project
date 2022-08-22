package nhncommerce.project.exception

enum class ErrorMessage(
    val message: String,
    val href: String,
) {
    // 권한
    WRONG_ACCESS("잘못된 접근입니다.", "/products"),

    // 유저
    DUPLICATE_EMAIL("이미 가입한 이메일입니다.", "/users/joinForm"),
    INCORRECT_PASSWORD("비밀번호가 일치하지 않습니다.", "/users/joinForm"),
    INCORRECT_ORIGIN_PASSWORD("기존 비밀번호가 일치하지 않습니다.", "/api/users/updatePasswordForm"),
    CHANGE_TO_ORIGIN_PASSWORD("기존 비밀번호와 일치하는 비밀번호로 수정할 수 없습니다.", "/api/users/updatePasswordForm"),
    INCORRECT_NEW_PASSWORD("새로운 비밀번호가 일치하지 않습니다.", "/api/users/updatePasswordForm"),
    NOT_EXIST_USER_PHONE("연락처가 없습니다. 연락처를 등록해주세요.", "/api/users/updateProfileForm"),
    DELETE_USER("탈퇴한 회원입니다.", "/products"),

    //이미지 업로드
    IMAGE_UPLOAD_FAILED("이미지 업로드 실패 입니다.","/admin/products"),
    IMAGE_DELETE_FAILED("이미지 삭제 실패 입니다.","/admin/products"),

    //일 매출 스케줄링
    SCHEDULED_FAILED("스케줄링 작업을 실패 입니다.","/admin"),

    // 배송지
    NOT_EXIST_ADDRESS("배송지가 없습니다. 배송지를 등록해주세요.","/api/delivers/createForm"),

    //재고
    OUT_OF_STOCK("선택하신 재고수량보다 남은 수량이 부족합니다.","/products"),

    //주문목록
    MOVE_PATH("관리자 전용 주문목록 페이지로 이동합니다.","/admin/orders"),

    //옵션
    EMPTY_PRODUCTDTO("상품이 존재하지않습니다.", "/products"),

    // 리뷰

    CANCEL_ORDER("취소된 주문입니다.", "/products"),
    DUPLICATE_REVIEW("이미 리뷰를 작성한 주문입니다.", "/products"),

    //이벤트 쿠폰
    EMPTY_NOW_EVENT("현재 진행중인 이벤트가 없습니다.", "/products"),
    EMPTY_NOW_EVENT_ADMIN("진행중인 이벤트 없음, 이벤트를 설정해주세요", "/admin/coupons/event")


}