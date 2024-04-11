package com.khanhbq.socket;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ChatMessage {

    private String msg;
}
