package kr.co.core.thefiven.data;

import java.util.ArrayList;
import lombok.Data;

@Data
public class UserData {
    private String joinType;    // 가입종류 (normal/facebook)

    private String phone;       // 전화번호
    private String id;          // 아이디 (이메일)
    private String pw;          // 비밀번호
    private String nick;        // 닉네임
    private String gender;      // 성별 (male/female)

    private String birth;           // 생년월일
    private String birth_type;      // 생년월일 음력/양력 (lunar/solar)
    private String birth_twin;      // 생년월일 쌍둥이체크 (Y/N)

    private String job;         // 직업
    private String salary;      // 연봉
    private String marriage;    // 결혼여부
    private String personality; // 성격
    private String nationality; // 국적
    private String blood;       // 혈액형
    private String location;    // 지역
    private String edu;         // 학력
    private String holiday;     // 휴일
    private String family;      // 형제자매
    private String height;      // 키
    private String body;        // 체형
    private String drink;       // 음주
    private String smoke;       // 흡연
    private String intro;       // 자기소개
    private String interests;   // 관심사 다수가능
    private ArrayList<String> images;    // 소개사진 다수가능

    public UserData() {}
    public UserData(String phone, String id, String pw, String nick, String gender, String birth, String birth_type, String birth_twin, String job, String salary, String marriage, String personality, String nationality, String blood, String location, String edu, String holiday, String family, String height, String body, String drink, String smoke, String intro, String interests, ArrayList<String> images) {
        this.phone = phone;
        this.id = id;
        this.pw = pw;
        this.nick = nick;
        this.gender = gender;
        this.birth = birth;
        this.birth_type = birth_type;
        this.birth_twin = birth_twin;
        this.job = job;
        this.salary = salary;
        this.marriage = marriage;
        this.personality = personality;
        this.nationality = nationality;
        this.blood = blood;
        this.location = location;
        this.edu = edu;
        this.holiday = holiday;
        this.family = family;
        this.height = height;
        this.body = body;
        this.drink = drink;
        this.smoke = smoke;
        this.intro = intro;
        this.interests = interests;
        this.images = images;
    }
}
