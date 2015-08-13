package com.yorshahar.locker.model.notification;

import java.util.Date;

/**
 * Created by yorshahar on 8/12/15.
 */
public class Notification {
    private String source;
    private Date dateArrived;
    private String title;
    private String body;

    public Notification(String source, Date dateArrived, String title, String body) {
        this.source = source;
        this.dateArrived = dateArrived;
        this.title = title;
        this.body = body;
    }

    public String getSource() {
        return source;
    }

    public Date getDateArrived() {
        return dateArrived;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

}
