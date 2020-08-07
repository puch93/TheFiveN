package kr.co.core.thefiven.data;

import java.io.Serializable;

import lombok.Data;

@Data
public class InterestSubData implements Serializable {
    private String title;
    private int image;
    private boolean selected;

    public InterestSubData(String title, int image, boolean selected) {
        this.title = title;
        this.image = image;
        this.selected = selected;
    }
}
