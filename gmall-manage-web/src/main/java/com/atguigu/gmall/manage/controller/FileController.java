package com.atguigu.gmall.manage.controller;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
public class FileController {

    @Value("${fileServer.url}")
    String fileUrl;
    // http://localhost:8082/fileUpload
    @PostMapping("fileUpload")
    public String fileUpload(MultipartFile file) throws IOException, MyException {
        String imgUrl = "";
        if(file != null){
            String configFile  = this.getClass().getClassLoader().getResource("tracker.conf").getFile();
            configFile = java.net.URLDecoder.decode(configFile,"utf-8");
            ClientGlobal.init(configFile);

            TrackerClient trackerClient = new TrackerClient();
            TrackerServer connection = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(connection,null);

            String originalFilename = file.getOriginalFilename();
            String extName  = StringUtils.substringAfterLast(originalFilename, ".");

            String[] uploadFile = storageClient.upload_appender_file(file.getBytes(), extName, null);

            imgUrl=fileUrl;
            for (int i = 0; i <uploadFile.length ; i++) {
                String s = uploadFile[i];
                imgUrl += "/"+s;
            }
            System.out.println("imgUrl = "+imgUrl);
        }
        return imgUrl;
    }
}
