package kr.co.core.thefiven.data;

import lombok.Data;

@Data
public class ReadMeData {
    private String idx;
    private String profile_img;
    private String intro;
    private String gender;
    private boolean login;
    private boolean like;
    private boolean like_double;
    private boolean like_message;

    private String profile_img_ck;

    public ReadMeData(String idx, String profile_img, String intro, String gender, boolean login, boolean like, boolean like_double, boolean like_message, String profile_img_ck) {
        this.idx = idx;
        this.profile_img = profile_img;
        this.intro = intro;
        this.login = login;
        this.gender = gender;
        this.like = like;
        this.like_double = like_double;
        this.like_message = like_message;
        this.profile_img_ck = profile_img_ck;
    }
}
