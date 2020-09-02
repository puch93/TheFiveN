package kr.co.core.thefiven.server.netUtil;

import java.lang.reflect.Member;

public class NetUrls {
    public static final String DOMAIN = "https://thefive.adamstore.co.kr";

    /* 가입 */
    //전화번호 인증요청
    public static final String AUTH_PHONE_REQUEST = DOMAIN + "/Member/sms_send";
    //전화번호 인증코드 검사
    public static final String AUTH_PHONE_CHECK = DOMAIN + "/Member/check_auth_join";
    //이메일 인증요청
    public static final String AUTH_EMAIL_REQUEST = DOMAIN + "/Member/email_send";
    //이메일 인증코드 검사
    public static final String AUTH_EMAIL_CHECK = DOMAIN + "/Member/check_email_auth";

    //회원가입
    public static final String JOIN = DOMAIN + "/Member/regist_member";
    //로그인
    public static final String LOGIN = DOMAIN + "/Member/login";

    //아이디 중복검사
    public static final String INSPECT_ID = DOMAIN + "/Member/duplication_id";
    //닉네임 중복검사
    public static final String INSPECT_NICK = DOMAIN + "/Member/duplication_nick";

    //비밀번호 찾기
    public static final String FIND_PW = DOMAIN + "/Member/find_pw";
    //비밀번호 변경
    public static final String FIND_PW_REG = DOMAIN + "/Member/findpw_regi";


    /* 메인 메뉴 */
    //메인 이성 추천회원 리스트 가져오기
    public static final String LIST_MAIN_RECOMMEND = DOMAIN + "/Member/recommend_memberlist";
    //메인 인기회원 리스트 가져오기
    public static final String LIST_MAIN_POPULAR = DOMAIN + "/Member/main_popular_memberlist";
    //다른회원 정보 가져오기
    public static final String INFO_OTHER = DOMAIN + "/Member/getProfile";
    public static final String CONFIRM_OTHER_CGPMS = DOMAIN + "/Member/setUserPoint";
    //심쿵 하기
    public static final String DO_LIKE = DOMAIN + "/Member/setSingleHeartAttack";
    //심쿵x2 하기
    public static final String DO_LIKE_DOUBLE = DOMAIN + "/Member/setDoubleHeartAttack";

    //CGPMS 타입별 회원수 가져오기
    public static final String CGPMS_MEMBER_COUNT = DOMAIN + "/Member/getCGPMSTypeCnt";
    //CGPMS 타입별 회원 데이터 가져오기
    public static final String CGPMS_MEMBER_LIST = DOMAIN + "/Member/getCGPMSMember_list";

    //나에게 심쿵을 보낸 회원 리스트 가져오기
    public static final String LIST_LIKED_MEMBER = DOMAIN + "/Member/getMyHeartAttacked_list";

    //내 프로필 열람회원 리스트
    public static final String LIST_ME_READED = DOMAIN + "/Member/readed_profile";


    /* 기타 */
    //내 프로필 보기
    public static final String INFO_ME = DOMAIN + "/Member/getMyProfile";

    //소개사진 등록
    public static final String EDIT_IMAGE = DOMAIN + "/Member/setProfilepimg";

    //관심사 등록
    public static final String EDIT_INTEREST = DOMAIN + "/Member/setMyInterest";

    //내 정보 수정
    public static final String EDIT_INFO = DOMAIN + "/Member/setProfileBasic";

    //공지사항 가져오기
    public static final String NOTICE = DOMAIN + "/Board/view_list";

    // 내가보낸 심쿵 리스트 가져오기
    public static final String LIST_LIKE_MEMBER = DOMAIN + "/Member/getMyHeartAttack_list";

    // 문의하기
    public static final String CONTACT = DOMAIN + "/Board/question_input";

    //신고하기
    public static final String REPORT = DOMAIN + "/Board/return_input";

    //차단하기 / 차단해제하기 스위치
    public static final String BLOCK = DOMAIN + "/Member/set_block_member";
    public static final String BLOCK_LIST = DOMAIN + "/Member/get_block_member_list";

    //탈퇴하기
    public static final String LEAVE = DOMAIN + "/Member/drop_member";

    //매칭중 회원리스트
    public static final String MATCHING = DOMAIN + "/Member/matching_list";

    //오프라인 모드 ON/OFF 여부 가져오기
    public static final String OFFLINE_STATE = DOMAIN + "/Member/loginYN";

    //오프라인 모드 ON/OFF 설정하기
    public static final String OFFLINE_SETTING = DOMAIN + "/Member/setloginYN";

    //아는사람 만나지 않기
    public static final String FRIEND_BLOCK = DOMAIN + "/Member/friend_block";

    //약관 가져오기
    public static final String TERMS = DOMAIN + "/Main/app_info";

