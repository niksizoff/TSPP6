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
        require(state == ContractState.CREATED) { " Нельзя активировать: текущее состояние $state" }
        state = ContractState.ACTIVE
        println(" [Состояние] Договор $id -> АКТИВНЫЙ (оплата получена)")
    }

    fun suspend() {
        require(state == ContractState.ACTIVE) { " Нельзя приостановить: текущее состояние $state" }
        state = ContractState.SUSPENDED
        println(" [Состояние] Договор $id -> ПРИОСТАНОВЛЕН (просрочка оплаты)")
    }

    fun resume() {
        require(state == ContractState.SUSPENDED) { " Нельзя возобновить: текущее состояние $state" }
        state = ContractState.ACTIVE
        println(" [Состояние] Договор $id -> ВОЗОБНОВЛЁН (долг погашен)")
    }

    fun terminate() {
        require(state in listOf(ContractState.ACTIVE, ContractState.SUSPENDED)) { " Нельзя расторгнуть: текущее состояние $state" }
        state = ContractState.TERMINATED
        println(" [Состояние] Договор $id -> РАСТОРГНУТ")
    }

    fun checkExpiration() {
        require(state == ContractState.ACTIVE) { " Проверка срока возможна только для активных договоров" }
        state = ContractState.ENDED
        println(" [Состояние] Договор $id -> ЗАВЕРШЁН (истёк срок)")
    }
}

enum class ClaimState { REGISTERED, APPROVED, REJECTED }

enum class ClaimState { REGISTERED, APPROVED, REJECTED }

class Claim(val id: String, val contract: InsuranceContract, var amount: Double) {
    private var state = ClaimState.REGISTERED

    fun approve() {
        require(contract.getState() == ContractState.ACTIVE) { " Выплата невозможна: договор не активен" }
        state = ClaimState.APPROVED
        println(" [Claim] Случай $id одобрен. Выплата: $amount")
    }

    fun reject() {
        state = ClaimState.REJECTED
        println(" [Claim] Случай $id отклонён менеджером")
    }

    fun getState() = state
}

fun main() {
    println("===  Система Офиса Страховой Фирмы ===\n")

    val client = Client("C1", "Иван Петров", "ivan@mail.com", "2024-01-15")
    val agent = InsuranceAgent("A1", "Анна Сидорова", "anna@ins.com", "Старший агент")
    val manager = ClaimManager("M1", "Олег Козлов", "oleg@ins.com", "Авто-страхование")
    val product = InsuranceProduct("P1", "Автострахование", 5000.0)

    client.login()
    agent.login()

    val contract = InsuranceContract("DOC-101", client, product, "2024-01-01", "2025-01-01")
    client.contracts.add(contract)
    println("Договор создан: ${contract.getState()}\n")

    contract.activate()
    println("После оплаты: ${contract.getState()}\n")

    contract.suspend()
    println("Просрочка: ${contract.getState()}\n")

    contract.resume()
    println("Долг погашен: ${contract.getState()}\n")

    val claim = client.registerClaim(contract, 15000.0)
    manager.processClaim(claim, approve = true)
    println()

    contract.checkExpiration()
    println("5️ Итог: ${contract.getState()}\n")

    client.viewContracts()
    client.logout()
    agent.logout()
}