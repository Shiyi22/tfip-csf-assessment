package ibf2022.batch2.csf.backend.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ibf2022.batch2.csf.backend.models.Bundle;
import ibf2022.batch2.csf.backend.models.SmallerBundle;
import ibf2022.batch2.csf.backend.repositories.ArchiveRepository;
import ibf2022.batch2.csf.backend.repositories.ImageRepository;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

@Controller
@RequestMapping()
public class UploadController {

	@Autowired
	private ImageRepository imgRepo;

	@Autowired
	private ArchiveRepository arcRepo; 

	// TODO: Task 2, Task 3, Task 4
	@PostMapping(path="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> upload(@RequestPart MultipartFile file, @RequestPart String name, @RequestPart String title, @RequestPart String comments) throws IOException {
		
		// expand zip file and upload each image into S3 , returns List<String>
		Optional<List<String>> opt = imgRepo.upload(file, name, title, comments); 

		// first possible failure
		if (opt.isEmpty()) {
			JsonObject jo = Json.createObjectBuilder().add("error", "Unable to upload image to S3").build(); 
			return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(jo.toString());
		}

		List<String> urls = opt.get(); 
		System.out.printf(">>> urls: %s\n", urls.toString()); 

		// create insert Object  
		Bundle insertObj = Bundle.toObj(name, title, comments, urls); 
		// Insert doc into mongo 	
		Bundle insertedObj = arcRepo.recordBundle(insertObj); 
		System.out.printf(">>> inserted obj: %s\n", insertObj.toString()); 

		// second possible failure 
		if (insertedObj == null) {
			JsonObject jo = Json.createObjectBuilder().add("error", "Unable to insert document into Mongo database").build(); 
			return ResponseEntity.status(500).contentType(MediaType.APPLICATION_JSON).body(jo.toString());
		}

		JsonObject jo = Json.createObjectBuilder().add("bundleId", insertedObj.getBundleId()).build(); 
		return ResponseEntity.status(201).contentType(MediaType.APPLICATION_JSON).body(jo.toString()); 
	}

	// TODO: Task 5
	@GetMapping(path="/bundle/{bundleId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<String> getBundle(@PathVariable String bundleId) {

		Bundle bundle = arcRepo.getBundleByBundleId(bundleId); 
		if (bundle == null) {
			JsonObject jo = Json.createObjectBuilder().add("error", "Cannot find Bundle using id: %s".formatted(bundleId)).build();
			return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(jo.toString());  
		}
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(bundle.toJson().toString());
	}
	

	// TODO: Task 6
	@GetMapping(path="/bundles")
	public ResponseEntity<String> getBundles() {
		Optional<List<SmallerBundle>> opt = arcRepo.getBundles(); 
		if (opt.isEmpty()) {
			JsonObject jo = Json.createObjectBuilder().add("error", "Cannot find any bundles in Mongo!").build(); 
			return ResponseEntity.badRequest().body(jo.toString()); 
		}
		JsonArrayBuilder arrBuilder = Json.createArrayBuilder(); 
		for (SmallerBundle sb : opt.get())
			arrBuilder.add(sb.toJson());
		return ResponseEntity.ok().body(arrBuilder.build().toString()); 
	}

}
