package kr.co.core.thefiven.data;

import java.io.Serializable;

import lombok.Data;

@Data
public class GiftStorageData implements Serializable {
    private String p_name;
    private String p_limit_date;
    private String p_title_image;
    private String p_barcode_image;

    private String tr_id;

    public GiftStorageData(String p_name, String p_limit_date, String p_title_image, String p_barcode_image, String tr_id) {
        this.p_name = p_name;
        this.p_limit_date = p_limit_date;
        this.p_title_image = p_title_image;
        this.p_barcode_image = p_barcode_image;
        this.tr_id = tr_id;
    }
}
