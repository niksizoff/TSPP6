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

class InsuranceAgent(
    employeeId: String,
    name: String,
    email: String,
    val position: String
) : User(employeeId, name, email, accessLevel = 2) {

    fun calculatePremium(product: InsuranceProduct, clientAge: Int): Double {
        val multiplier = if (clientAge > 30) 1.2 else 1.0
        return product.basePremium * multiplier
    }

    fun printContract(contract: InsuranceContract) {
        println(" Агент распечатал договор ${contract.id} для клиента ${contract.client.name}")
    }
}

class ClaimManager(
    managerId: String,
    name: String,
    email: String,
    val specialization: String
) : User(managerId, name, email, accessLevel = 3) {

    fun processClaim(claim: Claim, approve: Boolean) {
        if (approve) {
            println(" Менеджер $name одобрил выплату по случаю ${claim.id}")
            claim.approve()
        } else {
            println(" Менеджер $name отклонил случай ${claim.id}")
            claim.reject()
        }
    }
}

class InsuranceProduct(
    val id: String,
    val name: String,
    val basePremium: Double,
    val minAge: Int = 18,
    val maxAge: Int = 65
) {
    fun isEligible(clientAge: Int): Boolean = clientAge in minAge..maxAge
}