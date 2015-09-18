package com.yorshahar.locker.model.notification;

import android.graphics.drawable.Drawable;

import com.yorshahar.locker.model.DomainObject;

import java.util.Date;

/**
 * Created by yorshahar on 8/12/15.
 */
public class Notification extends DomainObject {
    private String source;
    private Date dateArrived;
    private String title;
    private String body;
    private Drawable icon;

    public Notification() {
        super();
    }

    public Notification(String source, Date dateArrived, String title, String body, Drawable icon) {
        this();

        this.source = source;
        this.dateArrived = dateArrived;
        this.title = title;
        this.body = body;
        this.icon = icon;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getDateArrived() {
        return dateArrived;
    }

    public void setDateArrived(Date dateArrived) {
        this.dateArrived = dateArrived;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;

        if (source != null ? !source.equals(that.source) : that.source != null) return false;
        return !(body != null ? !body.equals(that.body) : that.body != null);
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }
}
