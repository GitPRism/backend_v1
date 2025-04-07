package com.gitprism.GitPRism.gitpullrequests.repository;

import com.gitprism.GitPRism.gitpullrequests.entity.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {
    Optional<PullRequest> findByGithubPrId(Long githubPrId);
}
