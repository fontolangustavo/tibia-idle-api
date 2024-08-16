package com.fontolan.tibiaidle.controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/api/v1/public")
public class PublicController {
    private final Path baseLocation = Paths.get("src/main/resources/static/images");

    @GetMapping("/images/{category}/{type}/{imageName:.+}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String category,
            @PathVariable String type,
            @PathVariable String imageName) {
        try {
            Path file = baseLocation.resolve(Paths.get(category, type, imageName));
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg"); // Ajuste o tipo de conteúdo conforme necessário
                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
