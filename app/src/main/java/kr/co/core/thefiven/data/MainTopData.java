package kr.co.core.thefiven.data;

import lombok.Data;

@Data
public class MainTopData {
    private String idx;
    private String location;
    private String age;
    private String profile_img;
    private String profile_img_ck;
    private String gender;

    public MainTopData(String idx, String location, String age, String gender, String profile_img, String profile_img_ck) {
        this.idx = idx;
        this.location = location;
        this.gender = gender;
        this.age = age;
        this.profile_img = profile_img;
        this.profile_img_ck = profile_img_ck;
    }
}
