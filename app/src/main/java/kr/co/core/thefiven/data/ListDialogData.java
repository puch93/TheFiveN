package kr.co.core.thefiven.data;

import lombok.Data;

@Data
public class ListDialogData  {
    private String contents;
    private boolean select;

    public ListDialogData(String contents, boolean select) {
        this.contents = contents;
        this.select = select;
    }
}
