package com.ostanets.githubstars.data

import com.ostanets.githubstars.domain.GithubRepository
import com.ostanets.githubstars.domain.GithubStargazer
import com.ostanets.githubstars.domain.GithubStarsAppRepository
import com.ostanets.githubstars.domain.GithubUser

class GithubStarsAppRepositoryImpl(private val githubStarsDao: GithubStarsDao) :
    GithubStarsAppRepository {
    override suspend fun addUser(user: GithubUser): Long {
        val insertedId = githubStarsDao.addUser(user.toEntity())
        if (!user.Repositories.isNullOrEmpty()) {
            user.Repositories.forEach {repository ->
                addRepository(repository)

                if (!repository.Stargazers.isNullOrEmpty()) {
                    repository.Stargazers.forEach {stargazer ->
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

        user = user?.copy(Repositories = initialedRepositories)

        return user
    }

    override suspend fun getUser(login: String): GithubUser? {
        return githubStarsDao.getUser(login)?.fromEntity()
    }

    override suspend fun getFavourites(): List<GithubRepository> {
        return githubStarsDao.getFavourites().map {
            githubStarsDao.getRepository(it)!!.fromEntity(true)
        }
    }

    override suspend fun isFavourite(repositoryId: Long): Boolean {
        return githubStarsDao.isRepositoryFavourite(repositoryId) != null
    }

    override suspend fun initRepositories(user: GithubUser): GithubUser {
        val repositories = githubStarsDao.getRepositories(user.Id)?.map {repository ->
            repository
                .fromEntity(
                    isFavourite(repository.RepositoryId)
                )
        }
        return user.copy(Repositories = repositories)
    }

    override suspend fun initStargazers(repository: GithubRepository): GithubRepository {
        val stargazers = githubStarsDao.getStargazers(repository.Id)?.map {
            it.fromEntity()
        }
        return repository.copy(Stargazers = stargazers)
    }

    override suspend fun addRepositoryToFavourites(repositoryId: Long) {
        githubStarsDao.addRepositoryToFavourites(FavouriteRepository(repositoryId))
    }

    override suspend fun removeRepositoryFromFavourites(repositoryId: Long) {
        githubStarsDao.removeRepositoryFromFavourites(FavouriteRepository(repositoryId))
    }
}