import { useState } from "react";

type Props = {
    onLoginSuccess: () => void;
};

export default function Login({ onLoginSuccess }: Props) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [msg, setMsg] = useState("");

    const login = async () => {
        const body = new URLSearchParams();
        body.append("username", username);
        body.append("password", password);

        const res = await fetch("/login", {
            method: "POST",
            body,
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
        });

        if (res.ok) {
            setMsg("");
            onLoginSuccess(); // 🔴 App wird umgeschaltet
        } else {
            setMsg("Login fehlgeschlagen");
        }
    };

    return (
        <div>
            <h3>LDAP Login</h3>

            <input
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
            />

            <br />

            <input
                type="password"
                placeholder="Passwort"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />

            <br />

            <button onClick={login}>Login</button>
            <p style={{ color: "red" }}>{msg}</p>
        </div>
    );
}
