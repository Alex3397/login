package com.alex.login.images;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping(value = "/images")
@Setter
@Getter
public class FileController {
    private String relativePath;
    private String FILE_PATH_ROOT;

    @GetMapping("/{path}/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable("path") String path,@PathVariable("filename") String filename) {
        if (path.equals("confirm")) {
            setRelativePath("/front-end/src/emails/Confirm Email/images/");
            setFILE_PATH_ROOT(System.getProperty("user.dir") + relativePath);
        } else if (path.equals("revalidate")) {
            setRelativePath("/front-end/src/emails/Revalidate Token/images/");
            setFILE_PATH_ROOT(System.getProperty("user.dir") + relativePath);
        } else if (path.equals("reset")) {
            setRelativePath("/front-end/src/emails/Reset Password/images/");
            setFILE_PATH_ROOT(System.getProperty("user.dir") + relativePath);
        }
        byte[] image = new byte[0];
        try {
            image = FileUtils.readFileToByteArray(new File(FILE_PATH_ROOT+filename));
        } catch (IOException e) {
            throw new ImageNotFoundException();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
    }
}
