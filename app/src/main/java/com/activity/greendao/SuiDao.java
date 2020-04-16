package com.activity.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2018/7/18.
 */
@Entity
public class SuiDao {
    @Id(autoincrement = true)
    private Long id;
    public String ip;
    public String port;
    public String type;
    public String link;
    public String name;//
    public String user;//隧道描述，方便用户分辨隧道
    public String time;
    @Generated(hash = 2141312454)
    public SuiDao(Long id, String ip, String port, String type, String link,
            String name, String user, String time) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.type = type;
        this.link = link;
        this.name = name;
        this.user = user;
        this.time = time;
    }
    @Generated(hash = 1920169455)
    public SuiDao() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getIp() {
        return this.ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getPort() {
        return this.port;
    }
    public void setPort(String port) {
        this.port = port;
    }
    public String getType() {
        return this.type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getLink() {
        return this.link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUser() {
        return this.user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }



}
