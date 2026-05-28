abstract class User(val id: String, val name: String, val email: String, val accessLevel: Int = 1) {
    fun login() = println(" [User] $name вошёл в систему")
    fun logout() = println(" [User] $name вышел из системы")
    fun checkAccess() = println(" [User] $name: уровень доступа = $accessLevel")
}