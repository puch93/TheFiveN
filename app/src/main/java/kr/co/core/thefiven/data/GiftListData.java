package kr.co.core.thefiven.data;

import java.io.Serializable;

import lombok.Data;

@Data
public class GiftListData implements Serializable {
    //상품코드
    private String goodsCode;
    //브랜드명
    private String brandName;
    //상품이름
    private String goodsName;
    //상품가격
    private String goodsPrice;
    //유효기간
    private String validPrdDay;
    //상품설명
    private String content;
    //상품이미지
    private String goodsImgS;

    public GiftListData(String goodsCode, String brandName, String goodsName, String goodsPrice, String validPrdDay, String content, String goodsImgS) {
        this.goodsCode = goodsCode;
        this.brandName = brandName;
        this.goodsName = goodsName;
        this.goodsPrice = goodsPrice;
        this.validPrdDay = validPrdDay;
        this.content = content;
        this.goodsImgS = goodsImgS;
    }
}
