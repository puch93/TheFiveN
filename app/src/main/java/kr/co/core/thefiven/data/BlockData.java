package kr.co.core.thefiven.data;

import lombok.Data;

@Data
public class BlockData {
    private String idx;
    private String nick;
    private String age;
    private String gender;
    private String location;
    private String profile_img;
    private String profile_img_ck;

    public BlockData(String idx, String nick, String age, String gender, String location, String profile_img, String profile_img_ck) {
        this.idx = idx;
        this.nick = nick;
        this.age = age;
        this.location = location;
        this.profile_img = profile_img;
        this.profile_img_ck = profile_img_ck;
    }
}
