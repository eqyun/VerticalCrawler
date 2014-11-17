package com.pada.mongo.respository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pada.mongo.domain.Counter;

public interface DBIdRepository extends  MongoRepository<Counter, String>{
	
	public Counter findByClazz(String clazz);
}
