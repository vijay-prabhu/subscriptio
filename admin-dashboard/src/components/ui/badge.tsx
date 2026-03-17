import { cn } from "@/lib/utils";

const variants: Record<string, string> = {
  default: "bg-zinc-800 text-zinc-200",
  success: "bg-emerald-900/50 text-emerald-400 border border-emerald-800",
  warning: "bg-amber-900/50 text-amber-400 border border-amber-800",
  danger: "bg-red-900/50 text-red-400 border border-red-800",
  info: "bg-blue-900/50 text-blue-400 border border-blue-800",
  purple: "bg-purple-900/50 text-purple-400 border border-purple-800",
};

export function Badge({ children, variant = "default", className }: {
  children: React.ReactNode;
  variant?: keyof typeof variants;
  className?: string;
}) {
  return (
    <span className={cn("inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium", variants[variant], className)}>
      {children}
    </span>
  );
}

const statusVariants: Record<string, keyof typeof variants> = {
  ACTIVE: "success",
  TRIALING: "info",
  PAST_DUE: "warning",
  CANCELLED: "danger",
  EXPIRED: "default",
  PAID: "success",
  OPEN: "info",
  DRAFT: "default",
  VOID: "default",
  UNCOLLECTIBLE: "danger",
  active: "success",
};

export function StatusBadge({ status }: { status: string }) {
  const variant = statusVariants[status] || "default";
  return <Badge variant={variant}>{status}</Badge>;
}
