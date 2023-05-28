package com.ostanets.githubstars.data

import com.ostanets.githubstars.domain.GithubRepository
import com.ostanets.githubstars.domain.GithubStargazer
import com.ostanets.githubstars.domain.GithubStarsAppRepository
import com.ostanets.githubstars.domain.GithubUser

class GithubStarsAppRepositoryImpl(private val githubStarsDao: GithubStarsDao) :
    GithubStarsAppRepository {
    override suspend fun addUser(user: GithubUser): Long {
        val insertedId = githubStarsDao.addUser(user.toEntity())
        if (user.Repositories.isNotEmpty()) {
            user.Repositories.forEach { repository ->
                addRepository(repository)

                if (!repository.Stargazers.isNullOrEmpty()) {
                    repository.Stargazers!!.forEach { stargazer ->
                        addStargazer(stargazer)
                    }
                }
            }
        }
        return insertedId
    }

    override suspend fun addRepository(repository: GithubRepository): Long {
        return githubStarsDao.addRepository(repository.toEntity())
    }

    override suspend fun addStargazer(stargazer: GithubStargazer): Long {
        return githubStarsDao.addStargazer(stargazer.toEntity())
    }

    override suspend fun getUser(userId: Long): GithubUser? {
        var user = githubStarsDao.getUser(userId)?.fromEntity()

        user = user?.let { initRepositories(it) }

        val initialedRepositories = user?.Repositories?.map { repository ->
            initStargazers(repository)
        }

        user?.Repositories = initialedRepositories as MutableList<GithubRepository>

        return user
    }

    override suspend fun getUser(login: String): GithubUser? {
        return githubStarsDao.getUser(login)?.fromEntity()
    }

    override suspend fun isUserExist(login: String): Boolean {
        return githubStarsDao.getUser(login) != null
    }

    override suspend fun isRepositoryExist(repositoryId: Long): Boolean {
        return githubStarsDao.getRepository(repositoryId) != null
    }

    override suspend fun isRepositoryFavorite(repositoryId: Long): Boolean {
        return githubStarsDao.isRepositoryFavorite(repositoryId)
    }

    override suspend fun getFavorites(): List<GithubRepository>? {
        return githubStarsDao.getFavorites()?.map {
            it.fromEntity(true)
        }
    }

    override suspend fun getFavorites(userId: Long): List<GithubRepository>? {
        return githubStarsDao.getFavorites(userId)?.map {
            it.fromEntity(true)
        }
    }

    override suspend fun initRepositories(user: GithubUser): GithubUser {
        val repositories = githubStarsDao.getRepositories(user.Id)?.map {
            it.fromEntity(
                githubStarsDao.isRepositoryFavorite(it.RepositoryId)
            )
        }
        user.Repositories = repositories as MutableList<GithubRepository>
        return user
    }

    override suspend fun initStargazers(repository: GithubRepository): GithubRepository {
        val stargazers = githubStarsDao.getStargazers(repository.Id)?.map {
            it.fromEntity()
        }
        repository.Stargazers = stargazers
        return repository
    }

    override suspend fun editUser(user: GithubUser) {
        githubStarsDao.editUser(user.Id, user.Login, user.AvatarUrl)
    }

    override suspend fun editRepository(repository: GithubRepository) {
        githubStarsDao.editRepository(repository.Id, repository.Name)
    }

    override suspend fun addRepositoryToFavorites(repositoryId: Long) {
        githubStarsDao.addRepositoryToFavorites(repositoryId)
    }

    override suspend fun removeRepositoryFromFavorites(repositoryId: Long) {
        githubStarsDao.removeRepositoryFromFavorites(repositoryId)
    }

    override suspend fun deleteRepository(repositoryId: Long) {
        githubStarsDao.deleteRepository(repositoryId)
    }
}