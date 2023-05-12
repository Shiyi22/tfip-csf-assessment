package ibf2022.batch2.csf.backend.models;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

public class Bundle {

    private String bundleId = generateId(); 
    private Date date = new Date(); 
    private String title;
    private String name; 
    private String comments; 
    private List<String> urls = new LinkedList<>(); 

    public String getBundleId() {return bundleId;}
    public void setBundleId(String bundleId) {this.bundleId = bundleId;}
    public Date getDate() {return date;}
    public void setDate(Date date) {this.date = date;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getComments() {return comments;}
    public void setComments(String comments) {this.comments = comments;}
    public List<String> getUrls() {return urls;}
    public void setUrls(List<String> urls) {this.urls = urls;}

    private String generateId() {
        return UUID.randomUUID().toString().substring(0, 8); 
    }

    public static Bundle toObj(String name, String title, String comments, List<String> urls) {
        Bundle insert = new Bundle(); 
        insert.setName(name);
        insert.setTitle(title);
        insert.setComments(comments);
        insert.setUrls(urls);
        return insert; 
    }

    public JsonObject toJson() {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder(); 
        for (String url : urls) 
            arrBuilder.add(url); 
        return Json.createObjectBuilder()
            .add("bundleId", getBundleId())
            .add("date", getDate().toString())
            .add("title", getTitle())
            .add("name", getName() != null ? getName() : "")
            .add("comments", getComments() != null ? getComments() : "")
            .add("urls", arrBuilder.build())
            .build(); 
    }

    @Override
    public String toString() {
        return "Insert [bundleId=" + bundleId + ", date=" + date + ", title=" + title + ", name=" + name + ", comments="
                + comments + ", urls=" + urls + "]";
    }

    
    
}
