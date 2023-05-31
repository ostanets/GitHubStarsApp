package com.ostanets.githubstars.data

import com.ostanets.githubstars.domain.GithubStarsAppRepo
import com.ostanets.githubstars.domain.User

class AppRepoImpl(private val appDao: AppDao) :
    GithubStarsAppRepo {
    override suspend fun addUser(user: User): Long {
        val insertedId = appDao.addUser(user.toEntity())
        if (user.Repositories.isNotEmpty()) {
            user.Repositories.forEach { repository ->
                addRepo(repository)

                if (repository.Stargazers.isNotEmpty()) {
                    repository.Stargazers.forEach { stargazer ->
                        addStargazer(stargazer)
                    }
                }
            }
        }
        return insertedId
    }

    override suspend fun addRepo(repository: GithubRepository): Long {
        return appDao.addRepository(repository.toEntity())
    }

    override suspend fun addStargazer(stargazer: GithubStargazer): Long {
        return appDao.addStargazer(stargazer.toEntity())
    }

    override suspend fun getUser(userId: Long): GithubUser? {
        var user = appDao.getUser(userId)?.fromEntity()

        user = user?.let { initRepos(it) }

        val initialedRepositories = user?.Repositories?.map { repository ->
            initStargazers(repository)
        }

        user?.Repositories = initialedRepositories as MutableList<GithubRepository>

        return user
    }

    override suspend fun getUser(login: String): GithubUser? {
        return appDao.getUser(login)?.fromEntity()
    }

    override suspend fun getRepo(repositoryId: Long): GithubRepository? {
        return appDao.getRepository(repositoryId)?.fromEntity(
            appDao.isRepositoryFavorite(repositoryId)
        )
    }

    override suspend fun isUserExist(login: String): Boolean {
        return appDao.getUser(login) != null
    }

    override suspend fun isRepoExist(repositoryId: Long): Boolean {
        return appDao.getRepository(repositoryId) != null
    }

    override suspend fun isRepoFavorite(repositoryId: Long): Boolean {
        return appDao.isRepositoryFavorite(repositoryId)
    }

    override suspend fun getFavorites(): List<GithubRepository>? {
        return appDao.getFavorites()?.map {
            it.fromEntity(true)
        }
    }

    override suspend fun getFavorites(userId: Long): List<GithubRepository>? {
        return appDao.getFavorites(userId)?.map {
            it.fromEntity(true)
        }
    }

    override suspend fun initRepos(user: GithubUser): GithubUser {
        val repositories = appDao.getRepositories(user.Id)?.map {
            it.fromEntity(
                appDao.isRepositoryFavorite(it.RepositoryId)
            )
        }
        user.Repositories = repositories as MutableList<GithubRepository>
        return user
    }

    override suspend fun initStargazers(repository: GithubRepository): GithubRepository {
        val stargazers = appDao.getStargazers(repository.Id)?.map {
            it.fromEntity()
        }
        repository.Stargazers = stargazers as MutableList<GithubStargazer>
        return repository
    }

    override suspend fun clearStargazers(repositoryId: Long) {
        appDao.clearStargazers(repositoryId)
    }

    override suspend fun editUser(user: GithubUser) {
        appDao.editUser(user.Id, user.Login, user.AvatarUrl)
    }

    override suspend fun editRepository(repository: GithubRepository) {
        appDao.editRepository(repository.Id, repository.Name)
    }

    override suspend fun addRepositoryToFavorites(repositoryId: Long) {
        appDao.addRepositoryToFavorites(repositoryId)
    }

    override suspend fun removeRepoFromFavorites(repositoryId: Long) {
        appDao.removeRepositoryFromFavorites(repositoryId)
    }

    override suspend fun deleteRepo(repositoryId: Long) {
        appDao.deleteRepository(repositoryId)
    }
}