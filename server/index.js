const express = require('express');
const db = require('./db');
const session = require('express-session');
const PgSession = require('connect-pg-simple')(session);
const bcrypt = require('bcryptjs');

const app = express();
app.use(express.json());

if (!process.env.SESSION_SECRET) {
    throw new Error('SESSION_SECRET is missing');
}

app.use(session({
    name: 'budgetpath_js.sid',
    store: new PgSession({
        pool: db,
        tableName: 'auth_sessions',
        createTableIfMissing: true
    }),
    secret: process.env.SESSION_SECRET,
    resave: false,
    saveUninitialized: false,
    cookie: { httpOnly: true, sameSite: 'lax', secure: false }
}));

app.get('/api/health', async (_req, res) => {
    try {
        await db.query('SELECT 1');
        res.json({ status: 'ok' });
    } catch (error) {
        console.error(error);
        res.status(503).json({ error: 'Database unavailable' });
    }
});

app.post('/api/auth/register', async (req, res) => {
    const { username, password, confirmPassword } = req.body ?? {};

    if (typeof username !== 'string' ||
        typeof password !== 'string' ||
        typeof confirmPassword !== 'string') {
        return res.status(400).json({ error: 'All fields are required.' });
    }

    const name = username.trim();

    if (!name || name.length > 100 ||
        !password || password.length > 100 ||
        bcrypt.truncates(password)) {
        return res.status(400).json({ error: 'Invalid username or password.' });
    }

    if (password !== confirmPassword) {
        return res.status(400).json({ error: 'The passwords do not match.' });
    }

    try {
        const passwordHash = await bcrypt.hash(password, 12);
        const result = await db.query(
            'INSERT INTO auth_users (username, password_hash) VALUES ($1, $2) RETURNING id, username',
            [name, passwordHash]
        );

        await new Promise((resolve, reject) =>
            req.session.regenerate(error => error ? reject(error) : resolve())
        );
        req.session.userId = result.rows[0].id;
        await new Promise((resolve, reject) =>
            req.session.save(error => error ? reject(error) : resolve())
        );

        return res.status(201).json({ user: result.rows[0] });
    } catch (error) {
        if (error.code === '23505') {
            return res.status(409).json({ error: 'Username already exists.' });
        }
        console.error(error);
        return res.status(500).json({ error: 'Registration failed.' });
    }
});

app.post('/api/auth/login', async (req, res) => {
    const { username, password } = req.body ?? {};
    const name = typeof username === 'string' ? username.trim() : '';

    if (!name || name.length > 100 ||
        typeof password !== 'string' || !password ||
        password.length > 100 || bcrypt.truncates(password)) {
        return res.status(401).json({ error: 'Invalid username or password.' });
    }

    try {
        const result = await db.query(
            'SELECT id, username, password_hash FROM auth_users WHERE LOWER(username) = LOWER($1)',
            [name]
        );
        const user = result.rows[0];

        if (!user || !(await bcrypt.compare(password, user.password_hash))) {
            return res.status(401).json({ error: 'Invalid username or password.' });
        }

        await new Promise((resolve, reject) =>
            req.session.regenerate(error => error ? reject(error) : resolve())
        );
        req.session.userId = user.id;
        await new Promise((resolve, reject) =>
            req.session.save(error => error ? reject(error) : resolve())
        );

        return res.json({ user: { id: user.id, username: user.username } });
    } catch (error) {
        console.error(error);
        return res.status(500).json({ error: 'Login failed.' });
    }
});

app.get('/api/auth/me', async (req, res) => {
    if (!req.session.userId) {
        return res.status(401).json({ error: 'Not signed in.' });
    }

    try {
        const result = await db.query(
            'SELECT id, username FROM auth_users WHERE id = $1',
            [req.session.userId]
        );

        if (!result.rows[0]) {
            return res.status(401).json({ error: 'Not signed in.' });
        }

        return res.json({ user: result.rows[0] });
    } catch (error) {
        console.error(error);
        return res.status(500).json({ error: 'Could not check session.' });
    }
});

app.post('/api/auth/logout', (req, res) => {
    req.session.destroy(error => {
        if (error) {
            console.error(error);
            return res.status(500).json({ error: 'Logout failed.' });
        }

        res.clearCookie('budgetpath_js.sid', { path: '/' });
        return res.status(204).end();
    });
});

app.listen(3002, '127.0.0.1', () => {
    console.log('API running at http://127.0.0.1:3002');
});