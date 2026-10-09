import { useEffect, useState } from 'react'
import './App.css'

const emptyForm = {
  username: '',
  password: '',
  confirmPassword: '',
}

function App() {
  const isRegister = window.location.pathname === '/register'
  const [form, setForm] = useState(emptyForm)
  const [user, setUser] = useState(null)
  const [checking, setChecking] = useState(true)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    fetch('/api/auth/me')
        .then(async response => response.ok ? (await response.json()).user : null)
        .then(setUser)
        .catch(() => setError('Cannot reach the API. Check that Node is running.'))
        .finally(() => setChecking(false))
  }, [])

  function updateField(event) {
    setForm({ ...form, [event.target.name]: event.target.value })
  }

  async function submit(event) {
    event.preventDefault()
    setError('')
    setBusy(true)

    const endpoint = isRegister ? 'register' : 'login'
    const fields = isRegister
        ? form
        : { username: form.username, password: form.password }

    try {
      const response = await fetch(`/api/auth/${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(fields),
      })
      const data = await response.json()

      if (!response.ok) {
        setError(data.error || 'Authentication failed.')
        return
      }

      setUser(data.user)
      setForm(emptyForm)
      window.history.replaceState(null, '', '/')
    } catch {
      setError('Cannot reach the API. Check that Node is running.')
    } finally {
      setBusy(false)
    }
  }

  async function logout() {
    setError('')

    try {
      const response = await fetch('/api/auth/logout', { method: 'POST' })
      if (!response.ok) throw new Error('Logout failed.')
      window.location.assign('/login')
    } catch {
      setError('Logout failed. Check that Node is running.')
    }
  }

  return (
      <main className="auth-page">
        <section className="auth-card">
          <header className="auth-header">
            <h1 className="auth-brand">BudgetPath</h1>
            <h2 className="auth-title">
              {user ? 'Account' : isRegister ? 'Register' : 'Login'}
            </h2>
          </header>

          {error && <p className="validation-summary" role="alert">{error}</p>}

          {checking ? (
              <p>Checking session…</p>
          ) : user ? (
              <>
                <p role="status">Signed in as {user.username}.</p>
                <button
                    className="auth-primary-button"
                    type="button"
                    onClick={logout}
                >
                  Logout
                </button>
              </>
          ) : (
              <>
                <form className="auth-form" onSubmit={submit}>
                  <div className="auth-field">
                    <label htmlFor="username">Username</label>
                    <input
                        id="username"
                        name="username"
                        value={form.username}
                        onChange={updateField}
                        autoComplete="username"
                        placeholder="Enter username"
                        maxLength={100}
                        required
                    />
                  </div>

                  <div className="auth-field">
                    <label htmlFor="password">Password</label>
                    <input
                        id="password"
                        name="password"
                        type="password"
                        value={form.password}
                        onChange={updateField}
                        autoComplete={isRegister ? 'new-password' : 'current-password'}
                        placeholder="Enter password"
                        maxLength={100}
                        required
                    />
                  </div>

                  {isRegister && (
                      <div className="auth-field">
                        <label htmlFor="confirmPassword">Confirm Password</label>
                        <input
                            id="confirmPassword"
                            name="confirmPassword"
                            type="password"
                            value={form.confirmPassword}
                            onChange={updateField}
                            autoComplete="new-password"
                            placeholder="Confirm password"
                            required
                        />
                      </div>
                  )}

                  <button className="auth-primary-button" disabled={busy}>
                    {busy ? 'Please wait…' : isRegister ? 'Register' : 'Login'}
                  </button>
                </form>

                <div className="auth-switch">
                  <p>{isRegister ? 'Existing user?' : 'New user?'}</p>
                  <a
                      className="auth-secondary-button"
                      href={isRegister ? '/login' : '/register'}
                  >
                    {isRegister ? 'Login' : 'Register'}
                  </a>
                </div>
              </>
          )}
        </section>
      </main>
  )
}

export default App