package com.glsx.plat.dfs.fastdfs;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.Base64;

@Slf4j
@Component
public class FastDfsUtils {

    @Value("${fdfs.tracker.nginx.domain:http://localhost}")
    private String domain;

    @Value("${fdfs.tracker.nginx.port:80}")
    private Integer port;

    @Resource
    private FastFileStorageClient storageClient;

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String upload(MultipartFile file) throws Exception {
        String fileExtName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), fileExtName, null);
        return domain + (port == 80 ? "" : ":" + port) + "/" + storePath.getFullPath();
    }

    /**
     * 上传图片文件(含缩略图)
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String uploadImage(MultipartFile file) throws Exception {
        String fileExtName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        StorePath storePath = this.storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), fileExtName, null);
        return domain + (port == 80 ? "" : ":" + port) + "/" + storePath.getFullPath();
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String upload(File file) throws Exception {
        String fileExtName = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        InputStream fis = new FileInputStream(file);
        StorePath storePath = this.storageClient.uploadFile(fis, fis.available(), fileExtName, null);
        return domain + (port == 80 ? "" : ":" + port) + "/" + storePath.getFullPath();
    }

    /**
     * 文件上传
     * 最后返回fastDFS中的文件名称;group1/M00/01/04/CgMKrVvS0geAQ0pzAACAAJxmBeM793.doc
     *
     * @param bytes     文件字节
     * @param fileSize  文件大小
     * @param extension 文件扩展名
     * @return fastDfs路径
     */
    public String upload(byte[] bytes, long fileSize, String extension) {
        InputStream bais = new ByteArrayInputStream(bytes);
        StorePath storePath = storageClient.uploadFile(bais, fileSize, extension, null);
        return domain + (port == 80 ? "" : ":" + port) + "/" + storePath.getFullPath();
    }

    /**
     * 文件上传
     * 最后返回fastDFS中的文件名称;group1/M00/01/04/CgMKrVvS0geAQ0pzAACAAJxmBeM793.doc
     *
     * @param bytes     文件字节
     * @param extension 文件扩展名
     * @return fastDfs路径
     */
    public String upload(byte[] bytes, String extension) {
        InputStream bais = new ByteArrayInputStream(bytes);
        StorePath storePath = null;
        try {
            storePath = storageClient.uploadFile(bais, bais.available(), extension, null);
            return domain + (port == 80 ? "" : ":" + port) + "/" + storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 上传文件并生成缩略图
     *
     * @param base64
     * @return
     */
    public String uploadByBase64(String base64) {
        String[] base64ImageSplit = base64.split(",");
        byte[] bytes = Base64.getDecoder().decode(base64ImageSplit[1]);//解码
        String fileExtName = base64ImageSplit[0].substring(base64ImageSplit[0].indexOf("/") + 1, base64ImageSplit[0].indexOf(";"));
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {// 调整异常数据
                bytes[i] += 256;
            }
        }
        return upload(bytes, bytes.length, fileExtName);
    }

    /**
     * 下载文件
     *
     * @param fileUrl 文件url
     * @return
     */
    public byte[] download(String fileUrl) {
        String group = fileUrl.substring(0, fileUrl.indexOf("/"));
        String path = fileUrl.substring(fileUrl.indexOf("/") + 1);
        return storageClient.downloadFile(group, path, new DownloadByteArray());
    }

    /**
     * 删除文件
     */
    public void delete(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            log.info("fileUrl == >>文件路径为空...");
            return;
        }
        try {
            StorePath storePath = StorePath.parseFromUrl(fileUrl);
            storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

}
