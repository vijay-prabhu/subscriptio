"use client";

import { useEffect, useState } from "react";
import { api } from "@/lib/api";
import { formatCurrency, formatDate } from "@/lib/utils";
import { StatusBadge } from "@/components/ui/badge";
import type { Invoice, Tenant, PageResponse } from "@/types";

export default function InvoicesPage() {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const tenants = await api<PageResponse<Tenant>>("/api/v1/admin/tenants?page=0&size=1");
        if (tenants.content.length > 0) {
          const data = await api<PageResponse<Invoice>>(
            `/api/v1/admin/invoices?tenantId=${tenants.content[0].id}&page=0&size=50`
          );
          setInvoices(data.content);
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  if (loading) return <div className="text-zinc-500">Loading...</div>;

  return (
    <div className="space-y-6">
      <h2 className="text-lg font-semibold text-white">Invoices</h2>

      {invoices.length === 0 ? (
        <div className="text-zinc-500 text-sm">No invoices yet. Invoices are generated when billing cycles run.</div>
      ) : (
        <div className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-zinc-800">
                <th className="text-left px-4 py-3 text-zinc-500 font-medium">Invoice #</th>
                <th className="text-left px-4 py-3 text-zinc-500 font-medium">Status</th>
                <th className="text-left px-4 py-3 text-zinc-500 font-medium">Total</th>
                <th className="text-left px-4 py-3 text-zinc-500 font-medium">Due Date</th>
                <th className="text-left px-4 py-3 text-zinc-500 font-medium">Period</th>
              </tr>
            </thead>
            <tbody>
              {invoices.map((inv) => (
                <tr key={inv.id} className="border-b border-zinc-800/50 hover:bg-zinc-800/30">
                  <td className="px-4 py-3 text-white font-medium">{inv.invoiceNumber}</td>
                  <td className="px-4 py-3"><StatusBadge status={inv.status} /></td>
                  <td className="px-4 py-3 text-zinc-400">{formatCurrency(inv.total)}</td>
                  <td className="px-4 py-3 text-zinc-400">{formatDate(inv.dueDate)}</td>
                  <td className="px-4 py-3 text-zinc-400">
                    {formatDate(inv.periodStart)} - {formatDate(inv.periodEnd)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