    //나를 차단했거나, 탈퇴회원인지 체크
    public static final String CHECK_MEMBER_OUT = DOMAIN + "/Member/getBlockedInfo";

    // 상대 프로필보기 1P결제 했는지 여부 확인
    public static final String CHECK_1POINT_PAY = DOMAIN + "/Member/getUserPoint";



    /* 추가 */

    //유료회원인지 체크하는 함수
    public static final String CHECK_PAY_MEMBER = DOMAIN + "/Member/chkmatchingMember";

    //관리자페이지 CGPMS 설명 불러오는 함수
    public static final String CGPMS_EXPLANATION = DOMAIN + "/Member/getCGPMSDescription";

    //로그아웃
    public static final String LOGOUT = DOMAIN + "/Member/logout";

    //설명페이지 가져오기
    public static final String EXPLANATION_PAGE = DOMAIN + "/Main/company_info";





    /* 결제 */
    //결제결과 전달 (유료회원 / 포인트)
    public static final String PAY_RESULT = DOMAIN + "/Member/sell_item";
    //심쿵구매 (포인트로구매)
    public static final String PAY_LIKE = DOMAIN + "/Member/point_to_heart";


    /* 채팅 */
    //심쿵메시지 보내기
    public static final String LIKE_SEND_MESSAGE = DOMAIN + "/Chat/setHeartAttackMessage";
    //심쿵메시지 확인하기
    public static final String LIKE_CONFIRM_MESSAGE = DOMAIN + "/Chat/getHeartAttackMessage";
    //심쿵메시지 답변하기
    public static final String LIKE_REPLY_MESSAGE = DOMAIN + "/Chat/replyHeartAttackMessage";

    //채팅방 리스트 가져오기
    public static final String LIST_CHATTING = DOMAIN + "/Chat/listChats";
    //상대회원과 채팅방 생성되어있는지 체크
    public static final String CHECK_ROOM = DOMAIN + "/Chat/chkChatRoom";
    //메시지 보내기
    public static final String SEND_MESSAGE = DOMAIN + "/Chat/fcmMsg";

    //내가보낸 첫 번째 메시지인지 체크
    public static final String CHAT_FIRST_MSG = DOMAIN + "/Chat/chkmyfirstchat";
    //상대가 답변을 보냈는지 체크
    public static final String CHAT_REPLY_CHECK = DOMAIN + "/Chat/chkyourfirstchat";
    //답변 확인하기
    public static final String CHAT_CONFIRM_REPLY = DOMAIN + "/Chat/chkchatresponse";
    //채팅나가기
    public static final String CHAT_LEAVE = DOMAIN + "/Chat/leaveChat";

    //채팅방 1P 결제 여부 확인
    public static final String CHAT_PAY_CHECK = DOMAIN + "/Chat/chkchatresponsepoint";
    //이미지 보내기
    public static final String CHAT_SEND_IMAGE = DOMAIN + "/Chat/addmsgimg";

    // 안읽은 채팅 개수 가져오기 함수
    public static final String CHATTING_ALL_COUNT = DOMAIN + "/Chat/nonereadchatcnt";


    /* 기프티쇼 */
    //기프티쇼 리스트  --
    public static final String GIFT_LIST = DOMAIN + "/Member/getGiftshowlist";
    //실제로 기프티쇼 구매하는 함수 --
    public static final String GIFT_BUY_COUPON = DOMAIN + "/Member/setGiftishowPoint";
    //기프티쇼 쿠폰 취소
    public static final String GIFT_COUPON_CANCEL = DOMAIN + "/Member/setGiftishowCouponCancel";
    //기프티쇼 - 쿠폰요청 페이지 --
    public static final String GIFT_COUPON_REQUEST = DOMAIN + "/Member/getGiftishowRequest";
    //기프티쇼 - 현재 비즈머니 확인
    public static final String GIFT_COUPON_CONFIRM = DOMAIN + "/Member/getBizMoneyInfo";
    //기프티쇼 상품 상세 정보 --
    public static final String GIFT_ITEM_DETAIL = DOMAIN + "/Member/getGiftishowDetail";

    //기프티쇼 브랜드 정보 조회
    public static final String GIFT_BRAND_LIST = DOMAIN + "/Member/getGiftishowBrandInfo";
    //기프티쇼 브랜드 상세 정보 조회.
    public static final String GIFT_BRAND_DETAIL = DOMAIN + "/Member/getGiftishowBrandInfoDetail";
    //기프티쇼 선물받은 내역 --
    public static final String GIFT_STORAGE = DOMAIN + "/Member/getMyGiftishowInfo";
    //기프티쇼 쿠폰 상세 정보 --
    public static final String GIFT_COUPON_DETAIL = DOMAIN + "/Member/getGiftishowCouponDetail";


    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";
}
