package com.sid.moviebkg.user.authentication.config;

import com.sid.moviebkg.common.dto.MessageDto;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "moviebkg.response")
public class ResponseMsgConfiguration {
    private List<MessageDto> messages;
}
