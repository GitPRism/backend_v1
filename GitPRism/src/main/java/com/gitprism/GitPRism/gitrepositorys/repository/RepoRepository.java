package com.gitprism.GitPRism.gitrepositorys.repository;

import com.gitprism.GitPRism.gitrepositorys.entity.Repo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface RepoRepository extends JpaRepository<Repo, Long> {
    List<Repo> findAllByGithubIdAndIsDeletedFalse(String githubId);
    Optional<Repo> findByGithubRepoId(Long githubRepoId);

    List<Repo> findByGithubId(String githubId);
}
