package com.pada.mongo.configuration;

import com.mongodb.MongoClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.pada.mongo.event.IdIncrementEvent;

import java.net.UnknownHostException;


@Configurable
@EnableMongoRepositories(basePackages = {"com.pada"})
public class MongoDBIdConfiguration {

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongo) throws UnknownHostException {
        UserCredentials userCredentials = new UserCredentials("pada", "dy.pada.com");
    	MongoTemplate mongoTemplate =  new MongoTemplate(new SimpleMongoDbFactory(mongo, "vc"/*,userCredentials*/));
    	MappingMongoConverter mConverter = (MappingMongoConverter) mongoTemplate.getConverter();
    	mConverter.setMapKeyDotReplacement("\\+");
    	return mongoTemplate;
    }

    @Bean
    public MongoClient mongo() throws UnknownHostException {
        return new MongoClient("192.168.163.44",27017);
    }

	@Bean
	public IdIncrementEvent getIdIncrementEvent(){
		return new IdIncrementEvent();
	}
	
	

}
