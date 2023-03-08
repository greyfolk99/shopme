package com.greyfolk99.shopme.service;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    public byte[] getData(String path, String filename) throws IOException {
        return Files.readAllBytes(Paths.get(path + "/" + filename));
    }

    public String uploadFile(Path path, byte[] fileData) throws IOException {
        FileOutputStream fos = new FileOutputStream(path.toFile());
        fos.write(fileData);
        fos.close();
        return path.getFileName().toString();
    }

    public String generateUniqueFilename(String filename) {
        return UUID.randomUUID() + "_" + filename;
    }

    public void deleteFile(Path fullPath) throws FileNotFoundException {
        File file = new File(fullPath.toString());
        if (file.exists()) {
            file.delete();
        } else
            throw new FileNotFoundException("파일이 존재하지 않습니다.");
    }
}
