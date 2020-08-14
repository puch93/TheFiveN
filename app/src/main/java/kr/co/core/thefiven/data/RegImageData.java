package kr.co.core.thefiven.data;

import lombok.Data;

@Data
public class RegImageData {
    private int position;
    private String image;
    private String image_auth; // 이미지
    private String image_change;

    public RegImageData(int position, String image, String image_auth, String image_change) {
        this.position = position;
        this.image = image;
        this.image_auth = image_auth;
        this.image_change = image_change;
    }
}
