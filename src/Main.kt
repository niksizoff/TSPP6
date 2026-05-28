abstract class User(val id: String, val name: String, val email: String, val accessLevel: Int = 1) {
    fun login() = println(" [User] $name вошёл в систему")
    fun logout() = println(" [User] $name вышел из системы")
    fun checkAccess() = println(" [User] $name: уровень доступа = $accessLevel")
}

class Client(
    clientId: String,
    name: String,
    email: String,
    val registrationDate: String
) : User(clientId, name, email) {
    val contracts = mutableListOf<InsuranceContract>()

    fun viewContracts() {
        println(" Договоры клиента $name (${contracts.size} шт.):")
        contracts.forEach { println("  ${it.id} | Статус: ${it.getState()}") }
    }

    fun registerClaim(contract: InsuranceContract, amount: Double): Claim {
        println(" Клиент $name регистрирует случай на $amount для договора ${contract.id}")
        return Claim("CL-${System.currentTimeMillis()}", contract, amount)
    }
}