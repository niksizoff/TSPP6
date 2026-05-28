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

enum class ContractState { CREATED, ACTIVE, SUSPENDED, TERMINATED, ENDED }

class InsuranceContract(
    val id: String,
    val client: Client,
    val product: InsuranceProduct,
    val startDate: String,
    val endDate: String
) {
    private var state = ContractState.CREATED

    fun getState() = state

    fun activate() {
        require(state == ContractState.CREATED) { "⛔ Нельзя активировать: текущее состояние $state" }
        state = ContractState.ACTIVE
        println(" [Состояние] Договор $id -> АКТИВНЫЙ (оплата получена)")
    }

    fun suspend() {
        require(state == ContractState.ACTIVE) { " Нельзя приостановить: текущее состояние $state" }
        state = ContractState.SUSPENDED
        println(" [Состояние] Договор $id -> ПРИОСТАНОВЛЕН (просрочка оплаты)")
    }

    fun resume() {
        require(state == ContractState.SUSPENDED) { "⛔ Нельзя возобновить: текущее состояние $state" }
        state = ContractState.ACTIVE
        println(" [Состояние] Договор $id -> ВОЗОБНОВЛЁН (долг погашен)")
    }

    fun terminate() {
        require(state in listOf(ContractState.ACTIVE, ContractState.SUSPENDED)) { "⛔ Нельзя расторгнуть: текущее состояние $state" }
        state = ContractState.TERMINATED
        println(" [Состояние] Договор $id -> РАСТОРГНУТ")
    }

    fun checkExpiration() {
        require(state == ContractState.ACTIVE) { "⛔ Проверка срока возможна только для активных договоров" }
        state = ContractState.ENDED
        println(" [Состояние] Договор $id -> ЗАВЕРШЁН (истёк срок)")
    }
}