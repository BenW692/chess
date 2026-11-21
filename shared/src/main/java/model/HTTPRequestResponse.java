package model;

public record HTTPRequestResponse(boolean success, String message, String httpBody) {
}
