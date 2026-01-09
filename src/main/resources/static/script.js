/**
 * WebAuthn Utility Functions
 */
const WebAuthnUtils = {
    base64urlToUint8Array(base64url) {
        const base64 = base64url.replace(/-/g, "+").replace(/_/g, "/");
        const binary = window.atob(base64);
        return Uint8Array.from(binary, c => c.charCodeAt(0));
    },

    isSupported() {
        return !!window.PublicKeyCredential;
    }
};

/**
 * Passkey Authentication (for login page)
 */
class PasskeyAuth {
    constructor({ headers, signinButton }) {
        this.headers = headers;
        this.signinButton = signinButton;
    }

    init() {
        if (!this.signinButton) return;
        this.signinButton.addEventListener("click", () => this.authenticate());
    }

    async authenticate() {
        if (!WebAuthnUtils.isSupported()) {
            alert("Passkey is not supported.");
            return;
        }

        try {
            const optionsResponse = await fetch("/webauthn/authenticate/options", {
                method: "POST",
                headers: { ...this.headers, "content-type": "application/json" }
            }).then(r => r.json());

            const options = {
                ...optionsResponse,
                challenge: WebAuthnUtils.base64urlToUint8Array(optionsResponse.challenge),
                allowCredentials: optionsResponse.allowCredentials?.map(cred => ({
                    ...cred,
                    id: WebAuthnUtils.base64urlToUint8Array(cred.id)
                }))
            };

            const credential = await navigator.credentials.get({ publicKey: options });

            const response = await fetch("/login/webauthn", {
                method: "POST",
                headers: { ...this.headers, "content-type": "application/json" },
                body: JSON.stringify(credential)
            });

            if (response.ok) {
                window.location.href = "/";
                return;
            }
        } catch (err) {
            console.error(err);
        }

        alert("Could not login with passkey");
    }
}

/**
 * Passkey Management (for passkeys page)
 */
class PasskeyManager {
    constructor({ headers, modal, passkeyNameInput, addPasskeyButton, addButtons, closeButtons, deleteButtons }) {
        this.headers = headers;
        this.modal = modal;
        this.passkeyNameInput = passkeyNameInput;
        this.addPasskeyButton = addPasskeyButton;
        this.addButtons = addButtons;
        this.closeButtons = closeButtons;
        this.deleteButtons = deleteButtons;
    }

    init() {
        this.addButtons.forEach(btn => btn.addEventListener("click", () => this.showModal()));
        this.closeButtons.forEach(btn => btn.addEventListener("click", e => this.hideModal(e)));
        this.addPasskeyButton?.addEventListener("click", () => this.addPasskey());
        this.passkeyNameInput?.addEventListener("keydown", e => {
            if (e.key === "Enter") {
                e.preventDefault();
                this.addPasskey();
            }
        });
        this.deleteButtons.forEach(btn => btn.addEventListener("click", e => this.deletePasskey(e)));
    }

    showModal() {
        if (this.modal) this.modal.style.display = "flex";
    }

    hideModal(event) {
        if (event && event.target !== event.currentTarget) return;
        if (this.modal) this.modal.style.display = "none";
        if (this.passkeyNameInput) this.passkeyNameInput.value = "";
    }

    async addPasskey() {
        const label = this.passkeyNameInput?.value;
        if (!label) {
            console.error("must set passkey label");
            return;
        }

        try {
            const optionsResponse = await fetch("/webauthn/register/options", {
                method: "POST",
                headers: this.headers
            }).then(r => r.json());

            const options = {
                ...optionsResponse,
                excludeCredentials: [],
                user: {
                    ...optionsResponse.user,
                    id: WebAuthnUtils.base64urlToUint8Array(optionsResponse.user.id)
                },
                challenge: WebAuthnUtils.base64urlToUint8Array(optionsResponse.challenge)
            };

            const credential = await navigator.credentials.create({ publicKey: options });

            await fetch("/webauthn/register", {
                method: "POST",
                headers: { ...this.headers, "content-type": "application/json" },
                body: JSON.stringify({ publicKey: { credential, label } })
            });

            window.location.href = window.location.pathname + "?success";
        } catch (e) {
            console.error("Could not register passkey", e);
        }

        this.hideModal();
    }

    async deletePasskey(evt) {
        const id = evt.target.getAttribute("data-id");
        if (!confirm("Are you sure you want to delete this passkey?")) return;

        const response = await fetch(`/webauthn/register/${id}`, {
            method: "DELETE",
            headers: this.headers
        });

        if (response.status === 204) {
            evt.target.closest(".passkey-item").remove();
        } else {
            alert(`Failed to delete passkey (status:${response.status})`);
        }
    }
}
