package kr.co.core.thefiven.data;

import lombok.Data;

@Data
public class MainBottomData {
    private String idx;
    private String nick;
    private String age;
    private String location;
    private String gender;

    private String profile_img;
    private String profile_img_count;

    private String cgpms_kind;
    private String cgpms_point;

    private boolean like_state;
    private boolean like_double_state;

    private String profile_img_ck;

    public MainBottomData(String idx, String nick, String age, String gender, String location, String profile_img, String profile_img_count, String cgpms_kind, String cgpms_point, boolean like_state, boolean like_double_state, String profile_img_ck) {
        this.idx = idx;
        this.nick = nick;
        this.age = age;
        this.gender = gender;
        this.location = location;
        this.profile_img = profile_img;
        this.profile_img_count = profile_img_count;
        this.cgpms_kind = cgpms_kind;
        this.cgpms_point = cgpms_point;
        this.like_state = like_state;
        this.like_double_state = like_double_state;
        this.profile_img_ck = profile_img_ck;
    }
}
