package ibf2022.batch2.csf.backend.models;

import java.util.Date;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class SmallerBundle {
    
    private String bundleId; 
    private Date date = new Date(); 
    private String title;

    public String getBundleId() {return bundleId;}
    public void setBundleId(String bundleId) {this.bundleId = bundleId;}
    public Date getDate() {return date;}
    public void setDate(Date date) {this.date = date;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    @Override
    public String toString() {
        return "SmallerBundle [ date=" + date + ", title=" + title + "]";
    }
    
    public JsonObject toJson() {
        return Json.createObjectBuilder()
            .add("bundleId", getBundleId())
            .add("date", getDate().toString())
            .add("title", getTitle())
            .build(); 
    }

    public static SmallerBundle toSmallerB(Document doc) {
        SmallerBundle sb = new SmallerBundle(); 
        sb.setBundleId(doc.getString("bundleId"));
        sb.setDate(doc.getDate("date"));
        sb.setTitle(doc.getString("title"));
        return sb;
    }
    

}
