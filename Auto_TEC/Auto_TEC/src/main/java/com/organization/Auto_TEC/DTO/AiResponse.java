package com.organization.Auto_TEC.DTO;

public class AiResponse {
    private String answer;
    private String warning;

    public AiResponse() {}

    public AiResponse(String answer) {
        this.answer = answer;
    }

    public AiResponse(String answer, String warning) {
        this.answer = answer;
        this.warning = warning;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }
}
