package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FilesystemReposite extends MongoRepository<FileSystem,String> {
}
