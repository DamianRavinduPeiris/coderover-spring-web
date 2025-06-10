package com.damian.coderover.response;

import java.io.Serializable;

public record Response(String message, Object data, int statusCode) implements Serializable {
}
