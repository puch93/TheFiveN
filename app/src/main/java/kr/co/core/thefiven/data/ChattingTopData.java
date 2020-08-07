package kr.co.core.thefiven.data;

import lombok.Data;

@Data
public class ChattingTopData {
    private String idx;
    private String nick;
    private String age;
    private String gender;
    private String location;
    private String contents;
    private String date;
    private boolean login;
    private String profile_img;
    private String profile_img_ck;

    private String room_idx;
    private String no_read_count;
    private String room_type;
    private String paychatYN;

    public ChattingTopData(String idx, String nick, String age, String gneder, String location, String contents, String date, boolean login, String profile_img, String room_idx, String profile_img_ck, String no_read_count, String room_type, String paychatYN) {
        this.idx = idx;
        this.nick = nick;
        this.age = age;
        this.gender = gender;
        this.location = location;
        this.contents = contents;
        this.date = date;
        this.login = login;
        this.profile_img = profile_img;
        this.room_idx = room_idx;
        this.profile_img_ck = profile_img_ck;
        this.no_read_count = no_read_count;
        this.room_type = room_type;
        this.paychatYN = paychatYN;
    }
}
