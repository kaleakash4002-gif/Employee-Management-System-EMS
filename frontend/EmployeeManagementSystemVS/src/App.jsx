
import { useState } from "react";
import "./App.css";

function App() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [token, setToken] = useState(localStorage.getItem("token") || "");
  const [employees, setEmployees] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e) => {
    e.preventDefault();
    setMessage("");
    setLoading(true);

    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      const data = await response.json();

      if (!response.ok) {
        setMessage(data.message || "Invalid email or password");
        return;
      }

      localStorage.setItem("token", data.token);
      setToken(data.token);
      await loadEmployees(data.token);
    } catch {
      setMessage("Cannot connect to the backend.");
    } finally {
      setLoading(false);
    }
  };

  const loadEmployees = async (authToken) => {
    try {
      const response = await fetch("http://localhost:8080/api/employees", {
        headers: { Authorization: `Bearer ${authToken}` },
      });

      if (!response.ok) {
        setMessage("Could not load employees. Please login again.");
        return;
      }

      const data = await response.json();
      setEmployees(data);
      setToken(authToken);
      setMessage("");
    } catch {
      setMessage("Could not connect to the backend.");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    setToken("");
    setEmployees([]);
    setEmail("");
    setPassword("");
    setMessage("");
  };

  if (token) {
    return (
      <div className="dashboard">
        <header className="dashboard-header">
          <h1>Employee Management System</h1>
          <button onClick={handleLogout}>Logout</button>
        </header>

        <main className="dashboard-content">
          <h2>Employee Dashboard</h2>
          <p>Total Employees: {employees.length}</p>

          {message && <p className="message">{message}</p>}

          <div className="employee-list">
            {employees.map((employee) => (
              <div className="employee-card" key={employee.id}>
                <h3>{employee.name}</h3>
                <p>ID: {employee.id}</p>
                <p>Email: {employee.email}</p>
              </div>
            ))}
          </div>

          <button onClick={() => loadEmployees(token)}>
            Refresh Employees
          </button>
        </main>
      </div>
    );
  }

  return (
    <div className="login-container">
      <form className="login-card" onSubmit={handleLogin}>
        <h1>Employee Management</h1>
        <p>Login to your account</p>

        <label>Email</label>
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Enter your email"
          required
        />

        <label>Password</label>
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Enter your password"
          required
        />

        <button type="submit" disabled={loading}>
          {loading ? "Logging in..." : "Login"}
        </button>

        {message && <p className="message">{message}</p>}
      </form>
    </div>
  );
}

export default App;
