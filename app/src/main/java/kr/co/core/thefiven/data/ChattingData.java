package kr.co.core.thefiven.data;

import lombok.Data;

@Data
public class ChattingData {
    private boolean pay;
    private String user_idx;

    private String data_type;
    private String send_time;
    private String date_line;
    private String contents;

    private boolean read;

    public ChattingData(boolean pay, String user_idx, String send_time, String date_line, String data_type, String contents,boolean read) {
        this.pay = pay;
        this.user_idx = user_idx;
        this.send_time = send_time;
        this.data_type = data_type;
        this.date_line = date_line;
        this.contents = contents;
        this.read = read;
    }

    public ChattingData(String date_line, String data_type) {
        this.date_line = date_line;
        this.data_type = data_type;
    }
}
