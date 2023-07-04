package com.ostanets.githubstars.data

import com.ostanets.githubstars.domain.AppRepo
import com.ostanets.githubstars.domain.Repo
import com.ostanets.githubstars.domain.Stargazer
import com.ostanets.githubstars.domain.User

class AppRepoImpl(private val appDao: AppDao) :
    AppRepo {
    override suspend fun addUser(user: User): Long {
        user as UserBody
        val insertedId = appDao.addUser(user)
        if (user.repos.isNotEmpty()) {
            user.repos.forEach { repo ->
                addRepo(repo)

                if (repo.stargazers.isNotEmpty()) {
                    repo.stargazers.forEach { stargazer ->
                        addStargazer(stargazer)
                    }
                }
            }
        }
        return insertedId
    }

    override suspend fun addRepo(repo: Repo): Long {
        repo as RepoBody
        return appDao.addRepo(repo)
    }

    override suspend fun addStargazer(stargazer: Stargazer): Long {
        stargazer as StargazerBody
        return appDao.addStargazer(stargazer)
    }

    override suspend fun getUser(userId: Long): User? {
        var user = appDao.getUser(userId)

        user = user?.let { initRepos(it) as UserBody }

        val initialedRepos = user?.repos?.map { repo ->
            initStargazers(repo)
        }

        initialedRepos?.let { user?.repos = it }

        return user
    }

    override suspend fun getUser(login: String): User? {
        return appDao.getUser(login)
    }

    override suspend fun getRepo(repoId: Long): Repo? {
        return appDao.getRepo(repoId)
    }

    override suspend fun isUserExist(login: String): Boolean {
        return appDao.getUser(login) != null
    }

    override suspend fun isRepoExist(repoId: Long): Boolean {
        return appDao.getRepo(repoId) != null
    }

    override suspend fun isRepoFavorite(repoId: Long): Boolean {
        return appDao.isRepoFavorite(repoId)
    }

    override suspend fun getFavorites(): List<Repo>? {
        return appDao.getFavorites()
    }

    override suspend fun getFavorites(userId: Long): List<Repo>? {
        return appDao.getFavorites(userId)
    }

    override suspend fun initRepos(user: User): User {
        user as UserBody
        val repos = appDao.getRepos(user.id)
        repos?.let { user.repos = it }
        return user
    }

    override suspend fun initStargazers(repo: Repo): Repo {
        repo as RepoBody
        val stargazers = appDao.getStargazers(repo.id)
        stargazers?.let { repo.stargazers = it }
        return repo
    }

    override suspend fun clearStargazers(repoId: Long) {
        appDao.clearStargazers(repoId)
    }

    override suspend fun editUser(user: User) {
        appDao.editUser(user.id, user.login, user.avatarUrl)
    }

    override suspend fun editRepo(repo: Repo) {
        appDao.editRepo(repo.id, repo.name)
    }

    override suspend fun addRepoToFavorites(repoId: Long) {
        appDao.addRepoToFavorites(repoId)
    }

    override suspend fun removeRepoFromFavorites(repoId: Long) {
        appDao.removeRepoFromFavorites(repoId)
    }

    override suspend fun deleteRepo(repoId: Long) {
        appDao.deleteRepo(repoId)
    }
}