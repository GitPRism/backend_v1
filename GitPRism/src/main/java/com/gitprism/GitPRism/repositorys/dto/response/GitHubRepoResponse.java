package com.gitprism.GitPRism.Repository.dto.response;

public class GitHubRepoResponse {
    private String name;
    private String htmlUrl;
    private String description;
    private String language;

    public GitHubRepoResponse() {}

    public GitHubRepoResponse(String name, String htmlUrl, String description, String language) {
        this.name = name;
        this.htmlUrl = htmlUrl;
        this.description = description;
        this.language = language;
    }

    // Getters & Setters
    public String getName() { return name; }
    public String getHtmlUrl() { return htmlUrl; }
    public String getDescription() { return description; }
    public String getLanguage() { return language; }

    public void setName(String name) { this.name = name; }
    public void setHtmlUrl(String htmlUrl) { this.htmlUrl = htmlUrl; }
    public void setDescription(String description) { this.description = description; }
    public void setLanguage(String language) { this.language = language; }
}
