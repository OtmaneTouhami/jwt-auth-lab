(function () {
  const page = document.getElementById("root");
  const header = document.createElement("div");
  const body = document.createElement("div");
  const footer = document.createElement("div");
  const baseURL = "http://localhost:8080/api";

  header.classList.add("app-header");
  body.classList.add("app-body");
  footer.classList.add("app-footer");
  page.append(header, body, footer);

  function destructElement(element) {
    element.innerHTML = "";
  }

  function buildInput(options) {
    const inputWrapper = document.createElement("div");
    const label = document.createElement("label");
    const input = document.createElement("input");
    inputWrapper.classList.add("input-group");
    if (options.name) {
      label.setAttribute("for", options.name);
      label.textContent =
        options.name.charAt(0).toUpperCase() +
        options.name.slice(1).replace("-", " ");
      input.setAttribute("id", options.name);
    }
    for (const opt in options) {
      input.setAttribute(opt, options[opt]);
    }
    inputWrapper.append(label, input);
    return inputWrapper;
  }

  function buildCallToAction(message, linkMessage, action) {
    const callToAction = document.createElement("div");
    const callToActionText = document.createElement("span");
    const callToActionButton = document.createElement("button");
    callToAction.classList.add("call-to-action");
    callToActionText.textContent = message + " ";
    callToActionButton.textContent = linkMessage;
    callToActionButton.classList.add("link-button");
    callToActionButton.addEventListener("click", action);
    callToAction.append(callToActionText, callToActionButton);
    return callToAction;
  }

  function showToast(text, isError = false) {
    Toastify({
      text: text,
      duration: 3000,
      close: true,
      gravity: "bottom",
      position: "right",
      className: isError ? "toast toast-error" : "toast toast-success",
    }).showToast();
  }

  function buildConfirmationModal(message, onConfirm) {
    const overlay = document.createElement("div");
    overlay.classList.add("modal-overlay");

    const modalContent = `
      <div class="modal-content">
        <p>${message}</p>
        <div class="modal-actions">
          <button class="modal-cancel-btn">Cancel</button>
          <button class="modal-confirm-btn">Confirm</button>
        </div>
      </div>
    `;
    overlay.innerHTML = modalContent;

    overlay.querySelector(".modal-cancel-btn").addEventListener("click", () => {
      document.body.removeChild(overlay);
    });

    overlay
      .querySelector(".modal-confirm-btn")
      .addEventListener("click", () => {
        onConfirm();
        document.body.removeChild(overlay);
      });

    document.body.appendChild(overlay);
  }

  class ApiError extends Error {
    constructor(message, data, status) {
      super(message);
      this.data = data;
      this.status = status;
    }
  }

  async function handleApiResponse(response) {
    const data = await response.json();
    if (!response.ok) {
      throw new ApiError(
        data.message || "An API error occurred",
        data,
        response.status
      );
    }
    return data;
  }

  async function Login(username, password) {
    const response = await fetch(`${baseURL}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password }),
    });
    const result = await handleApiResponse(response);
    localStorage.setItem("token", result.token);
    localStorage.setItem("username", result.username);
  }

  async function Register(username, email, password) {
    const response = await fetch(`${baseURL}/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, email, password }),
    });
    await handleApiResponse(response);
  }

  async function fetchProtectedData() {
    const token = localStorage.getItem("token");
    if (!token) {
      showToast("You are not logged in.", true);
      return;
    }
    const response = await fetch(`${baseURL}/hello`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
    return await handleApiResponse(response);
  }

  function buildLogin() {
    destructElement(body);
    const container = document.createElement("div");
    const title = document.createElement("h3");
    const form = document.createElement("form");
    container.classList.add("form-container");
    title.classList.add("form-title");
    title.textContent = "Welcome Back! Login To Your Account";

    const usernameInput = buildInput({
      type: "text",
      name: "username",
      placeholder: "Enter your username",
      required: true,
    });
    const passwordInput = buildInput({
      type: "password",
      name: "password",
      placeholder: "Enter your password",
      required: true,
    });
    const submitButton = document.createElement("button");
    submitButton.type = "submit";
    submitButton.textContent = "Log In";
    submitButton.classList.add("submit-button");
    const callToAction = buildCallToAction(
      "You don't have an account?",
      "Sign Up",
      buildRegister
    );

    form.addEventListener("submit", async (event) => {
      event.preventDefault();
      const formData = new FormData(form);
      const username = formData.get("username");
      const password = formData.get("password");

      try {
        await Login(username, password);
        showToast("Login successful!");
        buildHome();
      } catch (error) {
        showToast(error.data.message || "Login failed.", true);
      }
    });

    form.append(usernameInput, passwordInput, submitButton);
    container.append(title, form, callToAction);
    body.append(container);
  }

  function buildRegister() {
    destructElement(body);
    const container = document.createElement("div");
    const title = document.createElement("h3");
    const form = document.createElement("form");
    container.classList.add("form-container", "form-register-container");
    form.classList.add("form-register");
    title.classList.add("form-title");
    title.textContent = "Create Your Personal Account";

    const usernameInput = buildInput({
      type: "text",
      name: "username",
      placeholder: "Enter your username",
      required: true,
    });
    const emailInput = buildInput({
      type: "email",
      name: "email",
      placeholder: "Enter your email",
      required: true,
    });
    const passwordInput = buildInput({
      type: "password",
      name: "password",
      placeholder: "At least 8 characters",
      minlength: 8,
      required: true,
    });
    const confirmPassword = buildInput({
      type: "password",
      name: "confirm-password",
      placeholder: "Confirm your password",
      minlength: 8,
      required: true,
    });
    passwordInput.classList.add("form-grid-full");
    confirmPassword.classList.add("form-grid-full");
    const submitButton = document.createElement("button");
    submitButton.type = "submit";
    submitButton.textContent = "Register";
    submitButton.classList.add("submit-button");
    const callToAction = buildCallToAction(
      "You already have an account?",
      "Log In",
      buildLogin
    );
    callToAction.classList.add("form-register-actions");

    form.addEventListener("submit", async (event) => {
      event.preventDefault();
      const formData = new FormData(form);
      const username = formData.get("username");
      const email = formData.get("email");
      const password = formData.get("password");
      const confirmPass = formData.get("confirm-password");

      if (password !== confirmPass) {
        showToast("Passwords do not match!", true);
        return;
      }

      try {
        await Register(username, email, password);
        showToast("Registration successful! Please log in.");
        buildLogin();
      } catch (error) {
        if (error.data && error.data.validationErrors) {
          const errors = Object.values(error.data.validationErrors).join("\n");
          showToast(errors, true);
        } else {
          showToast(error.data.message || "Registration failed.", true);
        }
      }
    });

    form.append(
      usernameInput,
      emailInput,
      passwordInput,
      confirmPassword,
      submitButton
    );
    container.append(title, form, callToAction);
    body.append(container);
  }

  function buildHome() {
    destructElement(body);
    const container = document.createElement("div");
    const title = document.createElement("h3");
    const dataDisplay = document.createElement("pre");
    const fetchDataButton = document.createElement("button");
    const logoutButton = document.createElement("button");

    const username = localStorage.getItem("username");
    title.textContent = `Welcome Home, ${username || "User"}!`;

    container.classList.add("panel");
    title.classList.add("panel-title");
    dataDisplay.classList.add("data-display");

    fetchDataButton.textContent = "Fetch Protected Data";
    fetchDataButton.classList.add("action-button");

    logoutButton.textContent = "Logout";
    logoutButton.classList.add("submit-button", "button-danger");

    fetchDataButton.addEventListener("click", async () => {
      try {
        const data = await fetchProtectedData();
        dataDisplay.textContent = JSON.stringify(data, null, 2);
        showToast("Data fetched successfully!");
      } catch (error) {
        if (
          error instanceof ApiError &&
          (error.status === 401 || error.status === 403)
        ) {
          handleLogout("Session expired. Please log in again.", true);
        } else {
          showToast(error.message || "Failed to fetch data.", true);
          dataDisplay.textContent = "Failed to fetch data.";
        }
      }
    });

    logoutButton.addEventListener("click", () => {
      buildConfirmationModal("Are you sure you want to log out?", () =>
        handleLogout()
      );
    });

    container.append(title, fetchDataButton, dataDisplay, logoutButton);
    body.append(container);
  }

  function handleLogout(
    message = "You have been logged out.",
    isError = false
  ) {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    showToast(message, isError);
    buildLogin();
  }

  function startApp() {
    const title = document.createElement("h1");
    title.textContent = "JWT SPA Application";
    title.classList.add("app-title");
    header.append(title);

    const token = localStorage.getItem("token");
    if (token) {
      buildHome();
    } else {
      buildLogin();
    }
  }

  startApp();
})();
