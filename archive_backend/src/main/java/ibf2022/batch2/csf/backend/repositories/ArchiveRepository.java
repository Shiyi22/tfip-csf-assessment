package ibf2022.batch2.csf.backend.repositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import ibf2022.batch2.csf.backend.models.Bundle;
import ibf2022.batch2.csf.backend.models.SmallerBundle;

@Repository
public class ArchiveRepository {

	@Autowired
	private MongoTemplate template; 

	private final String C_ARCHIVES = "archives";  

	//TODO: Task 4
	// You are free to change the parameter and the return type
	// Do not change the method's name
	// Write the native mongo query that you will be using in this method
	// db.archives.insert({ bundleId: 'some Id', date: new Date(), title: "some title",name: "some name", comments: 'some comments', urls: ["url1", "url2", "url3"]});
	//
	public Bundle recordBundle(Bundle insertObj) {

		// JsonObject jo = Json.createObjectBuilder()
		// 					.add("bundleId", insertObj.getBundleId())
		// 					.add("date", insertObj.getDate().toString())
		// 					.add("title", insertObj.getTitle())
		// 					.add("name", insertObj.getName())
		// 					.add("comments", insertObj.getComments())
		// 					.add("urls", insertObj.getUrls().toString())
		// 					.build();
		// Document doc = Document.parse(jo.toString()); 
		return template.insert(insertObj, C_ARCHIVES); 
	}

	//TODO: Task 5
	// You are free to change the parameter and the return type
	// Do not change the method's name
	// Write the native mongo query that you will be using in this method
	// db.archives.find({bundleId: "73928bb6"})
	//
	public Bundle getBundleByBundleId(String bundleId) {

		Query query = Query.query(Criteria.where("bundleId").is(bundleId));
		Bundle bundle = template.findOne(query, Bundle.class, C_ARCHIVES); 
		System.out.printf(">>> Bundle retrieved: %s\n", bundle.toString()); 
		return bundle;
	}

	//TODO: Task 6
	// You are free to change the parameter and the return type
	// Do not change the method's name
	// Write the native mongo query that you will be using in this method
	/* db.archives.aggregate([
  		{
    		$project: { title: 1, date: 1}
  		},
  		{
    		$sort: {date: -1, title: 1}
  		}
	 */
	//
	public Optional<List<SmallerBundle>> getBundles(/* any number of parameters here */) {
		ProjectionOperation projOps = Aggregation.project("title", "date", "bundleId");
		SortOperation sortOps = Aggregation.sort(Sort.Direction.DESC, "date").and(Sort.Direction.ASC, "title");
		Aggregation agg = Aggregation.newAggregation(projOps, sortOps);
		// List<Bundle> results = template.aggregate(agg, C_ARCHIVES, Bundle.class).getMappedResults(); // this creates new bundleId

		List<SmallerBundle> results = template.aggregate(agg, C_ARCHIVES, SmallerBundle.class).getMappedResults(); 
		System.out.printf(">>> Smaller bundle list: %s\n", results);

		// convert Document to SmallerBundle
		// List<SmallerBundle> sb = new LinkedList<>(); 
		// for (Document d : results)
		// 	sb.add(SmallerBundle.toSmallerB(d));

		if (results.size() == 0)
			return Optional.empty();
		return Optional.of(results);
	}


}
