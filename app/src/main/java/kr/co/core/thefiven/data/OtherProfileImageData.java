package kr.co.core.thefiven.data;

import java.io.Serializable;

import lombok.Data;

@Data
public class OtherProfileImageData implements Serializable {
    private String profile_img;
    private String profile_img_ck;

    public OtherProfileImageData(String profile_img, String profile_img_ck) {
        this.profile_img = profile_img;
        this.profile_img_ck = profile_img_ck;
    }
}
