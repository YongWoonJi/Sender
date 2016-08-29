package com.sender.team.sender.data;

public class ContractsData {
    private String addr_lon;
    private int price;
    private String addr_lat;
    private String memo;
    private String arr_time;
    private ContractsDataPic[] pic;
    private String sender_id;
    private String info;
    private String rec_phone;

    public String getAddr_lon() {
        return this.addr_lon;
    }

    public void setAddr_lon(String addr_lon) {
        this.addr_lon = addr_lon;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAddr_lat() {
        return this.addr_lat;
    }

    public void setAddr_lat(String addr_lat) {
        this.addr_lat = addr_lat;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getArr_time() {
        return this.arr_time;
    }

    public void setArr_time(String arr_time) {
        this.arr_time = arr_time;
    }

    public ContractsDataPic[] getPic() {
        return this.pic;
    }

    public void setPic(ContractsDataPic[] pic) {
        this.pic = pic;
    }

    public String getSender_id() {
        return this.sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRec_phone() {
        return this.rec_phone;
    }

    public void setRec_phone(String rec_phone) {
        this.rec_phone = rec_phone;
    }
}
