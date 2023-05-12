package ibf2022.batch2.csf.backend.repositories;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Repository
public class ImageRepository {

	@Autowired
	private AmazonS3 S3Client;

	@Value("${do.storage.bucketname}")
    private String bucketName; 

    @Value("${do.storage.endpoint}")
    private String endPoint;

	//TODO: Task 3
	// You are free to change the parameter and the return type
	// Do not change the method's name
	public Optional<List<String>> upload(MultipartFile file, String name, String title, String comments) {

		List<String> urls = new LinkedList<>(); 
		try {
			InputStream is = file.getInputStream();
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
	
			ZipEntry zipEntry;
			while ((zipEntry = zis.getNextEntry()) != null) {
				if (!zipEntry.isDirectory() && isImageFile(zipEntry.getName())) {
					// byte[] buffer = new byte[1024];
					byte[] buffer = new byte[(int) zipEntry.getSize()];
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int len;
					while ((len = zis.read(buffer)) > 0) {
						baos.write(buffer, 0, len);
					}
					byte[] data = baos.toByteArray();
					InputStream imageIS = new ByteArrayInputStream(data);
						
					// save image into S3 
					try {
						String fileExt = getFileExtension(zipEntry.getName()); // could be null
						System.out.printf(">>> fileExt: %s\n", fileExt); 
						Map<String, String> userData = new HashMap<>();
						userData.put("name", name);
						userData.put("title", title);
						userData.put("comments", comments); 
	
						ObjectMetadata metadata = new ObjectMetadata(); 
						metadata.setContentType("image/" + fileExt);
						metadata.setContentLength(data.length);
						metadata.setUserMetadata(userData);
	
						String key = zipEntry.getName();
						PutObjectRequest putReq = new PutObjectRequest(bucketName, key, imageIS, metadata); // set title as key
						putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead); 
						S3Client.putObject(putReq); 
						System.out.printf(">>> Put Request: %s\n", putReq.toString());

						// get urls 
						String partURL = "https://" + bucketName + "." + endPoint;
						urls.add(partURL + "/" + key); 

					} catch (AmazonS3Exception ex) {
						ex.printStackTrace();
						return Optional.empty(); 
					}
				}
			}
			// close resources 
			zis.closeEntry();
			zis.close();
		} catch (IOException ex2) {
			ex2.printStackTrace(); 
			return Optional.empty(); 
		}
		return Optional.of(urls); 
	}

	private String getFileExtension(String fileName) {
		int idx = fileName.lastIndexOf('.');
		if (idx == -1) {
			return null;
		} else {
			return fileName.substring(idx + 1);
		}
	}

	private boolean isImageFile(String fileName) {
		String[] validExtensions = {".gif", ".jpg", ".jpeg", ".png"};
		for (String extension : validExtensions) {
			if (fileName.toLowerCase().endsWith(extension)) {
				return true;
			}
		}
		return false;
	}
}
