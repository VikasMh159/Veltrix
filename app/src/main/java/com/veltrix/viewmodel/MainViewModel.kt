package com.veltrix.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.veltrix.AppTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class Employee(
    val id: String,
    val name: String,
    val role: String,
    val rating: Float,
    val avatarUrl: String
)

data class MainUiState(
    val isSplashVisible: Boolean = true,
    val isLoggedIn: Boolean = false,
    val selectedTab: AppTab = AppTab.Dashboard,
    val isDarkMode: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val email: String = "admin@veltrix.io",
    val password: String = "password",
    val isLoginLoading: Boolean = false,
    val authError: String? = null,
    val employees: List<Employee> = sampleEmployees,
    val searchQuery: String = ""
) {
    val filteredEmployees: List<Employee>
        get() = if (searchQuery.isBlank()) employees else {
            employees.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                    it.role.contains(searchQuery, ignoreCase = true)
            }
        }

    val totalTasks: Int
        get() = employees.size * 3 + 18

    val avgRating: Int
        get() = ((employees.map { it.rating }.average().takeIf { !it.isNaN() } ?: 0.0) * 20).toInt()
}

class MainViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    init {
        _uiState.update { it.copy(isLoggedIn = auth.currentUser != null) }
    }

    fun finishSplash() {
        _uiState.update { it.copy(isSplashVisible = false) }
    }

    fun updateEmail(value: String) {
        _uiState.update { it.copy(email = value, authError = null) }
    }

    fun updatePassword(value: String) {
        _uiState.update { it.copy(password = value, authError = null) }
    }

    fun login() {
        val state = _uiState.value
        val email = state.email.trim()
        val password = state.password

        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(authError = "Authentication fail") }
            return
        }

        _uiState.update { it.copy(isLoginLoading = true, authError = null) }
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _uiState.update {
                    it.copy(
                        isLoggedIn = true,
                        isLoginLoading = false,
                        authError = null
                    )
                }
            }
            .addOnFailureListener { error ->
                _uiState.update {
                    it.copy(
                        isLoginLoading = false,
                        authError = "Authentication fail"
                    )
                }
            }
    }

    fun loginWithGoogle(idToken: String) {
        if (idToken.isBlank()) {
            _uiState.update { it.copy(authError = "Authentication fail") }
            return
        }

        _uiState.update { it.copy(isLoginLoading = true, authError = null) }
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                _uiState.update {
                    it.copy(
                        isLoggedIn = true,
                        isLoginLoading = false,
                        authError = null
                    )
                }
            }
            .addOnFailureListener {
                auth.signOut()
                _uiState.update {
                    it.copy(
                        isLoginLoading = false,
                        authError = "Authentication fail"
                    )
                }
            }
    }

    fun logout() {
        auth.signOut()
        _uiState.update {
            it.copy(
                isLoggedIn = false,
                selectedTab = AppTab.Dashboard,
                password = "",
                isLoginLoading = false,
                authError = null
            )
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
    }

    fun toggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun addEmployee(employee: Employee) {
        _uiState.update { it.copy(employees = listOf(employee) + it.employees) }
    }

    fun updateEmployee(employee: Employee) {
        _uiState.update { state ->
            state.copy(employees = state.employees.map { if (it.id == employee.id) employee else it })
        }
    }

    fun noop() = Unit
}

private val sampleEmployees = listOf(
    Employee("1", "Aarav Sharma", "Chief Executive Officer", 4.9f, ""),
    Employee("2", "Nisha Mehta", "HR Manager", 4.7f, ""),
    Employee("3", "Rohan Iyer", "Senior Developer", 4.8f, ""),
    Employee("4", "Maya Kapoor", "Product Manager", 4.6f, ""),
    Employee("5", "Kabir Verma", "QA Engineer", 4.2f, ""),
    Employee("6", "Ishita Rao", "DevOps Engineer", 4.5f, ""),
    Employee("7", "Aditya Singh", "Sales Executive", 4.1f, ""),
    Employee("8", "Sara Khan", "UI Designer", 4.8f, ""),
    Employee("9", "Dev Patel", "Data Analyst", 4.4f, ""),
    Employee("10", "Tara Joseph", "Recruiter", 4.0f, ""),
    Employee("11", "Kunal Das", "Tech Lead", 4.9f, ""),
    Employee("12", "Ritika Sen", "Customer Success", 4.3f, ""),
    Employee("13", "Priya Nair", "Finance Executive", 4.4f, ""),
    Employee("14", "Vikram Sethi", "Operations Manager", 4.6f, ""),
    Employee("15", "Ananya Bose", "Marketing Lead", 4.7f, "")
)
