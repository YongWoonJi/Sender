package com.sender.team.sender.data;

public class ContractsData {
    private String here_lat;
    private String here_lon;
    private String name;
    private String addr_lon;
    private int price;
    private String addr_lat;
    private String memo;
    private String arr_time;
    private ContractsDataPic[] pic;
    private String sending_id;
    private String contract_id;
    private String info;
    private String rec_phone;

    public String getSending_id() {
        return sending_id;
    }

    public void setSending_id(String sending_id) {
        this.sending_id = sending_id;
    }

    public String getDelivering_id() {
        return delivering_id;
    }

    public void setDelivering_id(String delivering_id) {
        this.delivering_id = delivering_id;
    }

    private String delivering_id;

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

    public String getHere_lat() {
        return here_lat;
    }

    public void setHere_lat(String here_lat) {
        this.here_lat = here_lat;
    }

    public String getHere_lon() {
        return here_lon;
    }

    public void setHere_lon(String here_lon) {
        this.here_lon = here_lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContractsDataPic[] getPic() {
        return this.pic;
    }

    public void setPic(ContractsDataPic[] pic) {
        this.pic = pic;
    }

    public String getSender_id() {
        return this.sending_id;
    }

    public void setSender_id(String sending_id) {
        this.sending_id = sending_id;
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

    public String getContract_id() {
        return contract_id;
    }

    public void setContract_id(String contract_id) {
        this.contract_id = contract_id;
    }
}
