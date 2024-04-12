package com.khanhbq.socket.adapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SocketBroadcastMessage {

    @JsonProperty("s")
    private Long source;
    @JsonProperty("r")
    private String[] rooms;
    @JsonProperty("d")
    private Object[] data;
}
