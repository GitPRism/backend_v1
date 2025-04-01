package com.gitprism.GitPRism.Repository.repository;

import com.gitprism.GitPRism.Repository.entity.Repo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepoRepository extends JpaRepository<Repo, Long> {
    List<Repo> findAllByGitIdAndIsDeletedFalse(String gitId);
}
