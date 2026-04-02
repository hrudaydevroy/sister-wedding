package com.wedding.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "Root",
            "api_key", "571594655958457",
            "api_secret", "UQE2B11VFej2Nc-qiP0B0zPTAsc"
        ));
    }
}
