package com.ostanets.githubstars.data

import com.ostanets.githubstars.domain.GithubRepository
import com.ostanets.githubstars.domain.GithubStargazer
import com.ostanets.githubstars.domain.GithubStarsAppRepository
import com.ostanets.githubstars.domain.GithubUser

class GithubStarsAppRepositoryImpl(private val githubStarsDao: GithubStarsDao) :
    GithubStarsAppRepository {
    override suspend fun addUser(user: GithubUser): Long {
        return githubStarsDao.addUser(user.toEntity())
    }

    override suspend fun addRepository(repository: GithubRepository): Long {
        return githubStarsDao.addRepository(repository.toEntity())
    }

    override suspend fun addStargazer(stargazer: GithubStargazer): Long {
        return githubStarsDao.addStargazer(stargazer.toEntity())
    }

    override suspend fun getUser(userId: Long): GithubUser {
        return githubStarsDao.getUser(userId).fromEntity()
    }

    override suspend fun getUser(login: String): GithubUser {
        return githubStarsDao.getUser(login).fromEntity()
    }

    override suspend fun getFavourites(): List<GithubRepository> {
        return githubStarsDao.getFavourites().map {
            it.fromEntity()
        }
    }

    override suspend fun initRepositories(user: GithubUser): GithubUser {
        val repositories = githubStarsDao.getRepositories(user.Id).map {
            it.fromEntity()
        }
        return user.copy(Repositories = repositories)
    }

    override suspend fun initStargazers(repository: GithubRepository): GithubRepository {
        val stargazers = githubStarsDao.getStargazers(repository.Id).map {
            it.fromEntity()
        }
        return repository.copy(Stargazers = stargazers)
    }

    override suspend fun addRepositoryToFavourites(repositoryId: Long) {
        githubStarsDao.addRepositoryToFavourites(repositoryId)
    }

    override suspend fun removeRepositoryFromFavourites(repositoryId: Long) {
        githubStarsDao.removeRepositoryFromFavourites(repositoryId)
    }
}