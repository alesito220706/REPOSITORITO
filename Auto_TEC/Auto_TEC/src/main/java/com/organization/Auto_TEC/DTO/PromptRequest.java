package com.organization.Auto_TEC.DTO;

import jakarta.validation.constraints.NotBlank;

public class PromptRequest {

    @NotBlank(message = "prompt is required")
    private String prompt;

    public PromptRequest() {}

    public PromptRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
