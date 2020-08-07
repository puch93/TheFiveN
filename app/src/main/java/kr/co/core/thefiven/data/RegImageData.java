package kr.co.core.thefiven.data;

import lombok.Data;

@Data
public class RegImageData {
    private String image;
    private String image_ck;
    private boolean fromServer;

    public RegImageData(String image, boolean fromServer, String image_ck) {
        this.image = image;
        this.image_ck = image_ck;
        this.fromServer = fromServer;
    }
}
