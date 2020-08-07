package kr.co.core.thefiven.data;

import lombok.Data;

@Data
public class ChattingBottomData {
    private String idx;
    private String nick;
    private String age;
    private String date;
    private String gender;

    private boolean login;
    private String profile_img;
    private String profile_img_ck;

    public ChattingBottomData(String idx, String nick, String age, String gender, String date, boolean login, String profile_img, String profile_img_ck) {
        this.idx = idx;
        this.nick = nick;
        this.age = age;
        this.gender = gender;
        this.date = date;
        this.login = login;
        this.profile_img = profile_img;
        this.profile_img_ck = profile_img_ck;
    }
}
