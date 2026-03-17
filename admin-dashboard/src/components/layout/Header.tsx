"use client";

import { clearToken } from "@/lib/api";
import { useRouter } from "next/navigation";

export function Header() {
  const router = useRouter();

  const handleLogout = () => {
    clearToken();
    router.push("/login");
  };

  return (
    <header className="h-16 bg-zinc-900 border-b border-zinc-800 flex items-center justify-between px-6">
      <div />
      <button
        onClick={handleLogout}
        className="text-sm text-zinc-400 hover:text-white transition-colors"
      >
        Sign out
      </button>
    </header>
  );
}
