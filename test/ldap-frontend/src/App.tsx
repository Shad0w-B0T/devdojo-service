import { useState } from "react";
import Login from "./Login";

export default function App() {
    const [result, setResult] = useState("");
    const [loggedIn, setLoggedIn] = useState(false);

    const apiFetch = async (url: string, options?: RequestInit) => {
        const res = await fetch(url, options);

        if (res.status === 401) {
            setLoggedIn(false);
            throw new Error("Unauthorized");
        }

        return res;
    };

    const callApi = async () => {
        try {
            const res = await apiFetch("/api");
            setResult(await res.text());
        } catch {
            setResult("");
        }
    };

    const logout = async () => {
        await fetch("/api/logout", { method: "POST" });
        setLoggedIn(false);
        setResult("");
    };

    return (
        <div>
            {!loggedIn ? (
                <Login onLoginSuccess={() => setLoggedIn(true)} />
            ) : (
                <>
                    <p style={{ color: "green" }}>Eingeloggt</p>

                    <button onClick={callApi}>Call /api</button>
                    <button onClick={logout}>Logout</button>

                    <pre>{result}</pre>
                </>
            )}
        </div>
    );
}
