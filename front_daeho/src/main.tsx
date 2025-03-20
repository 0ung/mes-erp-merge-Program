import { createRoot } from "react-dom/client";
import App from "./App.tsx";
import { BrowserRouter } from "react-router-dom";
import LoginProvider from "./context/LoginProvider.tsx";
import NameProvider from "./context/nameProvider.tsx";

createRoot(document.getElementById("root")!).render(
  <BrowserRouter>
    <LoginProvider>
      <NameProvider>
        <App />
      </NameProvider>
    </LoginProvider>
  </BrowserRouter>
);
